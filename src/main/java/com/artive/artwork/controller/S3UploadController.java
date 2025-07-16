package com.artive.artwork.controller;

import com.artive.auth.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class S3UploadController {

    private final S3Uploader s3Uploader;

    @PostMapping("/thumbnail")
    public ResponseEntity<String> uploadThumbnail(@RequestPart MultipartFile file) throws IOException {
        String url = s3Uploader.upload(file, "artwork-thumbnail");
        return ResponseEntity.ok(url);
    }
}
