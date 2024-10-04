package com.sideproject.withpt.application.pt.repository.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EachGymMemberListResponse {

    private Long totalMembers;
    private Slice<PtMemberListDto> memberList;
}
