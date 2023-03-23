package scs.planus.auth.jwt.filter;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import scs.planus.auth.jwt.JwtProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("== JwtAuthenticationFilter ==");
        try {
            String token = jwtProvider.resolveToken(request);
            if (token != null && jwtProvider.isValidToken(token)) {
                String email = jwtProvider.getPayload(token);
                Authentication authentication = jwtProvider.getAuthentication(email);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.info("jwt authentication error = {}", e.getMessage());
            request.setAttribute("planusException", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
