package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scs.planus.global.exception.PlanusException;
import scs.planus.global.util.encryptor.Encryptor;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_APPLE_IDENTITY_TOKEN;

@Component
public class AppleClaimsValidator {
    private static final String NONCE_KEY = "nonce";

    private final String iss;
    private final String clientId;
    private final String nonce;

    public AppleClaimsValidator(@Value("${oauth.apple.iss}") String iss,
                                @Value("${oauth.apple.client-id}") String clientId,
                                @Value("${oauth.apple.nonce}") String nonce) {
        this.iss = iss;
        this.clientId = clientId;
        this.nonce = Encryptor.encryptWithSHA256(nonce);
    }

    public void validation(Claims claims) {
        boolean result = claims.getIssuer().contains(iss) &&
                claims.getAudience().equals(clientId) &&
                claims.get(NONCE_KEY, String.class).equals(nonce);

        if (!result) {
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        }
    }
}