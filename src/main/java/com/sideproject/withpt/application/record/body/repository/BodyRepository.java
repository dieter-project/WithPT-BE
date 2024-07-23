package com.sideproject.withpt.application.record.body.repository;

import com.sideproject.withpt.domain.record.body.Body;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyRepository extends JpaRepository<Body, Long>, BodyCustomRepository {

}
