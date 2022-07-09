package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties("auth.image")
public class ImageConfig {
    private int interval;
    private int threads;
    private float quality;
    private float qualityWebp;
    private int size;
    private Path workFolder;

    public int interval() {
        return interval;
    }

    public int threads() {
        return threads;
    }

    public float quality() {
        return quality;
    }

    public float qualityWebp() {
        return qualityWebp;
    }

    public int size() {
        return size;
    }

    public Path workFolder() {
        return workFolder;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public void setQualityWebp(float qualityWebp) {
        this.qualityWebp = qualityWebp;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWorkFolder(Path workFolder) {
        this.workFolder = workFolder;
    }
}
