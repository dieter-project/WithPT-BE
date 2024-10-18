package com.sideproject.withpt.application.member.repository;

import com.sideproject.withpt.application.member.controller.response.MemberSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQueryRepository {

    Slice<MemberSearchResponse> findBySearchOption(Pageable pageable, String name);
}
