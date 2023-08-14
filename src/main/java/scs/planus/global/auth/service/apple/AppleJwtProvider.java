package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scs.planus.global.exception.PlanusException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import static scs.planus.global.exception.CustomExceptionStatus.INTERNAL_SERVER_ERROR;

@Component
public class AppleJwtProvider {
    private final String clientId;
    private final String clientSecretKey;
    private final String keyId;
    private final String teamId;
    private final String authUrl;
    private final long expiredIn;

    public AppleJwtProvider(@Value("${oauth.apple.client-id}") String clientId,
                            @Value("${oauth.apple.client-secret.key}") String clientSecretKey,
                            @Value("${oauth.apple.client-secret.expired-in}") long expiredIn,
                            @Value("${oauth.apple.key-id}") String keyId,
                            @Value("${oauth.apple.team-id}") String teamId,
                            @Value("${oauth.apple.iss}") String authUrl) {
        this.clientId = clientId;
        this.clientSecretKey = clientSecretKey;
        this.expiredIn = expiredIn;
        this.keyId = keyId;
        this.teamId = teamId;
        this.authUrl = authUrl;
    }

    public String createClientSecret(){
        Date now = new Date();
        Date expired = new Date(now.getTime() + expiredIn);

        return Jwts.builder()
                .setHeaderParam("alg", "ES256")
                .setHeaderParam("kid", keyId)
                .setIssuer(teamId)
                .setSubject(clientId)
                .setAudience(authUrl)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(createPrivateKey(clientSecretKey), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey createPrivateKey(String clientSecretKey){
        try {
            byte[] encoded = Base64.decodeBase64(clientSecretKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
    }
}
