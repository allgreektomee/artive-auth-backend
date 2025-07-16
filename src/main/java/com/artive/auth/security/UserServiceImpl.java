package com.artive.auth.security;

import com.artive.auth.dto.SignupRequestDto;
import com.artive.auth.entity.User;
import com.artive.auth.repository.UserRepository;
import com.artive.auth.service.S3Uploader;
import com.artive.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User signup(SignupRequestDto dto, String thumbnailUrl) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String slug = "user_"+ UUID.randomUUID().toString().substring(0, 4) + UUID.randomUUID().toString().substring(0, 4);
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .thumbnailUrl(thumbnailUrl)
                .slug(slug) // 먼저 설정
                .build();

        User saved = userRepository.save(user);


        return userRepository.save(saved);
    }

    @Override
    public void updateProfile(String email, String name, String bio, MultipartFile thumbnail) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (name != null) user.setName(name);
        if (bio != null) user.setBio(bio);
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = s3Uploader.upload(thumbnail, "user-thumbnail");
            user.setThumbnailUrl(thumbnailUrl);
        }

        userRepository.save(user);
    }
}
