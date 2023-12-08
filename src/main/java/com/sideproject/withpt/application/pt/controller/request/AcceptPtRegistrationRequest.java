package com.sideproject.withpt.application.pt.controller.request;

import lombok.Getter;

@Getter
public class AcceptPtRegistrationRequest {

    private Long memberId;
    private Long trainerId;
    private Long gymId;
}
