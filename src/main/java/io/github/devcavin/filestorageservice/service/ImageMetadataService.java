package io.github.devcavin.filestorageservice.service;

import io.github.devcavin.filestorageservice.config.ImageStorageProperties;
import io.github.devcavin.filestorageservice.dto.ImageMetadataResponse;
import io.github.devcavin.filestorageservice.model.ImageMetadata;
import io.github.devcavin.filestorageservice.repository.ImageMetadataRepository;
import org.springframework.core.io.Resource;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
public class ImageMetadataService {
    private final ImageStorageProperties imageStorageProperties;
    private final ImageMetadataRepository imageMetadataRepository;
    private final LocalImageStorageService localImageStorageService;


    public ImageMetadataService(ImageStorageProperties imageStorageProperties, ImageMetadataRepository imageMetadataRepository, LocalImageStorageService localImageStorageService) {
        this.imageStorageProperties = imageStorageProperties;
        this.imageMetadataRepository = imageMetadataRepository;
        this.localImageStorageService = localImageStorageService;
    }

    public ImageMetadataResponse upload(MultipartFile file, String ownerId) throws IOException {
        validateImageFile(file);

        String storagePath;

        try (InputStream inputStream = file.getInputStream()) {
            storagePath = localImageStorageService.storeImage(inputStream, file.getOriginalFilename());
        }

        ImageMetadata metadata = new ImageMetadata(
                ObjectId.get(),
                file.getOriginalFilename(),
                storagePath,
                file.getContentType(),
                ownerId,
                file.getSize(),
                Instant.now(),
                Instant.now()
        );

        // Save to database here
        imageMetadataRepository.save(metadata);

        return ImageMetadataResponse.from(metadata);
    }

    public ImageMetadata imageById(String imageId) throws FileNotFoundException {
        ObjectId id = new ObjectId(imageId);

        ImageMetadata image = imageMetadataRepository.findById(id).orElse(null);

        if  (image == null) {
            throw new FileNotFoundException("Image not found");
        }

        return image;
    }

    public Resource getImageResource(String imageId) throws IOException {
        ImageMetadata metadata = imageById(imageId);

        return localImageStorageService.getFileResource(metadata.storageName());
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !imageStorageProperties.allowedMimeTypes().contains(mimeType)) {
            throw new IllegalArgumentException("Invalid mime type");
        }
    }
}
