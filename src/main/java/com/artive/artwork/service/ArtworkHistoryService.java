package com.artive.artwork.service;

import com.artive.artwork.dto.ArtworkHistoryRequest;
import com.artive.artwork.dto.ArtworkHistoryResponseDto;
import com.artive.artwork.entity.Artwork;
import com.artive.artwork.entity.ArtworkHistory;
import com.artive.artwork.entity.ArtworkHistoryImage;
import com.artive.artwork.repository.ArtworkHistoryImageRepository;
import com.artive.artwork.repository.ArtworkHistoryRepository;
import com.artive.artwork.repository.ArtworkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkHistoryService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkHistoryRepository historyRepository;
    private final ArtworkHistoryImageRepository imageRepository;

    /**
     * 히스토리 등록
     */
    @Transactional
    public ArtworkHistoryResponseDto createHistory(Long artworkId, ArtworkHistoryRequest request) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Artwork not found"));

        ArtworkHistory history = ArtworkHistory.builder()
                .artwork(artwork)
                .content(request.getContent())
                .createdAt(LocalDateTime.now().toString())
                .updatedAt(LocalDateTime.now().toString())
                .build();

        historyRepository.save(history);

        List<ArtworkHistoryImage> imageEntities = request.getImageUrls().stream()
                .map(url -> ArtworkHistoryImage.builder()
                        .artworkHistory(history)
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList());

        imageRepository.saveAll(imageEntities);

        return toDto(history, imageEntities);
    }

    /**
     * 작품별 히스토리 전체 조회
     */
    public List<ArtworkHistoryResponseDto> getHistoriesByArtwork(Long artworkId) {
        List<ArtworkHistory> histories = historyRepository.findByArtworkId(artworkId);

        return histories.stream()
                .map(h -> {
                    List<ArtworkHistoryImage> images = imageRepository.findByArtworkHistory(h);
                    return toDto(h, images);
                })
                .collect(Collectors.toList());
    }

    /**
     * 단일 히스토리 조회
     */
    public ArtworkHistoryResponseDto getHistoryById(Long historyId) {
        ArtworkHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));
        List<ArtworkHistoryImage> images = imageRepository.findByArtworkHistory(history);

        return toDto(history, images);
    }

    /**
     * 히스토리 수정
     */
    @Transactional
    public ArtworkHistoryResponseDto updateHistory(Long historyId, ArtworkHistoryRequest request) {
        ArtworkHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        history.setContent(request.getContent());
        history.setUpdatedAt(LocalDateTime.now().toString());
        historyRepository.save(history);

        // 기존 이미지 제거 후 새 이미지 등록
        imageRepository.deleteByArtworkHistory(history);
        List<ArtworkHistoryImage> newImages = request.getImageUrls().stream()
                .map(url -> ArtworkHistoryImage.builder()
                        .artworkHistory(history)
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList());
        imageRepository.saveAll(newImages);

        return toDto(history, newImages);
    }

    /**
     * 히스토리 삭제
     */
    @Transactional
    public void deleteHistory(Long historyId) {
        ArtworkHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        imageRepository.deleteByArtworkHistory(history);
        historyRepository.delete(history);
    }

    /**
     * Entity -> DTO 변환
     */
    private ArtworkHistoryResponseDto toDto(ArtworkHistory history, List<ArtworkHistoryImage> images) {
        List<String> imageUrls = images.stream()
                .map(ArtworkHistoryImage::getImageUrl)
                .collect(Collectors.toList());

        return ArtworkHistoryResponseDto.builder()
                .id(history.getId())
                .content(history.getContent())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .imageUrls(imageUrls)
                .build();
    }
}
