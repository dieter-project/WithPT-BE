package com.sideproject.withpt.application.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponse {

    private String message;
}
