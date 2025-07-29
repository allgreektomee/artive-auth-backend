package com.artive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://www.artivefor.me",
                        "https://artive-frontend-eqqh4jh6a-artives-projects.vercel.app",  // ✅ 서브도메인 추가
                        "https://artive-frontend-git-main-artives-projects.vercel.app"   // ✅ 추가 배포 주소도 함께
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
