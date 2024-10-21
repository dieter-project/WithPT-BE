package com.sideproject.withpt.application.record.delegator;


import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.RecordDelegator;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseInfoRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
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
class RecordDelegatorTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseInfoRepository exerciseInfoRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private RecordDelegator recordDelegator;

    @DisplayName("운동 데이터를 저장할 때 북마크 여부에 따라 북마크도 같이 저장된다.")
    @Test
    void saveExerciseAndBookmark() {
        // given
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);
        ExerciseRequest aerobic = ExerciseRequest.builder()
            .uploadDate(uploadDate)
            .title("유산소")
            .exerciseType(AEROBIC)
            .exerciseTime(100)
            .bookmarkYn(true)
            .build();
        ExerciseRequest anaerobic = ExerciseRequest.builder()
            .uploadDate(uploadDate)
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyPart(BodyPart.UPPER_BODY.name())
            .specificBodyParts(List.of(BodyPart.CHEST.name(), BodyPart.SHOULDERS.name(), BodyPart.ARMS.name()))
            .weight(100)
            .times(10)
            .exerciseSet(5)
            .bookmarkYn(false)
            .build();

        ExerciseRequest stretching = ExerciseRequest.builder()
            .uploadDate(uploadDate)
            .title("스트레칭")
            .exerciseType(STRETCHING)
            .bodyPart(BodyPart.FULL_BODY.name())
            .exerciseTime(60)
            .bookmarkYn(true)
            .build();

        List<ExerciseRequest> request = List.of(aerobic, anaerobic, stretching);
        Member member = saveMember();

        // when
        recordDelegator.saveExerciseAndBookmark(member.getId(), request, null, uploadDate);

        // then
        Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);
        assertThat(optionalExercise).isPresent();
        Exercise savedExercise = optionalExercise.get();
        assertThat(savedExercise.getUploadDate()).isEqualTo(uploadDate);

        List<ExerciseInfo> exerciseInfos = exerciseInfoRepository.findAll();
        assertThat(exerciseInfos).hasSize(3)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소", ANAEROBIC),
                tuple("스트레칭", STRETCHING)
            );

        ExerciseInfo savedAerobic = exerciseInfos.get(0);
        assertThat(savedAerobic.getBodyCategory()).isNull();

        ExerciseInfo savedAnaerobic = exerciseInfos.get(1);
        assertThat(savedAnaerobic.getBodyCategory().getName()).isEqualTo(BodyPart.UPPER_BODY);
        assertThat(savedAnaerobic.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .containsExactlyInAnyOrder(
                tuple(BodyPart.CHEST, 2),
                tuple(BodyPart.SHOULDERS, 2),
                tuple(BodyPart.ARMS, 2)
            );

        ExerciseInfo savedStretching = exerciseInfos.get(2);
        assertThat(savedStretching.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);

        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).hasSize(2)
            .extracting("title", "exerciseType")
            .contains(
                tuple("유산소", AEROBIC),
                tuple("스트레칭", STRETCHING)
            );
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