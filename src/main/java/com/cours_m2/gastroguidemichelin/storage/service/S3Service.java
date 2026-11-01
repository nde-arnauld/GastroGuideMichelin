package com.cours_m2.gastroguidemichelin.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.restaurant-bucket}")
    private String bucketRestaurant;

    @Value("${cloud.aws.s3.evaluation-bucket}")
    private String bucketEvaluation;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public String generatePresignedUploadUrl(String resourceType, String contentType, String objectKey) {
        String bucketName = getBucketName(resourceType);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        String url = this.s3Presigner.presignPutObject(presignRequest)
                .url()
                .toString()
                .replace("%3B", ";")
                .replace("%2F", "/");
        System.out.println("Generated presigned upload URL: " + url);
        return url;
    }

    private String getBucketName(String resourceType) {
        return switch (resourceType.toLowerCase()) {
            case "restaurant" -> bucketRestaurant;
            case "evaluation" -> bucketEvaluation;
            default -> throw new IllegalArgumentException("Ce type de ressource est invalide: " + resourceType);
        };
    }

    public String generateObjectKey(String ressourceType, String filename) {
        String prefix = switch (ressourceType.toLowerCase()) {
            case "restaurant" -> "restaurant/";
            case "evaluation" -> "evaluation/";
            default -> throw new IllegalArgumentException("Ce type de ressource est invalide: " + ressourceType);
        };
        String extension = filename.substring(filename.lastIndexOf("."));
        return prefix + UUID.randomUUID() + extension;
    }

    public String generatePresignedDownloadUrl(String resourceType, String objectKey) {
        String bucketName = getBucketName(resourceType);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return this.s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString()
                .replace("%3B", ";")
                .replace("%2F", "/");
    }

    public List<String> getBucketNames() {
        return this.s3Client.listBuckets().buckets().stream()
                .map(Bucket::name)
                .toList();
    }

    public String uploadFile(String resourceType, MultipartFile file) throws IOException {
        String bucketName = getBucketName(resourceType);
        String objectKey = generateObjectKey(resourceType, file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        this.s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );
        return objectKey;
    }
}
