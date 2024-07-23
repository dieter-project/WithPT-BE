package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageResponse;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ImageCustomRepository {
    Slice<BodyImageResponse> findAllBodyImage(Pageable pageable, Long memberId, Usages usages);
    Slice<BodyImageInfoResponse> findAllByMemberAndUsagesAndUploadDate(Pageable pageable, Member member, Usages usages, LocalDate uploadDate);
}
