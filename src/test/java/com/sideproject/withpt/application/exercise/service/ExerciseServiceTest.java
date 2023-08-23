package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("운동 기록 입력 성공")
    public void saveExerciseSuccess() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("운동 기록 리스트 조회 성공")
    public void findAllExerciseListSuccess() {
        // given


        // when

        // then
    }

}