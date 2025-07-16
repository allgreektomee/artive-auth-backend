package com.artive.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    private String email;
    private String password;
    private String name;


    @Column(unique = true, nullable = false)
    private String slug; // 사용자 도메인 경로

    private String role;
    private String profileImage;
    private String bio;
    private String createdAt;

    @Builder.Default
    private boolean isVerified = false;

    // === UserDetails 구현 ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + role); // ex: ROLE_ARTIST
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;  // 인증된 사용자만 활성
    }
}
