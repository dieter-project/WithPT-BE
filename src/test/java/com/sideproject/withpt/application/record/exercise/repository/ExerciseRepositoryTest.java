package com.sideproject.withpt.application.record.exercise.repository;

import static com.sideproject.withpt.application.type.BodyPart.ABS;
import static com.sideproject.withpt.application.type.BodyPart.BACK;
import static com.sideproject.withpt.application.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.application.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.application.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ExerciseRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @DisplayName("업로드 날짜에 해당하는 운동 정보를 조회할 수 있다.")
    @Test
    void findFirstByMemberAndUploadDate() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, List.of(ABS, BACK), 100, 10, 5, 0);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, List.of(ABS, BACK), 0, 0, 0, 60);
        Exercise exercise = createExercise(member, uploadDate, List.of(aerobic, anaerobic, stretching));

        exerciseRepository.save(exercise);

        // when
        Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);

        // then
        assertThat(optionalExercise).isPresent();
        assertThat(optionalExercise.get().getUploadDate()).isEqualTo(uploadDate);
        assertThat(optionalExercise.get().getExerciseInfos()).hasSize(3)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소", ANAEROBIC),
                tuple("스트레칭", STRETCHING)
            );
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