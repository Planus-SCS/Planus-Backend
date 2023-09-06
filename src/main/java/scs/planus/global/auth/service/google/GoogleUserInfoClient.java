package scs.planus.global.auth.service.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import scs.planus.global.auth.entity.GoogleUserInfo;
import scs.planus.global.auth.entity.OAuthUserInfo;

@Component
public class GoogleUserInfoClient {

    private final String googleUserInfoUri;

    public GoogleUserInfoClient(@Value("${oauth.google.user-info-uri}") final String googleUserInfoUri) {
        this.googleUserInfoUri = googleUserInfoUri;
    }

    public OAuthUserInfo getUserAttributes(String accessToken) {
        return WebClient.create()
                .post()
                .uri(googleUserInfoUri)
                .headers(header -> {
                    header.setBearerAuth(accessToken);
                })
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }
}
