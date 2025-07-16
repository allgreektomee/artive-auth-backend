package com.artive.artwork.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkRequest {
    private String title;
    private String description;
    private String thumbnailUrl; // 프론트에서 업로드 후 받은 URL 전달
}
