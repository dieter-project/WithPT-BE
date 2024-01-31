package com.sideproject.withpt.application.type;

public enum LessonRequestStatus {
    SENT("내가 보낸 요청"),
    RECEIVED("받은 요청"),
    REGISTRATION("등록"),
    CHANGE("변경");

    private final String status;

    LessonRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
