package com.artive.artwork.controller;

import com.artive.artwork.dto.ApiResponse;
import com.artive.artwork.dto.ArtworkHistoryRequest;
import com.artive.artwork.dto.ArtworkRequest;
import com.artive.artwork.dto.ArtworkResponseDto;
import com.artive.artwork.service.ArtworkService;
import com.artive.artwork.entity.Artwork;
import com.artive.auth.entity.User;
import com.artive.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    /**
     * 작품 생성
     * @param request ArtworkRequest 객체 (제목, 설명 등 포함)
     * param user 현재 로그인한 사용자
     * @return 생성된 Artwork 객체
     */
    @PostMapping
    public ResponseEntity<Artwork> createArtwork(
            @RequestBody ArtworkRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Artwork created = artworkService.createArtwork(request, userDetails.getUser());
        return ResponseEntity.ok(created);
    }

    /**
     * 썸네일 단일 업로드
     * @param file MultipartFile 객체
     * @return 업로드된 이미지 URL
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadThumbnailOnly(@RequestPart("file") MultipartFile file) {
        String imageUrl = artworkService.uploadImageOnly(file);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 히스토리용 이미지 다중 업로드
     * @param files MultipartFile 리스트
     * @return 업로드된 이미지 URL 리스트
     */
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadMultiple(
            @RequestPart("files") List<MultipartFile> files) {
        List<String> urls = artworkService.uploadMultipleImages(files);
        return ResponseEntity.ok(urls);
    }

    /**
     * 작품 삭제
     * @param id Artwork ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteArtwork(@PathVariable Long id) {
        artworkService.deleteArtwork(id);
        return ResponseEntity.ok(new ApiResponse("삭제 완료"));
    }

    /**
     * 썸네일 이미지 수정
     * @param id Artwork ID
     * @param newThumbnail 새 썸네일 이미지
     * @return 새로운 썸네일 URL
     */
    @PutMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateThumbnail(
            @PathVariable Long id,
            @RequestPart("thumbnail") MultipartFile newThumbnail) {
        String newUrl = artworkService.updateThumbnail(id, newThumbnail);
        return ResponseEntity.ok(newUrl);
    }

    /**
     * 작품 목록 조회
     * @param userId (선택) 특정 유저의 작품만 조회할 경우
     * @return Artwork 리스트
     */
    @GetMapping
    public ResponseEntity<List<ArtworkResponseDto>> getArtworks(
            @RequestParam(required = false) Long userId
    ) {
        List<ArtworkResponseDto> list = artworkService.getAllArtworks(userId);
        return ResponseEntity.ok(list);
    }
    /**
     * 작품 상세 조회
     * @param id 작품 ID
     * @return Artwork 객체
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtworkResponseDto> getArtworkById(@PathVariable Long id) {
        ArtworkResponseDto dto = artworkService.getArtworkById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 작품 수정
     * @param id 작품 ID
     * @param request 수정할 제목/설명
     * @return 수정된 Artwork
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArtworkResponseDto> updateArtwork(
            @PathVariable Long id,
            @RequestBody ArtworkRequest request
    ) {
        ArtworkResponseDto updated = artworkService.updateArtwork(id, request);
        return ResponseEntity.ok(updated);
    }




}
