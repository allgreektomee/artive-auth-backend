package com.artive.auth.service;

import com.artive.auth.entity.EmailVerificationToken;
import com.artive.auth.entity.User;
import com.artive.auth.repository.EmailVerificationTokenRepository;
import com.artive.auth.repository.UserRepository;
import com.artive.config.AppProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    private final AppProperties appProperties;

    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setEmail(email);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(verificationToken);

        String subject = "이메일 인증 요청";
        String message = "아래 링크를 클릭하여 이메일 인증을 완료해주세요:\n"
        + appProperties.getBackendUrl() + "/auth/verify?token=" + token;

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailMessage.setFrom("your_email@gmail.com"); // 반드시 설정 필요

            mailSender.send(mailMessage);
            System.out.println("이메일 전송 완료: " + email);
        } catch (Exception e) {
            System.out.println("메일 발송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public boolean verifyToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("토큰이 유효하지 않습니다."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }

        User user = userRepository.findByEmail(verificationToken.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setVerified(true); // Email 인증 플래그 활성화
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
        return true;
    }
}
