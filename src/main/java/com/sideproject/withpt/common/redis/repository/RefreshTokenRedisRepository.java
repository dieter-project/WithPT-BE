package com.sideproject.withpt.common.redis.repository;

import com.sideproject.withpt.common.redis.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long userId);

}
