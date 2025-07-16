package com.artive.auth.security.oauth;

import com.artive.auth.entity.User;
import com.artive.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name"); // 구글 이름을 nickname으로 사용


        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Step 1: 먼저 ID 생성을 위해 저장
                    User newUser = User.builder()
                            .email(email)
                            .name(nickname)
                            .password("GOOGLE")
                            .role("ARTIST")
                            .isVerified(true)
                            .build();

                    User savedUser = userRepository.save(newUser); // ID 확보

                    // Step 2: UUID 기반 고유 slug 생성
                    String slug = "user_" + savedUser.getId() + "_" + UUID.randomUUID().toString().substring(0, 4);
                    savedUser.setSlug(slug);

                    return userRepository.save(savedUser);
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes,
                "email"
        );
    }

}
