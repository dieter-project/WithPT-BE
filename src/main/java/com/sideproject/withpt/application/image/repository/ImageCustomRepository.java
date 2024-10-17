package com.sideproject.withpt.application.image.repository;

import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ImageCustomRepository {
    Slice<ImageInfoResponse> findAllByMemberAndUsagesAndUploadDate(Member member, UsageType usageType, LocalDate uploadDate, Pageable pageable);
}
