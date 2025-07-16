package com.artive.auth.security.oauth;

import com.artive.auth.entity.User;
import com.artive.auth.repository.UserRepository;
import com.artive.auth.security.jwt.JwtProvider;
import com.artive.config.AppProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = ((org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal()).getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow();

        String token = jwtProvider.generateToken(user.getEmail(), user.getRole());

        // 프론트로 토큰 전달 (여기선 query param 사용 예시)
        String name = user.getName(); // 또는 user.getName()

        String backendUrl = appProperties.getBackendUrl();

        response.sendRedirect(backendUrl +"/oauth2/success?token=" + token + "&nickname=" + name);
    }
}
