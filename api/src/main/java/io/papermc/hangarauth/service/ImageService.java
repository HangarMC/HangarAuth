package io.papermc.hangarauth.service;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.luciad.imageio.webp.WebPWriteParam;

import io.papermc.hangarauth.config.custom.ImageConfig;

@Component
public class ImageService {

    private static final MediaType WEBP = new MediaType("image", "webp");

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageConfig config;

    private final LinkedBlockingQueue<ImageToOptimize> workerQueue = new LinkedBlockingQueue<>();

    @Autowired
    public ImageService(ImageConfig config) {
        this.config = config;

        if (!Files.isDirectory(config.workFolder())) {
            try {
                Files.createDirectories(config.workFolder());
            } catch (IOException e) {
                LOGGER.error("Couldn't create work folder {}", config.workFolder());
            }
        }

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

    public byte[] getImage(Path origFile, HttpServletRequest request, HttpServletResponse response) {
        boolean supportsWebp = supportsWebp(request);

        byte[] content = getContent(origFile);
        if (content.length == 0) {
            return new byte[0];
        }
        if ("true".equals(request.getParameter("orig"))) {
            response.setHeader("X-Hangar-Optimized", "disabled");
            response.setHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE);
            return content; // disabled, return orig
        }
        String hash = getHash(content);
        if (hash == null) {
            response.setHeader("X-Hangar-Optimized", "error");
            response.setHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE);
            return content; // error, return orig
        }
        byte[] optimizedFile = getOptimizedFile(hash, supportsWebp);
        if (optimizedFile.length == 0) {
            queueUp(origFile, content, hash);
            response.setHeader("X-Hangar-Optimized", "orig");
            response.setHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE);
            return content; // no optimization available, return orig
        } else {
            response.setHeader("X-Hangar-Optimized", hash);
            if (supportsWebp) {
                response.setHeader("Content-Type", WEBP.toString());
            } else {
                response.setHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE);
            }
            return optimizedFile; // return optimized file
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
        Path folderPath = config.workFolder().resolve(folderName);
        if (!Files.isDirectory(folderPath)) {
            return new byte[0];
        }

        Path imagePath;
        if (webp) {
            imagePath = folderPath.resolve(hash + ".webp");
        } else {
            imagePath = folderPath.resolve(hash);
        }

        if (Files.isRegularFile(imagePath)) {
            return getContent(imagePath);
        } else {
            return new byte[0];
        }
    }

    private byte[] getContent(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            LOGGER.warn("Couldn't read file {}", path, e);
            return new byte[0];
        }
    }

    private String getHash(byte[] content) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(content);
            return DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("error while hashing ", e);
            return null;
        }
    }

    private void queueUp(Path origFile, byte[] content, String hash) {
        ImageToOptimize image = new ImageToOptimize(origFile, content, hash);
        workerQueue.add(image);
    }

    private void optimize(ImageToOptimize image) throws IOException {
        // construct folder
        String folderName = image.hash().substring(0, 2);
        Path folderPath = config.workFolder().resolve(folderName);
        if (!Files.isDirectory(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                LOGGER.warn("Error while creating folder {}", folderPath, e);
                return;
            }
        }
        // optimize
        optimizeAndWrite(image.content(), folderPath.resolve(image.hash() + ".jpg"), false);
        // webp
        optimizeAndWrite(image.content(), folderPath.resolve(image.hash() + ".webp"), true);
    }

    private void optimizeAndWrite(byte[] imageBytes, Path imagePath, boolean webp)
        throws IOException {
        if (!Files.isRegularFile(imagePath)) {
            byte[] optimzed = optimizeBytes(imageBytes, webp);
            try {
                Files.write(imagePath, optimzed, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

        IIOImage optimizedImage = new IIOImage(bufferedImage, null, null);
        writer.write(null, optimizedImage, params);
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
        Path origFile,
        byte[] content,
        String hash
    ) {

    }
}
