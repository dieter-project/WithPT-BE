package com.sideproject.withpt.common.redis;

import com.sideproject.withpt.common.exception.GlobalException;
import io.netty.util.internal.StringUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void put(String key, String value) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw GlobalException.REDIS_PUT_EMPTY_KEY;
        }

        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw GlobalException.REDIS_PUT_FAIL;
        }
    }

    public void put(String key, String value, TimeUnit expireTimeUnit, Long expireTime) {
        if (StringUtil.isNullOrEmpty(key)) {
            throw GlobalException.REDIS_PUT_EMPTY_KEY;
        }

        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, expireTime, expireTimeUnit);
        } catch (Exception e) {
            throw GlobalException.REDIS_PUT_FAIL;
        }
    }

    public String get(String key) {
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }

        String redisValue = (String) redisTemplate.opsForValue().get(key);

        if (StringUtil.isNullOrEmpty(redisValue)) {
            return null;
        }

        return redisValue;
    }

    public boolean validationRefreshToken(String key, String refreshToken) {
        String redisRefreshToken = redisTemplate.opsForValue().get(key);
        return refreshToken.equals(redisRefreshToken);
    }

    public void delete(String key) {
        if (!StringUtil.isNullOrEmpty(key)) {
            redisTemplate.delete(key);
        }
    }
}
