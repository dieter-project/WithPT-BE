package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.domain.record.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageCustomRepository {
    List<Image> findByMemberIdAndUploadDateAndUsage(Long memberId, LocalDate uploadDate, Usages usages);
    Optional<Image> findByUrl(String url);
    void deleteByUrl(String url);
    void deleteByEntityId(Long entityId);
}
