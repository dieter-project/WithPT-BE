package com.sideproject.withpt.common.jwt.model.constants;

import com.sideproject.withpt.application.type.Role;

public interface JwtConstants {

    String TOKEN_HEADER = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = 1000 * 60 * 30; // 30분
    long REFRESH_TOKEN_VALID_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    String MEMBER_REFRESH_TOKEN_PREFIX = "Refresh:" + Role.MEMBER;
    String TRAINER_REFRESH_TOKEN_PREFIX = "Refresh:" + Role.TRAINER;
    String ACCESS_TOKEN_BLACK_LIST_PREFIX = "Black-Access:";
}
