package com.sideproject.withpt.application.member.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.pt.repository.model.AssignedPTInfoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_EMPTY)
public class MemberAndPTInfoResponse {

    private MemberInfoResponse memberInfo;
    private List<AssignedPTInfoResponse> ptInfos;

    @Builder
    private MemberAndPTInfoResponse(MemberInfoResponse memberInfo, List<AssignedPTInfoResponse> ptInfos) {
        this.memberInfo = memberInfo;
        this.ptInfos = ptInfos;
    }

    public static MemberAndPTInfoResponse of(MemberInfoResponse loginInfo, List<AssignedPTInfoResponse> ptInfos) {
        return MemberAndPTInfoResponse.builder()
            .memberInfo(loginInfo)
            .ptInfos(ptInfos)
            .build();
    }
}
