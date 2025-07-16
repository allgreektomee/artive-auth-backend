// SignupRequestDto.java
package com.artive.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignupRequestDto {
    private String email;
    private String password;
    private String name;
//    private MultipartFile thumbnail;  // 썸네일 이미지
}
