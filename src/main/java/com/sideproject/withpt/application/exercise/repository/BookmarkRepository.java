package com.sideproject.withpt.application.exercise.repository;

import com.sideproject.withpt.domain.record.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
        List<Bookmark> findByMemberId(Long memberId);
}
