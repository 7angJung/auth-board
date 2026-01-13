package com.jupeter.authboard.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = resolveBearerToken(authHeader);

        if(token != null && jwtTokenProvider.validate(token)) {
            Claims claims = jwtTokenProvider.parse(token).getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);

            var authorities = List.of(new SimpleGrantedAuthority(role));

            // principal은 일단 userId 문자열로(간단)
            var authentication = new UsernamePasswordAuthenticationToken(
                    String.valueOf(userId), null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(String authHeader) {
        if(authHeader == null) return null;
        if(!authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}
