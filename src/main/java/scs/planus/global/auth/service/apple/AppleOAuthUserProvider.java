package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.entity.userinfo.AppleUserInfo;
import scs.planus.global.auth.entity.userinfo.OAuthUserInfo;
import scs.planus.global.auth.entity.ApplePublicKeys;
import scs.planus.global.exception.PlanusException;

import java.security.PublicKey;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.INVALID_APPLE_IDENTITY_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {
    private static final String EMAIL_KEY = "email";

    private final AppleJwtParser appleJwtParser;
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public OAuthUserInfo getUserInfo(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

        ApplePublicKeys applePublicKey = appleAuthClient.getApplePublicKey();

        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, applePublicKey);
        log.info("identityToken = [{}]", identityToken);
        log.info("publicKey = [{}]", publicKey);
        Claims claims = appleJwtParser.parseClaimWithPublicKey(identityToken, publicKey);

        validateClaims(claims);

        String email = claims.get(EMAIL_KEY, String.class);
        return new AppleUserInfo(email);
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            log.info("AppleOAuthUserProvider");
            log.info("claims = [{}]", claims);
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        }
    }
}
