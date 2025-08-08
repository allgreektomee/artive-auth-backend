package com.artive.auth.security.oauth;

import com.artive.auth.security.jwt.JwtAuthenticationFilter;
import com.artive.auth.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 요청은 모두 허용 (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 인증 없이 접근 가능한 엔드포인트들
                        .requestMatchers(
                                "/auth/**",
                                "/mail/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/artwork/**"
                        ).permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 명시 (쿠키/인증정보 포함이므로 * 사용 불가)
        config.setAllowedOrigins(List.of("https://www.artivefor.me"));

        // 허용할 HTTP 메소드
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // 허용할 헤더
        config.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // preflight 요청 캐시 시간 (초)
        config.setMaxAge(3600L);

        // 노출할 헤더 설정
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
