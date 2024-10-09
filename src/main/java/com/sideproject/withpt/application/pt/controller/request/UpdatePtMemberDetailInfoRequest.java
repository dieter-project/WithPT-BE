package com.sideproject.withpt.application.pt.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePtMemberDetailInfoRequest {

    private int totalPtCount;
    private int remainingPtCount;
    private String note;

    @Builder
    private UpdatePtMemberDetailInfoRequest(int totalPtCount, int remainingPtCount, String note) {
        this.totalPtCount = totalPtCount;
        this.remainingPtCount = remainingPtCount;
        this.note = note;
    }
}
