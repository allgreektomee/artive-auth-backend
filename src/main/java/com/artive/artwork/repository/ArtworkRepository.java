package com.artive.artwork.repository;

import com.artive.artwork.entity.Artwork;
import com.artive.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    List<Artwork> findByUser(User user);
    List<Artwork> findByUserId(Long userId);

}
