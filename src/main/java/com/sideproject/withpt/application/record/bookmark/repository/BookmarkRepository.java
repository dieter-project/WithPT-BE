package com.sideproject.withpt.application.record.bookmark.repository;

import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberIdAndTitle(Long memberId, String title);

    boolean existsByMemberAndTitle(Member member, String title);

    List<Bookmark> findAllByMemberOrderByUploadDateDescTitleAsc(Member member);

    Optional<Bookmark> findByIdAndMember(Long bookmarkId, Member member);

    @Modifying
    @Query("delete from Bookmark b where b.id in :ids and b.member = :member")
    void deleteAllByIdsAndMember(@Param("ids") List<Long> ids, @Param("member") Member member);
}
