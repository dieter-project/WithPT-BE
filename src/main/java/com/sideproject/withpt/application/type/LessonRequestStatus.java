package com.sideproject.withpt.application.type;

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
