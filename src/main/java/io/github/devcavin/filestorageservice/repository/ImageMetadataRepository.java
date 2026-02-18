package io.github.devcavin.filestorageservice.repository;

import io.github.devcavin.filestorageservice.model.ImageMetadata;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageMetadataRepository extends MongoRepository<ImageMetadata, ObjectId> { }
