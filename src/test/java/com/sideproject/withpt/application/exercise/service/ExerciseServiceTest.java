package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.record.Exercise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.sideproject.withpt.application.exercise.Fixture.ExerciseFixture.*;
import static com.sideproject.withpt.application.exercise.Fixture.MemberFixture.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@Import(TestEmbeddedRedisConfig.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    @DisplayName("오늘 날짜로 운동 리스트 조회하기")
    void findExerciseList() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(MEMBER));
        given(exerciseRepository.findByMemberIdAndExerciseDateBetween(anyLong(), any(), any()))
                .willReturn(EXERCISE_LIST);

        // when
        List<ExerciseListResponse> response = exerciseService.findAllExerciseList(MEMBER.getId());

        // then
        then(exerciseRepository).should(times(1))
                .findByMemberIdAndExerciseDateBetween(anyLong(), any(), any());

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getTitle()).isEqualTo("운동명1");
        assertThat(response.get(1).getTitle()).isEqualTo("운동명2");
    }

    @Test
    @DisplayName("해당하는 운동 리스트 조회하기")
    void findOneExercise() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(MEMBER));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(EXERCISE_REQUEST.toEntity(MEMBER)));

        // when
        ExerciseListResponse exercise = exerciseService.findOneExercise(MEMBER.getId(), EXERCISE.getId());

        // then
        then(exerciseRepository).should(times(1)).findById(anyLong());
        assertThat(exercise.getTitle()).isEqualTo("운동명");
    }

    @Test
    @DisplayName("운동 기록 리스트 받아서 저장하기")
    void saveExerciseList() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(MEMBER));
        given(exerciseRepository.save(any(Exercise.class))).willReturn(EXERCISE);

        // when
        exerciseService.saveExercise(MEMBER.getId(), EXERCISE_REQUEST_LIST);

        // then
        then(exerciseRepository).should(times(2)).save(any(Exercise.class));
    }

    @Test
    @DisplayName("운동 기록 단건 수정하기")
    void modifyExercise() {
        // given
        ExerciseRequest exerciseRequest = ExerciseRequest.builder().title("수정 운동명").build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(MEMBER));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(EXERCISE));

        // when
        exerciseService.modifyExercise(MEMBER.getId(), EXERCISE.getId(), exerciseRequest);

        // then
        assertThat(EXERCISE.getTitle()).isEqualTo("수정 운동명");
    }

    @Test
    @DisplayName("운동 기록 단건 삭제하기")
    void deleteExercise() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(MEMBER));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(EXERCISE));

        // when
        exerciseService.deleteExercise(MEMBER.getId(), EXERCISE.getId());

        // then
        then(exerciseRepository).should(times(1)).deleteById(anyLong());
    }

}