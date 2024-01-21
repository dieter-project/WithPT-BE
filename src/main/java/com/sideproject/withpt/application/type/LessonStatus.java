package com.sideproject.withpt.application.type;

public enum LessonStatus {
    RESERVED("예약"),
    PENDING_APPROVAL("승인 대기 중"),
    COMPLETION("완료"),
    CANCELED("취소");

    private final String status;

    LessonStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
