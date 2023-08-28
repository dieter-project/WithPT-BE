package com.sideproject.withpt.common.redis.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.common.redis.domain.RefreshToken;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RefreshTokenRedisRepositoryTest {

    @Autowired
    RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Test
    public void save () {
        //given
        Long userId = 2L;
        String token = "testtesttest";
        Long expiration = 100000L;

        RefreshToken refreshToken = RefreshToken.createRefreshToken(userId, token, expiration);

        //when
        refreshTokenRedisRepository.save(refreshToken);

        //then
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRedisRepository.findById(2L);

        assertThat(optionalRefreshToken.isPresent()).isTrue();
        assertThat(optionalRefreshToken.get().getRefreshToken()).isEqualTo(token);
    }
}