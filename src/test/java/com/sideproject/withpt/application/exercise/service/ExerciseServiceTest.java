package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.Exercise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void 오늘_날짜의_회원의_운동기록_전체_조회하기() {
        // given

        // when

        // then
    }

    @Test
    void 운동_기록_입력하기() {
        // given
        Exercise exercise = Exercise.builder()
                .title("운동명입니다.")
                .weight(300)
                .set(3)
                .bookmarkYn("N")
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .build();

        // when

        // then
    }

    @Test
    void 운동_기록_수정하기() {
        // given

        // when

        // then
    }

    @Test
    void 운동_기록_삭제하기() {
        // given

        // when

        // then
    }

}