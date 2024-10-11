package com.sideproject.withpt.application.auth.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponse {

    private String message;
}
