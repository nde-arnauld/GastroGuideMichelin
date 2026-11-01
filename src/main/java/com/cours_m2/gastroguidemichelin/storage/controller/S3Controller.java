package com.cours_m2.gastroguidemichelin.storage.controller;

import com.cours_m2.gastroguidemichelin.storage.dto.StorageDTO;
import com.cours_m2.gastroguidemichelin.storage.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/buckets")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getBucketNames() {
        return this.s3Service.getBucketNames();
    }

    @Operation(
            summary = "Génère une URL présignée d'envoie de fichier vers S3",
            description = "Cette méthode génère une URL présignée valide pendant une durée limitée pour permettre l'envoi d'un fichier vers un bucket S3."
    )
    @PostMapping("/presigned-url")
    @ResponseStatus(HttpStatus.OK)
    public StorageDTO.UploadResponse generatePresignedUrl(@Valid @RequestBody StorageDTO.UploadRequest request)
    {
        String imageKey = this.s3Service.generateObjectKey(request.getResourceType(), request.getFilename());
        String uploadUrl = this.s3Service.generatePresignedUploadUrl(request.getResourceType(), request.getContentType(), imageKey);
        return StorageDTO.UploadResponse
                .builder()
                .uploadUrl(uploadUrl)
                .imageKey(imageKey)
                .method("PUT")
                .build();
    }

    @Operation(summary = "Génère une URL présignée de lecture",
            description = "Cette méthode génère une URL présignée valide pendant une durée limitée pour permettre " +
                    "la lecture d'un fichier depuis un bucket S3.")
    @GetMapping("/download-url")
    @ResponseStatus(HttpStatus.OK)
    public StorageDTO.DownloadResponse generateDownloadUrl(
            @RequestParam String resourceType,
            @RequestParam String imageKey
    ) {
        String downloadUrl = this.s3Service.generatePresignedDownloadUrl(resourceType, imageKey);
        return StorageDTO.DownloadResponse.builder()
                .url(downloadUrl)
                .build();
    }
}
