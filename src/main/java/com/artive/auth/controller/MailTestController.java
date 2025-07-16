package com.artive.auth.controller;

import com.artive.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailTestController {

    private final EmailVerificationService verificationService;

    @GetMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestParam String email) {
        verificationService.sendVerificationEmail(email);
        return ResponseEntity.ok("메일 전송 완료!");
    }



}