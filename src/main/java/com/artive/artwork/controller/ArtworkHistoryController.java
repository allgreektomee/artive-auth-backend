package com.artive.artwork.controller;

import com.artive.artwork.dto.ArtworkHistoryRequest;
import com.artive.artwork.dto.ArtworkHistoryResponseDto;
import com.artive.artwork.service.ArtworkHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/artworks/{artworkId}/histories")
public class ArtworkHistoryController {

    private final ArtworkHistoryService artworkHistoryService;

    /**
     * 히스토리 등록
     */
    @PostMapping
    public ResponseEntity<ArtworkHistoryResponseDto> createHistory(
            @PathVariable Long artworkId,
            @RequestBody ArtworkHistoryRequest request
    ) {
        ArtworkHistoryResponseDto response = artworkHistoryService.createHistory(artworkId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 작품의 모든 히스토리 조회
     */
    @GetMapping
    public ResponseEntity<List<ArtworkHistoryResponseDto>> getHistories(
            @PathVariable Long artworkId
    ) {
        List<ArtworkHistoryResponseDto> response = artworkHistoryService.getHistoriesByArtwork(artworkId);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 히스토리 조회
     */
    @GetMapping("/{historyId}")
    public ResponseEntity<ArtworkHistoryResponseDto> getHistory(
            @PathVariable Long artworkId,
            @PathVariable Long historyId
    ) {
        ArtworkHistoryResponseDto response = artworkHistoryService.getHistoryById(historyId);
        return ResponseEntity.ok(response);
    }

    /**
     * 히스토리 수정
     */
    @PutMapping("/{historyId}")
    public ResponseEntity<ArtworkHistoryResponseDto> updateHistory(
            @PathVariable Long artworkId,
            @PathVariable Long historyId,
            @RequestBody ArtworkHistoryRequest request
    ) {
        ArtworkHistoryResponseDto response = artworkHistoryService.updateHistory(historyId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 히스토리 삭제
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(
            @PathVariable Long artworkId,
            @PathVariable Long historyId
    ) {
        artworkHistoryService.deleteHistory(historyId);
        return ResponseEntity.ok("히스토리 삭제 완료");
    }
}
