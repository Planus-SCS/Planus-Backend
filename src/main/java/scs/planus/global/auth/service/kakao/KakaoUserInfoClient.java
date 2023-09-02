package scs.planus.global.auth.service.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class KakaoUserInfoClient {

    private final String kakaoUserInfoUri;

    public KakaoUserInfoClient(@Value("${oauth.kakao.user-info-uri}") final String kakaoUserInfoUri) {
        this.kakaoUserInfoUri = kakaoUserInfoUri;
    }

    public Map<String, Object> getUserAttributes(String accessToken) {
        return WebClient.create()
                .post()
                .uri(kakaoUserInfoUri)
                .headers(header -> {
                    header.setBearerAuth(accessToken);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
