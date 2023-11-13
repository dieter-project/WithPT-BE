package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.domain.record.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUrl(String url);
    List<Image> findByMemberIdAndUploadDate(Long memberId, LocalDate uploadDate);
}
