package io.github.devcavin.filestorageservice.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "image_metadata")
public record ImageMetadata(
        @Id ObjectId id,
        String imageName,
        String storageName,
        String mimeType,
        String ownerId,
        Long size,
        Instant createdAt,
        Instant updatedAt
        ) { }
