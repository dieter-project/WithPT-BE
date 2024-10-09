package com.sideproject.withpt.common.type;

public enum LessonRequestStatus {

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
