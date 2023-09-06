package scs.planus.global.auth.service.google;

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
public class GoogleAccessTokenClient {

    private final String googleTokenUri;
    private final String googleClientId;
    private final String googleClientSecret;
    private final String redirectUri;

    public GoogleAccessTokenClient(@Value("${oauth.google.token-uri}") final String googleTokenUri,
                                   @Value("${oauth.google.client-id}") final String googleClientId,
                                   @Value("${oauth.google.client-secret}") final String googleClientSecret,
                                   @Value("${oauth.google.redirect-uri}") final String redirectUri) {
        this.googleTokenUri = googleTokenUri;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.redirectUri = redirectUri;
    }

    public OAuthTokenResponseDto getToken(String code) {
        return WebClient.create()
                .post()
                .uri(googleTokenUri)
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
        data.add("client_id", googleClientId);
        data.add("client_secret", googleClientSecret);
        data.add("redirect_uri", redirectUri);
        data.add("grant_type", "authorization_code");
        return data;
    }
}
