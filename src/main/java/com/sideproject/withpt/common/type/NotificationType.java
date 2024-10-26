package com.sideproject.withpt.common.type;

public enum NotificationType {

    DIET_FEEDBACK("식단 피드백"),
    LESSON_REGISTRATION_REQUEST("수업 등록 요청"),
    LESSON_CHANGE_REQUEST("수업 변경 요청"),
    LESSON_REGISTRATION_COMPLETION("수업 예약 완료"),
    PT_REGISTRATION_REQUEST("PT 등록 요청");


    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
