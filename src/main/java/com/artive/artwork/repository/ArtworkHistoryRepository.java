package com.artive.artwork.repository;


import com.artive.artwork.entity.ArtworkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkHistoryRepository extends JpaRepository<ArtworkHistory, Long> {
    // 필요하면 여기에 커스텀 쿼리 메서드 추가

    /**
     * 특정 Artwork ID에 해당하는 모든 히스토리 조회
     */
    List<ArtworkHistory> findByArtworkId(Long artworkId);

}