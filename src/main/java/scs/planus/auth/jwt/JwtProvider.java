package scs.planus.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import scs.planus.auth.PrincipalDetails;
import scs.planus.auth.PrincipalDetailsService;
import scs.planus.common.exception.PlanusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static scs.planus.common.response.CustomResponseStatus.UNAUTHORIZED_ACCESS_TOKEN;

@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private final PrincipalDetailsService principalDetailsService;

    @Value("${jwt.access-token.expired-in}")
    private long accessTokenExpiredIn;

    @Value("${jwt.refresh-token.expired-in}")
    private long refreshTokenExpiredIn;

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

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX_TOKEN)) {
            return bearerToken.substring(PREFIX_TOKEN.length());
        }
        return null;
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

    public Authentication getAuthentication(String email) {
        PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
    }
}
