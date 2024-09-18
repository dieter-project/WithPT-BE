package com.sideproject.withpt.common.jwt;

import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.security.CustomDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private final Map<Role, CustomDetailService> customDetailServices;

    public JwtTokenProvider(
        @Value("${spring.jwt.secret}") String secretKey,
        List<CustomDetailService> customDetailServices) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.customDetailServices = customDetailServices.stream().collect(
            Collectors.toUnmodifiableMap(CustomDetailService::getRole, Function.identity())
        );
    }

    public String generate(String subject, Date expireAt) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expireAt)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String generate(String subject, Role role, Date expireAt) {
        return Jwts.builder()
            .setSubject(subject)
            .claim("role", role)
            .setExpiration(expireAt)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String extractSubject(String token) {
        Claims claims = this.parseClaims(token);
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = this.parseClaims(token);
        return (String) claims.get("role");
    }

    public Long extractMemberId(String token) {
        return Long.valueOf(this.extractSubject(token));
    }

    public Authentication getAuthentication(String token) {
        String role = extractRole(token);
        CustomDetailService customDetailService = customDetailServices.get(Role.valueOf(role));
        UserDetails userDetails = customDetailService.loadUserByUsername(extractSubject(token));

        return new UsernamePasswordAuthenticationToken(extractMemberId(token), "", userDetails.getAuthorities());
    }

    // Jwt 토큰 만료 기간 얻기
    public Date getExpiredDate(String token) {
        return this.parseClaims(token).getExpiration();
    }

    public boolean isExpiredToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isValidationToken(String token) {
        return !Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().isEmpty();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
