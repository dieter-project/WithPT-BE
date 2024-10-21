package com.sideproject.withpt.common.type;

import com.sideproject.withpt.domain.lesson.Lesson;
import java.util.List;

public enum LessonStatus {
    RESERVED("예약"),
    PENDING_APPROVAL("승인 대기 중"),
    COMPLETION("완료"),
    TIME_OUT_CANCELED("자동 취소"),
    REJECT("수업 변경 거절"),
    CANCELED("취소");

    private final String status;

    LessonStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isScheduleChangeNotAllowed(LessonStatus status) {
        return status != RESERVED;
    }

    public static boolean isReserved(LessonStatus status) {
        return status == RESERVED;
    }

    public static boolean isPendingApproval(LessonStatus status) {
        return status == PENDING_APPROVAL;
    }

    public static boolean isCanceled(Lesson lesson) {
        return List.of(CANCELED, TIME_OUT_CANCELED).contains(lesson.getStatus());
    }
}
