package com.sideproject.withpt.application.pt.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdatePtMemberDetailInfoRequest {

    private final int totalPtCount;
    private final int remainingPtCount;
    private final String note;

    @Builder
    private UpdatePtMemberDetailInfoRequest(int totalPtCount, int remainingPtCount, String note) {
        this.totalPtCount = totalPtCount;
        this.remainingPtCount = remainingPtCount;
        this.note = note;
    }
}
