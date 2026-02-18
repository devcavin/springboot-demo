package io.github.devcavin.filestorageservice.service;

import io.github.devcavin.filestorageservice.config.ImageStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class LocalImageStorageService {
    private final Path rootPath;

    public LocalImageStorageService(ImageStorageProperties imageStorageProperties) {
        this.rootPath = Paths.get(imageStorageProperties.basePath());
    }

    // store a file
    public String storeImage(InputStream inputStream, String imageName) throws IOException {
        LocalDate dateNow =  LocalDate.now();

        Path dateDirectory = rootPath.resolve(
                dateNow.getYear() + File.separator + String.format("%02d", dateNow.getMonthValue()) + String.format(
                        "%02d", dateNow.getDayOfMonth())
        );

        Files.createDirectories(dateDirectory);

        String extension = getFileExtension(imageName);
        String storedImageName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path filePath = dateDirectory.resolve(storedImageName);

        try(OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);) {
            StreamUtils.copy(inputStream, outputStream);
        }

        return rootPath.relativize(filePath).toString();
    }

    // retrieve the stored file resource
    public Resource getFileResource(String storedPath) throws IOException {
        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRootPath = filePath.toAbsolutePath().normalize();

        if (!filePath.startsWith(normalizedRootPath)) {
            throw new SecurityException("Access denied");
        }

        if (!Files.exists(filePath)) { throw  new FileNotFoundException("File not found"); }

        return new UrlResource(filePath.toUri());
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot + 1);
    }
}
