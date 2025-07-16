package com.artive.auth.service;

import com.artive.auth.entity.User;
import com.artive.auth.dto.SignupRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserByEmail(String email);
    User signup(SignupRequestDto dto, String thumbnailUrl);
    void updateProfile(String email, String name, String bio, MultipartFile thumbnail) throws IOException;
}
