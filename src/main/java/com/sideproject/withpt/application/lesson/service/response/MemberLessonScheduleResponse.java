package com.sideproject.withpt.application.lesson.service.response;

import com.sideproject.withpt.application.lesson.repository.dto.MemberLessonInfoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLessonScheduleResponse {

    private List<MemberLessonInfoResponse> lessonInfos;

    public MemberLessonScheduleResponse(List<MemberLessonInfoResponse> lessonInfos) {
        this.lessonInfos = lessonInfos;
    }
}
