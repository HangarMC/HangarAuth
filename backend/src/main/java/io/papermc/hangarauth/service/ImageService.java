package io.papermc.hangarauth.service;

import com.luciad.imageio.webp.WebPWriteParam;
import io.papermc.hangarauth.config.CacheConfig;
import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.file.FileService;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ImageService {

    private static final MediaType WEBP = new MediaType("image", "webp");
    private static final String HEADER = "X-Hangar-Optimized";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageConfig config;
    private final FileService fileService;
    private final String imagesFolder;

    private final LinkedBlockingQueue<ImageToOptimize> workerQueue = new LinkedBlockingQueue<>();
    private final Cache hashCache;

    @Autowired
    public ImageService(final ImageConfig config, final CacheManager cacheManager, final FileService fileService) {
        this.config = config;
        this.fileService = fileService;
        this.imagesFolder = fileService.resolve(fileService.getRoot(), "images");

        this.hashCache = cacheManager.getCache(CacheConfig.HASH_CACHE);

        final ExecutorService service = Executors.newFixedThreadPool(config.threads());
        service.submit(() -> {
            ImageToOptimize image = null;
            while (true) {
                try {
                    this.optimize(image = this.workerQueue.take());
                    Thread.sleep(config.interval());
                } catch (final Exception ex) {
                    LOGGER.warn("Error while optimizing image {}: {} {} ", image, ex.getClass().getName(),
                        ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    public String getCdnPathFromFile(final String origFile, final HttpServletRequest request, final HttpServletResponse response) {
        return this.getCdnPath(this.fileService.getDownloadUrl(origFile),
                (content, hash) -> this.workerQueue.add(new ImageToOptimize(origFile, null, content, hash)),
                () -> this.getContentFromPath(origFile),
            () -> this.getHash(origFile, () -> this.getContentFromPath(origFile)),
            request, response
        );
    }

    public String getCdnPathFromUrl(final String origUrl, final HttpServletRequest request, final HttpServletResponse response) {
        return this.getCdnPath(origUrl,
                (content, hash) -> this.workerQueue.add(new ImageToOptimize(null, origUrl, content, hash)),
                () -> this.getContentFromUrl(origUrl),
             () -> this.getHash(origUrl, () -> this.getContentFromUrl(origUrl)),
             request, response
        );
    }

    private String getCdnPath(final String orig, final BiConsumer<byte[], String> queueUp, final Supplier<byte[]> contentSupplier, final Supplier<String> hashSupplier, final HttpServletRequest request, final HttpServletResponse response) {
        if ("true".equals(request.getParameter("orig"))) {
            response.setHeader(HEADER, "disabled");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return orig;
        }

        final String hash = hashSupplier.get();
        if (hash == null) {
            response.setHeader(HEADER, "error");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return orig;
        }
        final boolean supportsWebp = this.supportsWebp(request);
        final String optimizedFilePath = this.getOptimizedFilePath(hash, supportsWebp);
        if (optimizedFilePath == null) {
            final byte[] bytes = contentSupplier.get();
            if (bytes.length == 0) {
                response.setHeader(HEADER, "error");
            } else {
                queueUp.accept(bytes, hash);
                response.setHeader(HEADER, "queued");
            }
            return orig;
        } else {
            response.setHeader(HEADER, hash);
            return this.fileService.getDownloadUrl(optimizedFilePath);
        }
    }

    public byte[] getImageFromFile(final String origFile, final HttpServletRequest request, final HttpServletResponse response) {
        return this.getImage(
            (content, hash) -> this.workerQueue.add(new ImageToOptimize(origFile, null, content, hash)),
            () -> this.getContentFromPath(origFile),
            () -> this.getHash(origFile, () -> this.getContentFromPath(origFile)),
            request, response);
    }

    public byte[] getImageFromUrl(final String origUrl, final HttpServletRequest request, final HttpServletResponse response) {
        return this.getImage(
            (content, hash) -> this.workerQueue.add(new ImageToOptimize(null, origUrl, content, hash)),
            () -> this.getContentFromUrl(origUrl),
            () -> this.getHash(origUrl, () -> this.getContentFromUrl(origUrl)),
            request, response);
    }

    private byte[] getImage(final BiConsumer<byte[], String> queueUp, final Supplier<byte[]> contentSupplier, final Supplier<String> hashSupplier, final HttpServletRequest request, final HttpServletResponse response) {
        if ("true".equals(request.getParameter("orig"))) {
            response.setHeader(HEADER, "disabled");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return contentSupplier.get();
        }

        final String hash = hashSupplier.get();
        if (hash == null) {
            response.setHeader(HEADER, "error");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return contentSupplier.get();
        }
        final boolean supportsWebp = this.supportsWebp(request);
        final String optimizedFilePath = this.getOptimizedFilePath(hash, supportsWebp);
        if (optimizedFilePath != null) {
            final byte[] bytes = this.getContentFromPath(optimizedFilePath);
            response.setHeader(HEADER, hash);
            if (supportsWebp) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, WEBP.toString());
            } else {
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            }
            return bytes;
        } else {
            final byte[] bytes = contentSupplier.get();
            if (bytes.length == 0) {
                response.setHeader(HEADER, "error");
            } else {
                queueUp.accept(bytes, hash);
                response.setHeader(HEADER, "orig");
            }
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return bytes;
        }
    }

    private boolean supportsWebp(final HttpServletRequest request) {
        final List<MediaType> acceptedMediaTypes = MediaType.parseMediaTypes(request.getHeader("Accept"));
        for (final MediaType acceptedMediaType : acceptedMediaTypes) {
            if (acceptedMediaType.includes(WEBP)) {
                return true;
            }
        }

        return false;
    }

    private String getOptimizedFilePath(final String hash, final boolean webp) {
        final String folderName = hash.substring(0, 2);
        final String folderPath = this.fileService.resolve(this.imagesFolder, folderName);

        final String imagePath;
        if (webp) {
            imagePath = this.fileService.resolve(folderPath, hash + ".webp");
        } else {
            imagePath = this.fileService.resolve(folderPath,hash);
        }

        if (this.fileService.exists(imagePath)) {
            return imagePath;
        } else {
            return null;
        }
    }

    private byte[] getContentFromPath(final String path) {
        try {
            return this.fileService.bytes(path);
        } catch (final IOException e) {
            LOGGER.warn("Couldn't read file {}", path, e);
            return new byte[0];
        }
    }

    private byte[] getContentFromUrl(final String url) {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5 * 1000);
            return conn.getInputStream().readAllBytes();
        } catch (final IOException e) {
            LOGGER.warn("Couldn't read url {}", url, e);
            return new byte[0];
        }
    }

    private String getHash(final String cacheKey, final Supplier<byte[]> contentSupplier) {
        final String cachedHash = this.hashCache.get(cacheKey, String.class);
        if (cachedHash != null) {
            return cachedHash;
        }

        try {
            final byte[] hash = MessageDigest.getInstance("MD5").digest(contentSupplier.get());
            final String hex = DatatypeConverter.printHexBinary(hash);
            this.hashCache.put(cacheKey, hex);
            return hex;
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("error while hashing ", e);
            return null;
        }
    }

    public void evictCache(final String key) {
        // TODO there is a bug here, if we change the avatar, we evict the avatar, but not all project icons that might fall back to the users avatar
        this.hashCache.evict(key);
    }

    private void optimize(final ImageToOptimize image) throws IOException {
        // construct folder
        final String folderName = image.hash().substring(0, 2);
        final String folderPath = this.fileService.resolve(this.imagesFolder, folderName);
        // optimize
        this.optimizeAndWrite(image.content(), this.fileService.resolve(folderPath, image.hash() + ".jpg"), false);
        // webp
        this.optimizeAndWrite(image.content(), this.fileService.resolve(folderPath, image.hash() + ".webp"), true);
    }

    private void optimizeAndWrite(final byte[] imageBytes, final String imagePath, final boolean webp)
        throws IOException {
        if (!this.fileService.exists(imagePath)) {
            final byte[] optimzed = this.optimizeBytes(imageBytes, webp);
            try {
                this.fileService.write(new ByteArrayInputStream(optimzed), imagePath, webp ? "image/webp" : MediaType.IMAGE_JPEG_VALUE);
            } catch (final IOException e) {
                LOGGER.warn("Error while writing file {}", imagePath, e);
            }
        }
    }

    private byte[] optimizeBytes(final byte[] imageBytes, final boolean webp) throws IOException {
        final InputStream in = new ByteArrayInputStream(imageBytes);
        final BufferedImage bufferedImage = ImageIO.read(in);
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(webp ? WEBP.toString() : MediaType.IMAGE_JPEG_VALUE);
        if (writers.hasNext()) {
            return this.optimizeImage(writers.next(), bufferedImage, webp);
        } else {
            LOGGER.warn("No writer found! (webp {})", webp);
            return imageBytes;
        }
    }

    private byte[] optimizeImage(final ImageWriter writer, BufferedImage bufferedImage, final boolean webp)
        throws IOException {
        bufferedImage = this.scale(bufferedImage, this.config.size(), this.config.size());

        final ImageWriteParam params;
        if (webp) {
            params = new WebPWriteParam(writer.getLocale());
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType(params.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            params.setCompressionQuality(this.config.qualityWebp());
        } else {
            params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(this.config.quality());
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        try {
            final IIOImage optimizedImage = new IIOImage(bufferedImage, null, null);
            writer.write(null, optimizedImage, params);
        } catch (final IIOException ex) {
            if (!ex.getMessage().contains("Bogus input colorspace")) {
                throw ex;
            }
            // draw to get rid of alpha
            final BufferedImage result = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            // reset the writer
            writer.reset();
            writer.setOutput(ios);
            // then attempt optimization again
            final IIOImage optimizedImage = new IIOImage(result, null, null);
            writer.write(null, optimizedImage, params);
        }

        writer.dispose();
        ios.flush();

        return out.toByteArray();
    }

    private BufferedImage scale(final BufferedImage originalImage, final int height, final int width) {
        final AffineTransform af = new AffineTransform();
        af.scale((double) width / originalImage.getWidth(), (double) height / originalImage.getHeight());
        final AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage rescaledImage = new BufferedImage(width, height, originalImage.getType());
        rescaledImage = operation.filter(originalImage, rescaledImage);

        return rescaledImage;
    }

    record ImageToOptimize(
        String origFile,
        String origUrl,
        byte[] content,
        String hash
    ) {

    }
}
