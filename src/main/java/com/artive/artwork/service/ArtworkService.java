package com.artive.artwork.service;

import com.artive.artwork.dto.ArtworkRequest;
import com.artive.artwork.dto.ArtworkResponseDto;
import com.artive.artwork.dto.ArtworkUploadDto;
import com.artive.artwork.entity.Artwork;
import com.artive.artwork.repository.ArtworkRepository;
import com.artive.auth.entity.User;
import com.artive.auth.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final S3Uploader s3Uploader;

    /**
     * 작품 생성
     */
    public Artwork createArtwork(ArtworkRequest request, User user) {
        Artwork artwork = Artwork.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .createdAt(LocalDateTime.now().toString())
                .updatedAt(LocalDateTime.now().toString())
                .user(user)
                .build();
        return artworkRepository.save(artwork);
    }

    /**
     * 썸네일과 함께 작품 생성
     */
    public Artwork createArtworkWithImage(ArtworkUploadDto dto, MultipartFile thumbnail, User user) {
        try {
            String imageUrl = s3Uploader.upload(thumbnail, "artworks/thumbnails");
            Artwork artwork = Artwork.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .thumbnailUrl(imageUrl)
                    .createdAt(LocalDateTime.now().toString())
                    .updatedAt(LocalDateTime.now().toString())
                    .user(user)
                    .build();
            return artworkRepository.save(artwork);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    /**
     * 썸네일 1장 업로드
     */
    public String uploadImageOnly(MultipartFile file) {
        try {
            return s3Uploader.upload(file, "artwork");
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    /**
     * 이미지 여러 장 업로드
     */
    public List<String> uploadMultipleImages(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadImageToS3)
                .collect(Collectors.toList());
    }

    private String uploadImageToS3(MultipartFile file) {
        try {
            return s3Uploader.upload(file, "history");
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    /**
     * 작품 삭제 (S3 이미지 포함)
     */
    public void deleteArtwork(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
        if (artwork.getThumbnailUrl() != null) {
            s3Uploader.delete(artwork.getThumbnailUrl());
        }
        artworkRepository.delete(artwork);
    }

    /**
     * 썸네일 이미지 교체
     */
    public String updateThumbnail(Long id, MultipartFile newFile) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        if (artwork.getThumbnailUrl() != null) {
            s3Uploader.delete(artwork.getThumbnailUrl());
        }

        try {
            String newUrl = s3Uploader.upload(newFile, "artwork");
            artwork.setThumbnailUrl(newUrl);
            artwork.setUpdatedAt(LocalDateTime.now().toString());
            artworkRepository.save(artwork);
            return newUrl;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    /**
     * 전체 작품 또는 유저별 작품 조회
     */
    public List<ArtworkResponseDto> getAllArtworks(Long userId) {
        List<Artwork> artworks = (userId != null) ?
                artworkRepository.findByUserId(userId) :
                artworkRepository.findAll();

        return artworks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ArtworkResponseDto convertToDto(Artwork artwork) {
        return ArtworkResponseDto.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .thumbnailUrl(artwork.getThumbnailUrl())
                .createdAt(artwork.getCreatedAt())
                .updatedAt(artwork.getUpdatedAt())
                .userId(artwork.getUser() != null ? artwork.getUser().getId() : null)
                .build();
    }

    /**
     * 단일 작품 조회
     */
    public ArtworkResponseDto getArtworkById(Long id) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));
        return convertToDto(artwork);
    }

    /**
     * 작품 정보 수정
     */
    public ArtworkResponseDto updateArtwork(Long id, ArtworkRequest request) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        artwork.setTitle(request.getTitle());
        artwork.setDescription(request.getDescription());
        artwork.setUpdatedAt(LocalDateTime.now().toString());

        Artwork updated = artworkRepository.save(artwork);
        return convertToDto(updated);
    }
}
