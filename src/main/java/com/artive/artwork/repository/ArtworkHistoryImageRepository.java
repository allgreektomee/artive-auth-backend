package com.artive.artwork.repository;

import com.artive.artwork.entity.ArtworkHistory;
import com.artive.artwork.entity.ArtworkHistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtworkHistoryImageRepository extends JpaRepository<ArtworkHistoryImage, Long> {
    /**
     * 특정 히스토리에 연결된 모든 이미지 조회
     */
    List<ArtworkHistoryImage> findByArtworkHistory(ArtworkHistory history);

    /**
     * 특정 히스토리에 연결된 이미지 전부 삭제
     */
    void deleteByArtworkHistory(ArtworkHistory history);
}