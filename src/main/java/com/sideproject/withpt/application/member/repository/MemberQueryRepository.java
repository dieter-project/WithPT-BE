package com.sideproject.withpt.application.member.repository;

import com.sideproject.withpt.application.member.dto.response.MemberSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQueryRepository {

    Page<MemberSearchResponse> findBySearchOption(Pageable pageable, String name, String nickname);
}
