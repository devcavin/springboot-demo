package io.github.devcavin.filestorageservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "app.file-storage-service")
@EnableConfigurationProperties(ImageStorageProperties.class)
@Component
public record ImageStorageProperties(
        String basePath,
        Set<String> allowedMimeTypes
) {
    public ImageStorageProperties() {
        this(
                "/images",
                new HashSet<>(Set.of("png", "jpg", "jpeg", "gif", "bmp", "webp", "svg"))
        );
    }
}
