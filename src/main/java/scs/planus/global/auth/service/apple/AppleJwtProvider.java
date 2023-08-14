package scs.planus.global.auth.service.apple;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scs.planus.global.util.resourceLoader.ResourcesLoader;

import java.util.Date;

@Component
public class AppleJwtProvider {
    private final String clientId;
    private final String clientSecret;
    private final String keyId;
    private final String teamId;
    private final String authUrl;
    private final long expiredIn;

    public AppleJwtProvider(@Value("${oauth.apple.client-id}") String clientId,
                            @Value("${oauth.apple.client-secret.key-path}") String clientSecret,
                            @Value("${oauth.apple.client-secret.expired-in}") long expiredIn,
                            @Value("${oauth.apple.key-id}") String ketId,
                            @Value("${oauth.apple.team-id}") String teamId,
                            @Value("${oauth.apple.iss}") String authUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiredIn = expiredIn;
        this.keyId = ketId;
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
                .signWith(ResourcesLoader.createPrivateKey(clientSecret), SignatureAlgorithm.ES256)
                .compact();
    }
}
