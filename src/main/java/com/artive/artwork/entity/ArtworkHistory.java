package com.artive.artwork.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 설명글
    private String mediaUrl; // 이미지 또는 동영상 링크

    private String createdAt;
    private String updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Builder.Default
    @OneToMany(mappedBy = "artworkHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtworkHistoryImage> images = new ArrayList<>();
}