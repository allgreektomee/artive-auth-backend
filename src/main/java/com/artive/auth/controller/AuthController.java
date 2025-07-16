package com.artive.auth.controller;

import com.artive.auth.dto.SignupRequestDto;
import com.artive.auth.dto.LoginRequestDto;
import com.artive.auth.dto.UserDto;
import com.artive.auth.entity.User;
import com.artive.auth.repository.UserRepository;
import com.artive.auth.security.jwt.JwtProvider;
import com.artive.auth.service.EmailVerificationService;
import com.artive.auth.security.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.artive.auth.service.S3Uploader;
import org.springframework.web.multipart.MultipartFile;
import com.artive.auth.security.UserDetailsImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final JwtProvider jwtProvider; // 🔥 여기에 주입
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailVerificationService verificationService;
    private final S3Uploader s3Uploader;
    private final UserServiceImpl userService;


//    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> signup(@ModelAttribute SignupRequestDto requestDto) {
//        try {
//            String thumbnailUrl = null;
//            if (requestDto.getThumbnail() != null && !requestDto.getThumbnail().isEmpty()) {
//                thumbnailUrl = s3Uploader.upload(requestDto.getThumbnail(), "user-thumbnail");
//            }
//
//            User savedUser = userService.signup(requestDto, thumbnailUrl);
//            String token = jwtProvider.generateToken(savedUser.getEmail());
//            return ResponseEntity.ok().body(Map.of("token", token, "message", "회원가입 성공"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
//        }
//    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto requestDto) {
        try {
            // 썸네일 없이 회원가입
            User savedUser = userService.signup(requestDto, null);

            String token = jwtProvider.generateToken(savedUser.getEmail());
            return ResponseEntity.ok().body(Map.of("token", token, "message", "회원가입 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
//        User user = userRepository.findByEmailAndIsVerifiedTrue(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("이메일 인증이 완료되지 않았거나 존재하지 않는 사용자입니다."));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        String token = jwtProvider.generateToken(user.getEmail());

        // ✅ HttpOnly 쿠키로 JWT 설정
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false) // HTTPS일 경우 true
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax") // 또는 "Strict" / "None"
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "slug", user.getSlug(),
                        "token", token,
                        "message", "로그인 성공"
                ));
    }


    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User user = userDetails.getUser();
        return ResponseEntity.ok(UserDto.from(user));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean result = verificationService.verifyToken(token);
        if (result) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다!");
        } else {
            return ResponseEntity.badRequest().body("이메일 인증에 실패했습니다.");
        }
    }

    @PutMapping(value = "/edit-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editProfile(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bio,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        try {
            userService.updateProfile(email, name, bio, thumbnail);
            return ResponseEntity.ok("프로필이 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("수정 실패: " + e.getMessage());
        }
    }


    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", !exists);  // 사용 가능하면 true

        return ResponseEntity.ok(result);
    }
}