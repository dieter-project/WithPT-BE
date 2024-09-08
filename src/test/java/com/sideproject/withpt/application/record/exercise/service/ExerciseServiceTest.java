package com.sideproject.withpt.application.record.exercise.service;


import static com.sideproject.withpt.application.type.BodyPart.ABS;
import static com.sideproject.withpt.application.type.BodyPart.BACK;
import static com.sideproject.withpt.application.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.application.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.application.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseInfoResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ExerciseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseService exerciseService;

    @DisplayName("요청하는 날짜의 운동 기록 조회")
    @Test
    void findExerciseAndExerciseInfos() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, List.of(ABS, BACK), 100, 10, 5, 0);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, List.of(ABS, BACK), 0, 0, 0, 60);
        Exercise exercise = createExercise(member, uploadDate, List.of(aerobic, anaerobic, stretching));
        exerciseRepository.save(exercise);

        // when
        ExerciseResponse exerciseResponse = exerciseService.findExerciseAndExerciseInfos(member.getId(), uploadDate);

        // then
        assertThat(exerciseResponse.getUploadDate()).isEqualTo(uploadDate);
        assertThat(exerciseResponse.getExerciseInfos()).hasSize(3)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소", ANAEROBIC),
                tuple("스트레칭", STRETCHING)
            );
    }

    @DisplayName("요청하는 날짜의 운동 기록 자체가 null 일 수 있다.")
    @Test
    void findExerciseAndExerciseInfosWhenExerciseDataIsEmpty() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        // when
        ExerciseResponse exerciseResponse = exerciseService.findExerciseAndExerciseInfos(member.getId(), uploadDate);

        // then
        assertThat(exerciseResponse).isNull();
    }

    @DisplayName("날짜별 운동 데이터들이 있을 시 운동 정보 단건 조회가 가능하다.")
    @Test
    void findOneExerciseInfo() {
        //given
        ExerciseInfo aerobic = ExerciseInfo.builder() // 유산소는 운동 시간만 작성
            .title("유산소")
            .exerciseType(AEROBIC)
            .exerciseTime(100)
            .build();

        ExerciseInfo anaerobic = ExerciseInfo.builder() // 무산소는 부위 다중 선택 가능
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyParts(List.of(ABS, BACK))
            .weight(100)
            .times(10)
            .exerciseSet(5)
            .build();

        ExerciseInfo stretching = ExerciseInfo.builder()
            .title("스트레칭")
            .exerciseType(STRETCHING)
            .bodyParts(List.of(ABS, BACK))
            .exerciseTime(60)
            .build();

        LocalDate uploadDate = LocalDate.of(2024, 9, 3);
        Exercise exercise = Exercise.builder()
            .uploadDate(uploadDate)
            .exerciseInfos(List.of(aerobic, anaerobic, stretching))
            .build();

        Exercise savedExercise = exerciseRepository.save(exercise);
        Long savedAerobicId = savedExercise.getExerciseInfos().get(0).getId();

        //when
        ExerciseInfoResponse response = exerciseService.findOneExerciseInfo(savedExercise.getId(), savedAerobicId);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getUploadDate()).isEqualTo(uploadDate);
        assertThat(response.getExerciseInfo())
            .extracting("title", "exerciseType")
            .contains("유산소", AEROBIC);
    }

    private Exercise createExercise(Member member, LocalDate uploadDate, List<ExerciseInfo> exerciseInfos) {
        return Exercise.builder()
            .member(member)
            .uploadDate(uploadDate)
            .exerciseInfos(exerciseInfos)
            .build();
    }

    private ExerciseInfo createExerciseInfo(String title, ExerciseType exerciseType, List<BodyPart> bodyParts, int weight, int exerciseSet, int times,
        int exerciseTime) {
        return ExerciseInfo.builder() // 유산소는 운동 시간만 작성
            .title(title)
            .exerciseType(exerciseType)
            .bodyParts(bodyParts)
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
    }

    private Member saveMember() {
        return memberRepository.save(
            Member.builder()
                .email("test@test.com")
                .name("member1")
                .dietType(DietType.DIET)
                .role(Role.MEMBER)
                .build()
        );
    }
}