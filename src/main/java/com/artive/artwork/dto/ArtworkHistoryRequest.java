package com.artive.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArtworkHistoryRequest {
    private Long artworkId;
    private String content;
    private List<String> imageUrls;
}
