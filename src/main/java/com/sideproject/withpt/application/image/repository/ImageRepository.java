package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.domain.record.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
