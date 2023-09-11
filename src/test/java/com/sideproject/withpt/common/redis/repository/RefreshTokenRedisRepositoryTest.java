package com.sideproject.withpt.common.redis.repository;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.MEMBER_REFRESH_TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.config.location="
    + "classpath:/application.yml,"
    + "classpath:/application-oauth.yml",
    classes = TestEmbeddedRedisConfig.class)
class RefreshTokenRedisRepositoryTest {

    @Autowired
    RedisClient redisClient;

    @Test
    public void save() {
        //given
        long userId = 3L;
        String token = "testtesttest";
        Long expiration = 10L;

        //when
        redisClient.put(MEMBER_REFRESH_TOKEN_PREFIX + userId, token, TimeUnit.SECONDS, expiration);

        //then
        String refreshToken = redisClient.get(MEMBER_REFRESH_TOKEN_PREFIX + userId);

        assertThat(redisClient.hasKey(MEMBER_REFRESH_TOKEN_PREFIX + userId)).isTrue();
        assertThat(refreshToken).isEqualTo(token);
    }
}