// S3Uploader.java
package com.artive.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private S3Client s3Client;

    @PostConstruct
    public void initialize() {
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String upload(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path tempFilePath = saveTempFile(file);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, tempFilePath);
        } finally {
            Files.deleteIfExists(tempFilePath);
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    private Path saveTempFile(MultipartFile multipartFile) throws IOException {
        Path tempPath = Files.createTempFile(UUID.randomUUID().toString(), null);
        multipartFile.transferTo(tempPath);
        return tempPath;
    }

    public void delete(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    private String extractKeyFromUrl(String url) {
        String[] prefixes = {"user-thumbnail/", "artwork/", "history/"};

        for (String prefix : prefixes) {
            int idx = url.indexOf(prefix);
            if (idx != -1) {
                return url.substring(idx); // prefix부터 끝까지 추출
            }
        }

        throw new RuntimeException("지원하지 않는 S3 이미지 URL 형식입니다: " + url);
    }



}
