package com.sideproject.withpt.application.record.exercise.repository;

import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ExerciseRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseInfoRepository exerciseInfoRepository;

    @DisplayName("업로드 날짜에 해당하는 운동 정보를 조회할 수 있다.")
    @Test
    void findFirstByMemberAndUploadDate() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);
        Exercise exercise = exerciseRepository.save(createExercise(member, uploadDate));

        BodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);

        List<BodyCategory> childBodyCategory1 = List.of(
            createChildBodyCategory(BodyPart.CHEST),
            createChildBodyCategory(BodyPart.SHOULDERS),
            createChildBodyCategory(BodyPart.ARMS)
        );
        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory1);

        List<BodyCategory> childBodyCategory2 = List.of(
            createChildBodyCategory(BodyPart.GLUTES),
            createChildBodyCategory(BodyPart.QUADRICEPS),
            createChildBodyCategory(BodyPart.HAMSTRINGS)
        );
        BodyCategory LOWER_BODY = createParentBodyCategory(BodyPart.LOWER_BODY, childBodyCategory2);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100, exercise);
        ExerciseInfo anaerobic1 = createExerciseInfo("무산소_전신", ANAEROBIC, FULL_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo anaerobic2 = createExerciseInfo("무산소_상체", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo anaerobic3 = createExerciseInfo("무산소_하체", ANAEROBIC, LOWER_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, FULL_BODY, 0, 0, 0, 60, exercise);

        exerciseInfoRepository.saveAll(List.of(aerobic, anaerobic1, anaerobic2, anaerobic3, stretching));

        // when
        Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);

        // then
        assertThat(optionalExercise).isPresent();
        Exercise savedExercise = optionalExercise.get();
        assertThat(savedExercise.getUploadDate()).isEqualTo(uploadDate);

        List<ExerciseInfo> exerciseInfos = exerciseInfoRepository.findAll();
        assertThat(exerciseInfos).hasSize(5)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소_전신", ANAEROBIC),
                tuple("무산소_상체", ANAEROBIC),
                tuple("무산소_하체", ANAEROBIC),
                tuple("스트레칭", STRETCHING)
            );

        log.info("유산소 검증");
        ExerciseInfo savedAerobic = exerciseInfos.get(0);
        assertThat(savedAerobic.getBodyCategory()).isNull();

        log.info("무산소_전신 검증");
        ExerciseInfo savedAnaerobic1 = exerciseInfos.get(1);
        assertThat(savedAnaerobic1.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);
        assertThat(savedAnaerobic1.getBodyCategory().getChildren()).isEmpty();

        log.info("무산소_상체 검증");
        ExerciseInfo savedAnaerobic2 = exerciseInfos.get(2);
        assertThat(savedAnaerobic2.getBodyCategory().getName()).isEqualTo(BodyPart.UPPER_BODY);
        assertThat(savedAnaerobic2.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .containsExactlyInAnyOrder(
                tuple(BodyPart.CHEST, 2),
                tuple(BodyPart.SHOULDERS, 2),
                tuple(BodyPart.ARMS, 2)
            );

        log.info("무산소_하체 검증");
        ExerciseInfo savedAnaerobic3 = exerciseInfos.get(3);
        assertThat(savedAnaerobic3.getBodyCategory().getName()).isEqualTo(BodyPart.LOWER_BODY);
        assertThat(savedAnaerobic3.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .containsExactlyInAnyOrder(
                tuple(BodyPart.GLUTES, 2),
                tuple(BodyPart.QUADRICEPS, 2),
                tuple(BodyPart.HAMSTRINGS, 2)
            );

        log.info("스트레칭 검증");
        ExerciseInfo savedStretching = exerciseInfos.get(4);
        assertThat(savedStretching.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);
        assertThat(savedStretching.getBodyCategory().getChildren()).hasSize(0);
    }

    @DisplayName("업로드 날짜에 해당하는 운동 정보가 존재하지 않을 수 있다.")
    @Test
    void findFirstByMemberAndUploadDateWhenDataIsEmpty() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        // when
        Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);

        // then
        assertThat(optionalExercise).isEmpty();
    }

    @DisplayName("운동 종류 별로 데이터가 정상적으로 저장되는지 확인")
    @Test
    void saveExerciseTest() {
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);
        Exercise exercise = exerciseRepository.save(createExercise(member, uploadDate));

        BodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);

        List<BodyCategory> childBodyCategory1 = List.of(
            createChildBodyCategory(BodyPart.CHEST),
            createChildBodyCategory(BodyPart.SHOULDERS),
            createChildBodyCategory(BodyPart.ARMS)
        );
        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory1);

        List<BodyCategory> childBodyCategory2 = List.of(
            createChildBodyCategory(BodyPart.GLUTES),
            createChildBodyCategory(BodyPart.QUADRICEPS),
            createChildBodyCategory(BodyPart.HAMSTRINGS)
        );
        BodyCategory LOWER_BODY = createParentBodyCategory(BodyPart.LOWER_BODY, childBodyCategory2);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100, exercise);
        ExerciseInfo anaerobic1 = createExerciseInfo("무산소_전신", ANAEROBIC, FULL_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo anaerobic2 = createExerciseInfo("무산소_상체", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo anaerobic3 = createExerciseInfo("무산소_하체", ANAEROBIC, LOWER_BODY, 100, 10, 5, 0, exercise);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, FULL_BODY, 0, 0, 0, 60, exercise);

        exerciseInfoRepository.saveAll(List.of(aerobic, anaerobic1, anaerobic2, anaerobic3, stretching));
    }

    private Exercise createExercise(Member member, LocalDate uploadDate) {
        return Exercise.builder()
            .member(member)
            .uploadDate(uploadDate)
            .build();
    }

    private ExerciseInfo createExerciseInfo(String title, ExerciseType exerciseType, BodyCategory bodyCategory, int weight, int exerciseSet, int times, int exerciseTime, Exercise exercise) {

        ExerciseInfo exerciseInfo = ExerciseInfo.builder() // 유산소는 운동 시간만 작성
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(bodyCategory)
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
        exerciseInfo.addExercise(exercise);

        return exerciseInfo;
    }

    private BodyCategory createParentBodyCategory(BodyPart bodyPart, List<BodyCategory> children) {
        return BodyCategory.builder()
            .name(bodyPart)
            .children(children)
            .build();
    }

    private BodyCategory createChildBodyCategory(BodyPart bodyPart) {
        return BodyCategory.builder()
            .name(bodyPart)
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