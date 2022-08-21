package io.papermc.hangarauth.service;

import com.luciad.imageio.webp.WebPWriteParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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

import io.papermc.hangarauth.config.CacheConfig;
import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.file.FileService;

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
    public ImageService(ImageConfig config, CacheManager cacheManager, FileService fileService) {
        this.config = config;
        this.fileService = fileService;
        this.imagesFolder = fileService.resolve(fileService.getRoot(), "images");

        hashCache = cacheManager.getCache(CacheConfig.HASH_CACHE);

        ExecutorService service = Executors.newFixedThreadPool(config.threads());
        service.submit(() -> {
            ImageToOptimize image = null;
            while (true) {
                try {
                    optimize(image = workerQueue.take());
                    Thread.sleep(config.interval());
                } catch (Exception ex) {
                    LOGGER.warn("Error while optimizing image {}: {} {} ", image, ex.getClass().getName(),
                        ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    public byte[] getImageFromFile(String origFile, HttpServletRequest request, HttpServletResponse response) {
        return getImage(
            (content, hash) -> workerQueue.add(new ImageToOptimize(origFile, null, content, hash)),
            () -> getContentFromPath(origFile),
            () -> getHash(origFile, () -> getContentFromPath(origFile)),
            request, response);
    }

    public byte[] getImageFromUrl(String origUrl, HttpServletRequest request, HttpServletResponse response) {
        return getImage(
            (content, hash) -> workerQueue.add(new ImageToOptimize(null, origUrl, content, hash)),
            () -> getContentFromUrl(origUrl),
            () -> getHash(origUrl, () -> getContentFromUrl(origUrl)),
            request, response);
    }

    private byte[] getImage(BiConsumer<byte[], String> queueUp, Supplier<byte[]> contentSupplier, Supplier<String> hashSupplier, HttpServletRequest request, HttpServletResponse response) {
        if ("true".equals(request.getParameter("orig"))) {
            response.setHeader(HEADER, "disabled");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return contentSupplier.get();
        }

        String hash = hashSupplier.get();
        if (hash == null) {
            response.setHeader(HEADER, "error");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return contentSupplier.get();
        }
        boolean supportsWebp = supportsWebp(request);
        byte[] optimizedFile = getOptimizedFile(hash, supportsWebp);
        if (optimizedFile.length == 0) {
            byte[] bytes = contentSupplier.get();
            if (bytes.length == 0) {
                response.setHeader(HEADER, "error");
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
                return bytes;
            }
            queueUp.accept(bytes, hash);
            response.setHeader(HEADER, "orig");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            return bytes;
        } else {
            response.setHeader(HEADER, hash);
            if (supportsWebp) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, WEBP.toString());
            } else {
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            }
            return optimizedFile;
        }
    }

    private boolean supportsWebp(HttpServletRequest request) {
        List<MediaType> acceptedMediaTypes = MediaType.parseMediaTypes(request.getHeader("Accept"));
        for (MediaType acceptedMediaType : acceptedMediaTypes) {
            if (acceptedMediaType.includes(WEBP)) {
                return true;
            }
        }

        return false;
    }

    private byte[] getOptimizedFile(String hash, boolean webp) {
        String folderName = hash.substring(0, 2);
        String folderPath = fileService.resolve(imagesFolder, folderName);

        String imagePath;
        if (webp) {
            imagePath = fileService.resolve(folderPath, hash + ".webp");
        } else {
            imagePath = fileService.resolve(folderPath,hash);
        }

        if (fileService.exists(imagePath)) {
            return getContentFromPath(imagePath);
        } else {
            return new byte[0];
        }
    }

    private byte[] getContentFromPath(String path) {
        try {
            return fileService.bytes(path);
        } catch (IOException e) {
            LOGGER.warn("Couldn't read file {}", path, e);
            return new byte[0];
        }
    }

    private byte[] getContentFromUrl(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5 * 1000);
            return conn.getInputStream().readAllBytes();
        } catch (IOException e) {
            LOGGER.warn("Couldn't read url {}", url, e);
            return new byte[0];
        }
    }

    private String getHash(String cacheKey, Supplier<byte[]> contentSupplier) {
        String cachedHash = hashCache.get(cacheKey, String.class);
        if (cachedHash != null) {
            return cachedHash;
        }

        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(contentSupplier.get());
            String hex = DatatypeConverter.printHexBinary(hash);
            hashCache.put(cacheKey, hex);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("error while hashing ", e);
            return null;
        }
    }

    public void evictCache(String key) {
        hashCache.evict(key);
    }

    private void optimize(ImageToOptimize image) throws IOException {
        // construct folder
        String folderName = image.hash().substring(0, 2);
        String folderPath = fileService.resolve(imagesFolder, folderName);
        // optimize
        optimizeAndWrite(image.content(), fileService.resolve(folderPath, image.hash() + ".jpg"), false);
        // webp
        optimizeAndWrite(image.content(), fileService.resolve(folderPath, image.hash() + ".webp"), true);
    }

    private void optimizeAndWrite(byte[] imageBytes, String imagePath, boolean webp)
        throws IOException {
        if (!fileService.exists(imagePath)) {
            byte[] optimzed = optimizeBytes(imageBytes, webp);
            try {
                fileService.write(new ByteArrayInputStream(optimzed), imagePath);
            } catch (IOException e) {
                LOGGER.warn("Error while writing file {}", imagePath, e);
            }
        }
    }

    private byte[] optimizeBytes(byte[] imageBytes, boolean webp) throws IOException {
        InputStream in = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(in);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(webp ? WEBP.toString() : MediaType.IMAGE_JPEG_VALUE);
        if (writers.hasNext()) {
            return optimizeImage(writers.next(), bufferedImage, webp);
        } else {
            LOGGER.warn("No writer found! (webp {})", webp);
            return imageBytes;
        }
    }

    private byte[] optimizeImage(ImageWriter writer, BufferedImage bufferedImage, boolean webp)
        throws IOException {
        bufferedImage = scale(bufferedImage, config.size(), config.size());

        ImageWriteParam params;
        if (webp) {
            params = new WebPWriteParam(writer.getLocale());
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType(params.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            params.setCompressionQuality(config.qualityWebp());
        } else {
            params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(config.quality());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        try {
            IIOImage optimizedImage = new IIOImage(bufferedImage, null, null);
            writer.write(null, optimizedImage, params);
        } catch (IIOException ex) {
            if (!ex.getMessage().contains("Bogus input colorspace")) {
                throw ex;
            }
            // draw to get rid of alpha
            BufferedImage result = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            // reset the writer
            writer.reset();
            writer.setOutput(ios);
            // then attempt optimization again
            IIOImage optimizedImage = new IIOImage(result, null, null);
            writer.write(null, optimizedImage, params);
        }

        writer.dispose();
        ios.flush();

        return out.toByteArray();
    }

    private BufferedImage scale(BufferedImage originalImage, int height, int width) {
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
