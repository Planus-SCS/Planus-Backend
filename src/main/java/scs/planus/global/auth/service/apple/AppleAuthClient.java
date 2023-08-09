package scs.planus.global.auth.service.apple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;

@Component
public class AppleAuthClient {

    private final String publicKeyUrl;

    public AppleAuthClient(@Value("${oauth.apple.public-key-url}") String publicKeyUrl) {
        this.publicKeyUrl = publicKeyUrl;
    }

    public ApplePublicKeys getApplePublicKey() {
        return WebClient.create()
                .get()
                .uri(publicKeyUrl)
                .retrieve()
                .bodyToMono(ApplePublicKeys.class)
                .block();
    }
}
