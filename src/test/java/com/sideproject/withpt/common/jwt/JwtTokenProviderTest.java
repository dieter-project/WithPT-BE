package com.sideproject.withpt.common.jwt;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_VALID_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    private Key key;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void init() {
        String secretKey = "ZGF5b25lLXNwcmluZy1ib290LWRpdmlkZW5kLXByb2plY3QtdHV0b3JpYWwtand0LXNlY3JldC1rZXkKfdieJFKElfjdows3mfn";

        jwtTokenProvider = new JwtTokenProvider(secretKey, userDetailsService);

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    public void generateToken() {
        //given
        String subject = "100";
        Date expireAt = new Date(ACCESS_TOKEN_VALID_TIME);

        //when
        String generate = jwtTokenProvider.generate(subject, expireAt);

        //then
        assertThat(generate).isNotNull();

    }

    @Test
    public void extractSubject() {
        //given
        String subject = "100";
        Date expireAt = new Date();
        String generatedToken = jwtTokenProvider.generate(subject, expireAt);

        //when
        String extractSubject = jwtTokenProvider.extractSubject(generatedToken);

        //then
        assertThat(extractSubject).isEqualTo(subject);
    }

    @Test
    public void isExpiredToken() {
        //given
        String token = Jwts.builder()
            .setSubject(String.valueOf(1L))
            .setExpiration(new Date())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        //when
        boolean isExpiredToken = jwtTokenProvider.isExpiredToken(token);

        //then
        assertTrue(isExpiredToken);
    }


    @Test
    void isExpiredTokenByNotExpiredToken() {
        // given
        Date expiredAt = new Date((new Date().getTime() + ACCESS_TOKEN_VALID_TIME));
        String subject = "100";
        String generatedToken = jwtTokenProvider.generate(subject, expiredAt);

        // when
        boolean isExpiredToken = jwtTokenProvider.isExpiredToken(generatedToken);

        // then
        assertFalse(isExpiredToken);
    }

}