package com.artive.auth.security.jwt;

import com.artive.auth.entity.User;
import com.artive.auth.repository.UserRepository;
import com.artive.auth.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. OPTIONS 요청은 JWT 검증 없이 바로 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtProvider.validateToken(token)) {
                    String email = jwtProvider.getEmailFromToken(token);

                    Optional<User> userOptional = userRepository.findByEmail(email);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        UserDetailsImpl userDetails = new UserDetailsImpl(user);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 민감정보 마스킹 후 로그
                        logger.info(String.format("JWT 인증 성공 - email: %s, token: %s...",
                                email,
                                token.substring(0, Math.min(token.length(), 10))
                        ));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("JWT 필터 처리 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }
}
