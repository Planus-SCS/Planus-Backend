package scs.planus.global.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scs.planus.global.auth.entity.Token;
import scs.planus.global.exception.PlanusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static scs.planus.global.exception.CustomExceptionStatus.UNAUTHORIZED_ACCESS_TOKEN;

@Component
@Getter
@Slf4j
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiredIn;
    private final long refreshTokenExpiredIn;

    public JwtProvider(@Value("${jwt.token.secret-key}") final String secretKey,
                       @Value("${jwt.access-token.expired-in}") final long accessTokenExpiredIn,
                       @Value("${jwt.refresh-token.expired-in}") final long refreshTokenExpiredIn) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredIn = accessTokenExpiredIn;
        this.refreshTokenExpiredIn = refreshTokenExpiredIn;
    }

    public Token generateToken(String payload) {
        return Token.builder()
                .accessToken(generateAccessToken(payload))
                .refreshToken(generateRefreshToken())
                .refreshTokenExpiredIn(refreshTokenExpiredIn)
                .build();
    }

    public boolean isValidToken(String token) {
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            log.info("expiredDate={}", claimsJws.getBody().getExpiration());
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("이미 만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰 형식입니다.");
        } catch (MalformedJwtException e) {
            log.info("인증 토큰이 올바르게 구성되지 않았습니다.");
        } catch (SignatureException e) {
            log.info("인증 시그니처가 올바르지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 토큰입니다.");
        }
        return false;
    }

    public String getPayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new PlanusException(UNAUTHORIZED_ACCESS_TOKEN);
        }
    }

    private String generateAccessToken(String payload) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date expired = new Date(now.getTime() + accessTokenExpiredIn);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken() {
        Date now = new Date();
        Date expired = new Date(now.getTime() + refreshTokenExpiredIn);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
