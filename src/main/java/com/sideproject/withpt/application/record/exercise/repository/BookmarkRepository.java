package com.sideproject.withpt.application.record.exercise.repository;

import com.sideproject.withpt.domain.record.exercise.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
        List<Bookmark> findByMemberId(Long memberId);
        Optional<Bookmark> findByMemberIdAndTitle(Long memberId, String title);

        @Modifying
        @Query("delete from Bookmark b where b.id in :ids")
        void deleteAllByIds(@Param("ids") List<Long> ids);
}
