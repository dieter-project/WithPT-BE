package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageCustomRepository {

    List<Image> findAllByMemberAndUsageIdentificationId(Member member, String usageIdentificationId);

    void deleteAllByUsageIdentificationIdAndMember(String usageIdentificationId, Member member);

    List<Image> findAllByMemberAndIdIn(Member member, List<Long> ids);
}
