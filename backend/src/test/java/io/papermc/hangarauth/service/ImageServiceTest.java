package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.custom.ImageConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ImageServiceTest {

    private ImageService imageService;

    @BeforeEach
    public void setUp() throws IOException {
        this.imageService = new ImageService(new ImageConfig(0.35f, 256));
        Files.createDirectories(Path.of("target/test-images"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test.bmp",
            "test.gif",
            "test.jpg",
            "test.png",
            "test.webp",
            "transparent.png",
    })
    void test(String file) throws IOException {
        final byte[] input = this.getClass().getClassLoader().getResourceAsStream("test-images/" + file).readAllBytes();
        final byte[] output = this.imageService.convertAndOptimize(input);
        Files.write(Path.of("target/test-images/" + file), output);
    }
}
