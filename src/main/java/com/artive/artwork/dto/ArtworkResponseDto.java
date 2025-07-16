package com.artive.artwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkResponseDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String createdAt;
    private String updatedAt;
    private Long userId; // ✅ 추가
}
