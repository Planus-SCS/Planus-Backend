package scs.planus.global.auth.service.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import scs.planus.global.exception.PlanusException;

import java.security.PublicKey;
import java.util.Map;

import static scs.planus.global.exception.CustomExceptionStatus.*;

@Component
public class AppleJwtParser {
    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64Utils.decodeFromUrlSafeString(encodedHeader));

            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        } catch (RuntimeException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }

    public Claims parseClaimWithPublicKey(String identityToken, PublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new PlanusException(UNAUTHORIZED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new PlanusException(INVALID_APPLE_IDENTITY_TOKEN);
        } catch (RuntimeException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }
}
