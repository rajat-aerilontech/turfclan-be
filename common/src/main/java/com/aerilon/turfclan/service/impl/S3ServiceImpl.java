package com.aerilon.turfclan.service.impl;

import com.aerilon.turfclan.exception.AwsRuntimeException;
import com.aerilon.turfclan.exception.ResourceNotFoundException;
import com.aerilon.turfclan.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile  file, String fileName, String folderName, boolean overwrite) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String sanitizedName = fileName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String key;
        if (overwrite) {
            key = String.format("%s/%s%s", folderName, sanitizedName, extension);
        } else {
            key = String.format("%s/%s_%s%s",
                    folderName,
                    UUID.randomUUID(),
                    sanitizedName,
                    extension
            );
        }
        log.info("Uploading file to S3: {}", key);
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (S3Exception e) {
            log.error("AWS S3 error during upload: {}", e.awsErrorDetails().errorMessage());
            throw new AwsRuntimeException("Failed to upload to S3", e);
        }
        return key;
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            log.info("Downloading file from S3: {}", key);
            return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()).asByteArray();
        } catch (S3Exception e) {
            log.error("Error downloading file {}: {}", key, e.awsErrorDetails().errorMessage());
            throw new ResourceNotFoundException("File not found or inaccessible in S3");
        }
    }

    @Override
    public List<String> listFiles() {
        ListObjectsV2Response result = s3Client.listObjectsV2(r -> r.bucket(bucketName));
        return result.contents().stream()
                .map(S3Object::key)
                .toList();
    }

    @Override
    public String preSignedUrl(String key, int expiryMinutes) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiryMinutes))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public void deleteFile(String key) {
        log.info("Deleting file from S3: {}", key);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }
}
