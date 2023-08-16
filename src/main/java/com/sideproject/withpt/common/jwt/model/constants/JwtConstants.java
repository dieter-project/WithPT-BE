package com.sideproject.withpt.common.jwt.model.constants;

public interface JwtConstants {

    String TOKEN_HEADER = "Authorization";
    String ACCESS_TOKEN_PREFIX = "Bearer ";
    long ACCESS_TOKEN_VALID_TIME = 1000 * 60 * 30; // 30분
    long REFRESH_TOKEN_VALID_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    String REFRESH_TOKEN_PREFIX = "Refresh:";
    String ACCESS_TOKEN_BLACK_LIST_PREFIX = "Black-Access:";
}
