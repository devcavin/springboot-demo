package io.github.devcavin.filestorageservice.controller;

import io.github.devcavin.filestorageservice.dto.ImageMetadataResponse;
import io.github.devcavin.filestorageservice.model.ImageMetadata;
import io.github.devcavin.filestorageservice.service.ImageMetadataService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageMetadataController {
    private final ImageMetadataService imageMetadataService;


    public ImageMetadataController(ImageMetadataService imageMetadataService) {
        this.imageMetadataService = imageMetadataService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String ownerId = "045";
            ImageMetadataResponse metadata  = imageMetadataService.upload(file, ownerId);
            return ResponseEntity.status(HttpStatus.OK).body(metadata);
        }  catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("id") String imageId) {
        try {
            ImageMetadata metadata = imageMetadataService.imageById(imageId);
            Resource imageResource = imageMetadataService.getImageResource(imageId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.imageName() + "\"")
                    .contentType(MediaType.parseMediaType(metadata.mimeType()))
                    .contentLength(metadata.size())
                    .body(imageResource);
        } catch (IOException e)  {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
