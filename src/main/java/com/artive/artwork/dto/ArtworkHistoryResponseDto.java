package com.artive.artwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkHistoryResponseDto {
    private Long id;
    private String content;
    private String createdAt;
    private String updatedAt;
    private List<String> imageUrls;
}
