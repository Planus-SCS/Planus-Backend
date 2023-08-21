package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.entity.apple.ApplePublicKeys;
import scs.planus.global.exception.PlanusException;

import java.security.PublicKey;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_APPLE_IDENTITY_TOKEN;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {
    private static final String EMAIL_KEY = "email";

    private final AppleJwtParser appleJwtParser;
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public String getAppleEmail(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

        ApplePublicKeys applePublicKey = appleAuthClient.getApplePublicKey();

        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKey);

        Claims claims = appleJwtParser.parseClaimWithPublicKey(identityToken, publicKey);

        validateClaims(claims);

        return claims.get(EMAIL_KEY, String.class);
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        }
    }
}
