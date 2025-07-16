package com.artive.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditProfileRequestDto {
    private String name;
    private String nickname;
    private String bio;
    private MultipartFile thumbnail;
}
