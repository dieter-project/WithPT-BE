package com.sideproject.withpt.common.jwt;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.ACCESS_TOKEN_VALID_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.security.CustomDetailService;
import com.sideproject.withpt.common.security.impl.MemberDetailService;
import com.sideproject.withpt.common.security.impl.TrainerDetailService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    TrainerRepository trainerRepository;

    private Key key;


    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void init() {
        String secretKey = "ZGF5b25lLXNwcmluZy1ib290LWRpdmlkZW5kLXByb2plY3QtdHV0b3JpYWwtand0LXNlY3JldC1rZXkKfdieJFKElfjdows3mfn";

        List<CustomDetailService> customDetailServices = new ArrayList<>();
        customDetailServices.add(new MemberDetailService(memberRepository));
        customDetailServices.add(new TrainerDetailService(trainerRepository));

        jwtTokenProvider = new JwtTokenProvider(secretKey, customDetailServices);

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