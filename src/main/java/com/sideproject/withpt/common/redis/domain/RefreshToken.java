package com.sideproject.withpt.common.redis.domain;

import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("refresh")
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private Long id;

    private String refreshToken;

    @TimeToLive
    private Long expiration; // second


    public static RefreshToken createRefreshToken(Long useId, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
            .id(useId)
            .refreshToken(refreshToken)
            .expiration(remainingMilliSeconds / 1000)
            .build();
    }
}
