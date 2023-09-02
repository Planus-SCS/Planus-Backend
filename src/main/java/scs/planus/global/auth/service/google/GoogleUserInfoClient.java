package scs.planus.global.auth.service.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class GoogleUserInfoClient {

    private final String googleUserInfoUri;

    public GoogleUserInfoClient(@Value("${oauth.google.user-info-uri}") final String googleUserInfoUri) {
        this.googleUserInfoUri = googleUserInfoUri;
    }

    public Map<String, Object> getUserAttributes(String accessToken) {
        return WebClient.create()
                .post()
                .uri(googleUserInfoUri)
                .headers(header -> {
                    header.setBearerAuth(accessToken);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
