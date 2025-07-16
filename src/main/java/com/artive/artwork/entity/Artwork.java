package com.artive.artwork.entity;

import com.artive.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    // 대표 이미지 URL 필드 추가
    private String thumbnailUrl;

    private String createdAt;

    private String updatedAt;

    // created_at, updated_at 등 추가 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
