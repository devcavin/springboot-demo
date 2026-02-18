package io.github.devcavin.filestorageservice.dto;

import io.github.devcavin.filestorageservice.model.ImageMetadata;

import java.time.Instant;

public record ImageMetadataResponse(
        String id,
        String imageName,
        String storageName,
        String mimeType,
        String ownerId,
        Long size,
        Instant createdAt,
        Instant updatedAt
) {

    public static ImageMetadataResponse from(ImageMetadata data) {
        return new ImageMetadataResponse(
                data.id().toHexString(),
                data.imageName(),
                data.storageName(),
                data.mimeType(),
                data.ownerId(),
                data.size(),
                data.createdAt(),
                data.updatedAt()
        );
    }
}

