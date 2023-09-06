package scs.planus.global.auth.service.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import scs.planus.global.auth.dto.OAuthTokenResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class KakaoAccessTokenClient {

    private final String kakaoTokenUri;
    private final String kakaoClientId;
    private final String kakaoClientSecret;
    private final String redirectUri;

    public KakaoAccessTokenClient(@Value("${oauth.kakao.token-uri}") final String kakaoTokenUri,
                                  @Value("${oauth.kakao.client-id}") final String kakaoClientId,
                                  @Value("${oauth.kakao.client-secret}") final String kakaoClientSecret,
                                  @Value("${oauth.kakao.redirect-uri}") final String redirectUri) {
        this.kakaoTokenUri = kakaoTokenUri;
        this.kakaoClientId = kakaoClientId;
        this.kakaoClientSecret = kakaoClientSecret;
        this.redirectUri = redirectUri;
    }

    public OAuthTokenResponseDto getToken(String code) {
        return WebClient.create()
                .post()
                .uri(kakaoTokenUri)
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(accessTokenRequest(code))
                .retrieve()
                .bodyToMono(OAuthTokenResponseDto.class)
                .block();
    }

    private MultiValueMap<String, String> accessTokenRequest(String code) {
        LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("code", code);
        data.add("client_id", kakaoClientId);
        data.add("client_secret", kakaoClientSecret);
        data.add("redirect_uri", redirectUri);
        data.add("grant_type", "authorization_code");
        return data;
    }
}
