package com.sideproject.withpt.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    TEST_ERROR(HttpStatus.BAD_REQUEST, "테스트 에러 입니다."),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원 입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
    AT_LEAST_ONE_DATA_MUST_BE_INCLUDED(HttpStatus.BAD_REQUEST, "최소 하나 이상의 데이터가 포함되어야 합니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INVALID_HEADER(HttpStatus.BAD_REQUEST, "유효한 header가 아닙니다."),
    REDIS_PUT_EMPTY_KEY(HttpStatus.BAD_REQUEST, "Empty Key를 입력하였습니다."),
    REDIS_PUT_FAIL(HttpStatus.BAD_REQUEST, "잘못된 Key를 입력하였습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다."),
    EMPTY_DELETE_FILE(HttpStatus.BAD_REQUEST, "삭제하려는 파일이 없습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    CREDENTIALS_DO_NOT_EXIST(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password"),
    WRONG_TYPE_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 구성의 JWT 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다. 재 로그인 해주세요~"),
    NOT_EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Access Token이 아닙니다."),
    NOT_VERIFICATION_AUTH_CODE(HttpStatus.UNAUTHORIZED, "비정상적인 접근!! 인증 코드 검증이 완료되지 않았습니다."),
    NOT_VERIFICATION_LOGOUT(HttpStatus.UNAUTHORIZED, "로그아웃 된 Token으로 접근하셨습니다. 재로그인 부탁드립니다."),

    /* 403 forbidden : 권한이 없는 사용자 */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다."),

    /* 500 Internal Server Error : 서버가 처리 방법을 모르는 상황이 발생. 서버는 아직 처리 방법을 알 수 없음.*/
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "특정할 수 없는 서버 에러 입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 전송에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
