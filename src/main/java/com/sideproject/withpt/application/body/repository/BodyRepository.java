package com.sideproject.withpt.application.body.repository;

import com.sideproject.withpt.domain.record.Body;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BodyRepository extends JpaRepository<Body, Long>, BodyCustomRepository {

}
