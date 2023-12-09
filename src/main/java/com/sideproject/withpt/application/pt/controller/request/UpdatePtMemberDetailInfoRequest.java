package com.sideproject.withpt.application.pt.controller.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdatePtMemberDetailInfoRequest {

    private int totalPtCount;
    private int remainingPtCount;
    private String note;
}
