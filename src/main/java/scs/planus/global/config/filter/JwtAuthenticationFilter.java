package scs.planus.global.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import scs.planus.global.auth.service.JwtProvider;
import scs.planus.global.exception.PlanusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static scs.planus.global.exception.CustomExceptionStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.resolveToken(request);
            if (token != null && jwtProvider.isValidToken(token)) {
                String email = jwtProvider.getPayload(token);
                Authentication authentication = jwtProvider.getAuthentication(email);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (RuntimeException e) {
            throw new PlanusException(INTERNAL_SERVER_ERROR);
        }
        filterChain.doFilter(request, response);
    }
}
