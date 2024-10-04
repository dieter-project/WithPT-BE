package com.sideproject.withpt.application.lesson.controller.response;

import com.sideproject.withpt.application.lesson.repository.dto.LessonInfoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonMembersResponse {

    private List<LessonInfoResponse> lessonInfos;

}
