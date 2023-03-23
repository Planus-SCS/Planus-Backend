package scs.planus.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Slf4j
public class JwtProvider {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private long accessTokenExpiredIn = 1000L * 60 * 30; // 30분
    private long refreshTokenExpiredIn = 1000L * 60 * 60 * 24 * 14; // 14일
    @Value("${jwt.token.secret-key}")
    private String secretKey;

    public Token generateToken(String payload) {
        return Token.builder()
                .accessToken(generateAccessToken(payload))
                .refreshToken(generateRefreshToken())
                .build();
    }

    public String generateAccessToken(String payload) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date expired = new Date(now.getTime() + accessTokenExpiredIn);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date expired = new Date(now.getTime() + refreshTokenExpiredIn);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
