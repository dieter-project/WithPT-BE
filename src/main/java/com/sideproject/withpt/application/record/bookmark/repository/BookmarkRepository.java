package com.sideproject.withpt.application.record.bookmark.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberIdAndTitle(Long memberId, String title);

    boolean existsByMemberAndTitle(Member member, String title);

    List<Bookmark> findAllByMemberOrderByUploadDateDescTitleAsc(Member member);

    @Modifying
    @Query("delete from Bookmark b where b.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);
}
