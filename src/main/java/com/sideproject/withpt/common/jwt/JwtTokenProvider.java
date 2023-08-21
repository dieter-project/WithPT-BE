package com.sideproject.withpt.common.jwt;

import com.sideproject.withpt.application.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey, UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
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

    public Long extractMemberId(String token) {
        return Long.valueOf(this.extractSubject(token));
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(extractSubject(token));
        return new UsernamePasswordAuthenticationToken(extractMemberId(token), "", userDetails.getAuthorities());
    }

    public boolean isExpiredToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
        }catch (ExpiredJwtException e){
            return true;
        }
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
