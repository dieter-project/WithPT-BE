package com.sideproject.withpt.application.record.exercise.service;


import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseEditRequest;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseInfoResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseInfoRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ExerciseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseInfoRepository exerciseInfoRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ExerciseService exerciseService;

    @DisplayName("요청하는 날짜의 운동 기록 조회")
    @Test
    void findExerciseAndExerciseInfos() {
        // given
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

        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        ExerciseInfo anaerobic1 = createExerciseInfo("무산소_전신", ANAEROBIC, FULL_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic2 = createExerciseInfo("무산소_상체", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic3 = createExerciseInfo("무산소_하체", ANAEROBIC, LOWER_BODY, 100, 10, 5, 0);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, FULL_BODY, 0, 0, 0, 60);
        Exercise exercise = createExercise(member, uploadDate, List.of(aerobic, anaerobic1, anaerobic2, anaerobic3, stretching));

        exerciseRepository.save(exercise);

        // when
        ExerciseResponse exerciseResponse = exerciseService.findExerciseAndExerciseInfos(member.getId(), uploadDate);

        // then
        assertThat(exerciseResponse.getUploadDate()).isEqualTo(uploadDate);
        assertThat(exerciseResponse.getExerciseInfos()).hasSize(5)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소_전신", ANAEROBIC),
                tuple("무산소_상체", ANAEROBIC),
                tuple("무산소_하체", ANAEROBIC),
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

        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        ExerciseInfo anaerobic1 = createExerciseInfo("무산소_전신", ANAEROBIC, FULL_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic2 = createExerciseInfo("무산소_상체", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic3 = createExerciseInfo("무산소_하체", ANAEROBIC, LOWER_BODY, 100, 10, 5, 0);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, FULL_BODY, 0, 0, 0, 60);
        List<ExerciseInfo> exerciseInfos = List.of(aerobic, anaerobic1, anaerobic2, anaerobic3, stretching);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise exercise = createExercise(member, uploadDate, exerciseInfos);
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

    @Nested
    @DisplayName("upload 날짜 운동 데이터가 존재하지 않을 때 운동 요청 데이터들이 저장된다.")
    class SaveExercise {
        @DisplayName("upload 날짜 운동 데이터가 존재하지 않을 때 운동 요청 데이터들이 저장된다.")
        @Test
        void saveExerciseWhenExerciseDatsIsNotSavedInDB() {
            // given
            LocalDate uploadDate = LocalDate.of(2024, 9, 3);
            ExerciseRequest aerobic = ExerciseRequest.builder()
                .uploadDate(uploadDate)
                .title("유산소")
                .exerciseType(AEROBIC)
                .exerciseTime(100)
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
                .build();

            ExerciseRequest stretching = ExerciseRequest.builder()
                .uploadDate(uploadDate)
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            List<ExerciseRequest> request = List.of(aerobic, anaerobic, stretching);
            Member member = saveMember();

            // when
            exerciseService.saveExercise(member.getId(), request, null, uploadDate);

            // then
            Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);
            assertThat(optionalExercise).isPresent();

            Exercise savedExercise = optionalExercise.get();
            assertThat(savedExercise.getUploadDate()).isEqualTo(uploadDate);
            assertThat(savedExercise.getExerciseInfos()).hasSize(3)
                .extracting("title", "exerciseType")
                .containsExactlyInAnyOrder(
                    tuple("유산소", AEROBIC),
                    tuple("무산소", ANAEROBIC),
                    tuple("스트레칭", STRETCHING)
                );

            ExerciseInfo savedAerobic = savedExercise.getExerciseInfos().get(0);
            assertThat(savedAerobic.getBodyCategory()).isNull();

            ExerciseInfo savedAnaerobic = savedExercise.getExerciseInfos().get(1);
            assertThat(savedAnaerobic.getBodyCategory().getName()).isEqualTo(BodyPart.UPPER_BODY);
            assertThat(savedAnaerobic.getBodyCategory().getChildren()).hasSize(3)
                .extracting("name", "depth")
                .containsExactlyInAnyOrder(
                    tuple(BodyPart.CHEST, 2),
                    tuple(BodyPart.SHOULDERS, 2),
                    tuple(BodyPart.ARMS, 2)
                );

            ExerciseInfo savedStretching = savedExercise.getExerciseInfos().get(2);
            assertThat(savedStretching.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);
        }

        @DisplayName("upload 날짜 운동 데이터가 존재할때 운동 요청 데이터들이 추가된다.")
        @Test
        void saveExerciseWhenExerciseDatsExistInDB() {
            // given
            List<BodyCategory> childBodyCategory1 = List.of(
                createChildBodyCategory(BodyPart.CHEST),
                createChildBodyCategory(BodyPart.SHOULDERS),
                createChildBodyCategory(BodyPart.ARMS)
            );
            BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory1);
            BodyCategory LOWER_BODY = createParentBodyCategory(BodyPart.LOWER_BODY, null);

            Member member = saveMember();
            LocalDate uploadDate = LocalDate.of(2024, 9, 3);

            ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
            ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
            ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, LOWER_BODY, 0, 0, 0, 60);
            List<ExerciseInfo> exerciseInfos = List.of(aerobic, anaerobic, stretching);
            exerciseInfoRepository.saveAll(exerciseInfos);

            Exercise exercise = createExercise(member, uploadDate, exerciseInfos);
            exerciseRepository.save(exercise);

            log.info("==== 운동 데이터 저장 ====");

            ExerciseRequest aerobicRequest = ExerciseRequest.builder()
                .uploadDate(uploadDate)
                .title("유산소")
                .exerciseType(AEROBIC)
                .exerciseTime(100)
                .build();
            ExerciseRequest anaerobicRequest = ExerciseRequest.builder() // 무산소는 부위 다중 선택 가능
                .uploadDate(uploadDate)
                .title("무산소")
                .exerciseType(ANAEROBIC)
                .bodyPart(BodyPart.UPPER_BODY.name())
                .specificBodyParts(List.of(BodyPart.CHEST.name(), BodyPart.SHOULDERS.name(), BodyPart.ARMS.name()))
                .weight(100)
                .times(10)
                .exerciseSet(5)
                .build();

            ExerciseRequest stretchingRequest = ExerciseRequest.builder()
                .uploadDate(uploadDate)
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            List<ExerciseRequest> request = List.of(aerobicRequest, anaerobicRequest, stretchingRequest);

            // when
            exerciseService.saveExercise(member.getId(), request, null, uploadDate);

            // then
            Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);
            assertThat(optionalExercise).isPresent();

            Exercise savedExercise = optionalExercise.get();
            assertThat(savedExercise.getUploadDate()).isEqualTo(uploadDate);
            assertThat(savedExercise.getExerciseInfos()).hasSize(6)
                .extracting("title", "exerciseType")
                .containsExactlyInAnyOrder(
                    tuple("유산소", AEROBIC),
                    tuple("무산소", ANAEROBIC),
                    tuple("스트레칭", STRETCHING),
                    tuple("유산소", AEROBIC),
                    tuple("무산소", ANAEROBIC),
                    tuple("스트레칭", STRETCHING)
                );
        }

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
            exerciseService.saveExerciseAndBookmark(member.getId(), request, null, uploadDate);

            // then
            Optional<Exercise> optionalExercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate);
            assertThat(optionalExercise).isPresent();

            Exercise savedExercise = optionalExercise.get();
            assertThat(savedExercise.getUploadDate()).isEqualTo(uploadDate);
            assertThat(savedExercise.getExerciseInfos()).hasSize(3)
                .extracting("title", "exerciseType")
                .containsExactlyInAnyOrder(
                    tuple("유산소", AEROBIC),
                    tuple("무산소", ANAEROBIC),
                    tuple("스트레칭", STRETCHING)
                );

            ExerciseInfo savedAerobic = savedExercise.getExerciseInfos().get(0);
            assertThat(savedAerobic.getBodyCategory()).isNull();

            ExerciseInfo savedAnaerobic = savedExercise.getExerciseInfos().get(1);
            assertThat(savedAnaerobic.getBodyCategory().getName()).isEqualTo(BodyPart.UPPER_BODY);
            assertThat(savedAnaerobic.getBodyCategory().getChildren()).hasSize(3)
                .extracting("name", "depth")
                .containsExactlyInAnyOrder(
                    tuple(BodyPart.CHEST, 2),
                    tuple(BodyPart.SHOULDERS, 2),
                    tuple(BodyPart.ARMS, 2)
                );

            ExerciseInfo savedStretching = savedExercise.getExerciseInfos().get(2);
            assertThat(savedStretching.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);

            List<Bookmark> bookmarks = bookmarkRepository.findAll();
            assertThat(bookmarks).hasSize(2)
                .extracting("title", "exerciseType")
                .contains(
                    tuple("유산소", AEROBIC),
                    tuple("스트레칭", STRETCHING)
                );
        }
    }

    /**
     * 유산소 -> 무산소 수정 부위, 상세 부위 insert
     */
    @DisplayName("유산소 -> 무산소 수정 : 부위, 상세 부위 새로 저장")
    @Test
    void modifyExercise_from_AEROBIC_to_ANAEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        List<ExerciseInfo> exerciseInfos = List.of(aerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyPart(BodyPart.UPPER_BODY.name())
            .specificBodyParts(List.of(BodyPart.CHEST.name(), BodyPart.SHOULDERS.name(), BodyPart.ARMS.name()))
            .weight(100)
            .times(10)
            .exerciseSet(5)
            .build();
        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "weight", "exerciseSet", "times", "exerciseTime")
            .contains("무산소", ANAEROBIC, 100, 10, 5, 0);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.UPPER_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
        assertThat(exerciseInfo.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .contains(
                tuple(BodyPart.CHEST, 2),
                tuple(BodyPart.SHOULDERS, 2),
                tuple(BodyPart.ARMS, 2)
            );
    }

    /**
     * 유산소 -> 스트레칭 부위 insert
     */
    @DisplayName("유산소 -> 스트레칭 수정 : 부위, 상세 부위 새로 저장")
    @Test
    void modifyExercise_from_AEROBIC_to_STRETCHING() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        List<ExerciseInfo> exerciseInfos = List.of(aerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise exercise = createExercise(member, uploadDate, exerciseInfos);
        Exercise savedExercise = exerciseRepository.save(exercise);

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("스트레칭")
            .exerciseType(STRETCHING)
            .bodyPart(BodyPart.FULL_BODY.name())
            .exerciseTime(60)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("스트레칭", STRETCHING, 60);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
    }

    /**
     * 유산소 -> 유산소 운동 기록 or 운동명 수정
     */
    @DisplayName("유산소 -> 유산소 수정 : 운동 기록, 운동명만 수정")
    @Test
    void modifyExercise_from_AEROBIC_to_AEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        List<ExerciseInfo> exerciseInfos = List.of(aerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise exercise = createExercise(member, uploadDate, exerciseInfos);
        Exercise savedExercise = exerciseRepository.save(exercise);

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("유산소_수정")
            .exerciseType(AEROBIC)
            .exerciseTime(777)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("유산소_수정", AEROBIC, 777);

        assertThat(exerciseInfo.getBodyCategory()).isNull();
    }

    /**
     * 무산소 -> 유산소 기존 부위, 상세 부위 삭제
     */
    @DisplayName("무산소 -> 유산소 수정 : 기존 부위, 상세 부위 삭제")
    @Test
    void modifyExercise_from_ANAEROBIC_to_AEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        List<BodyCategory> childBodyCategory = List.of(
            createChildBodyCategory(BodyPart.CHEST),
            createChildBodyCategory(BodyPart.SHOULDERS),
            createChildBodyCategory(BodyPart.ARMS)
        );
        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory);
        ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        List<ExerciseInfo> exerciseInfos = List.of(anaerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("유산소_수정")
            .exerciseType(AEROBIC)
            .exerciseTime(777)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("유산소_수정", AEROBIC, 777);

        assertThat(exerciseInfo.getBodyCategory()).isNull();
    }

    /**
     * 무산소 -> 스트레칭 부위 업데이트, 상세 부위 있으면 삭제
     */
    @DisplayName("무산소 -> 스트레칭 수정 : 부위 업데이트, 상세 부위 있으면 삭제")
    @Test
    void modifyExercise_from_ANAEROBIC_to_STRETCHING() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        List<BodyCategory> childBodyCategory = List.of(
            createChildBodyCategory(BodyPart.CHEST),
            createChildBodyCategory(BodyPart.SHOULDERS),
            createChildBodyCategory(BodyPart.ARMS)
        );
        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory);
        ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        List<ExerciseInfo> exerciseInfos = List.of(anaerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("스트레칭")
            .exerciseType(STRETCHING)
            .bodyPart(BodyPart.FULL_BODY.name())
            .exerciseTime(60)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("스트레칭", STRETCHING, 60);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.FULL_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
        assertThat(exerciseInfo.getBodyCategory().getChildren()).isEmpty();
    }

    @DisplayName("무산소 -> 무산소 수정 : 부위, 상세 부위 있으면 업데이트")
    @Test
    void modifyExercise_from_ANAEROBIC_to_ANAEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        List<BodyCategory> childBodyCategory = List.of(
            createChildBodyCategory(BodyPart.CHEST),
            createChildBodyCategory(BodyPart.SHOULDERS),
            createChildBodyCategory(BodyPart.ARMS)
        );
        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, childBodyCategory);
        ExerciseInfo anaerobic = createExerciseInfo("무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        List<ExerciseInfo> exerciseInfos = List.of(anaerobic);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyPart(BodyPart.LOWER_BODY.name())
            .specificBodyParts(List.of(BodyPart.GLUTES.name(), BodyPart.QUADRICEPS.name(), BodyPart.HAMSTRINGS.name()))
            .weight(150)
            .times(20)
            .exerciseSet(10)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "weight", "exerciseSet", "times", "exerciseTime")
            .contains("무산소", ANAEROBIC, 150, 20, 10, 0);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.LOWER_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
        assertThat(exerciseInfo.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .contains(
                tuple(BodyPart.GLUTES, 2),
                tuple(BodyPart.QUADRICEPS, 2),
                tuple(BodyPart.HAMSTRINGS, 2)
            );
    }

    /**
     * 스트레칭 -> 유산소 부위 삭제
     */
    @DisplayName("스트레칭 -> 유산소 수정 : 기존 부위 삭제")
    @Test
    void modifyExercise_from_STRETCHING_to_AEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, null);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, UPPER_BODY, 0, 0, 0, 60);
        List<ExerciseInfo> exerciseInfos = List.of(stretching);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("유산소_수정")
            .exerciseType(AEROBIC)
            .exerciseTime(777)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("유산소_수정", AEROBIC, 777);

        assertThat(exerciseInfo.getBodyCategory()).isNull();
    }

    /**
     * 스트레칭 -> 무산소 부위 업데이트, 상세 부위 있으면 추가
     */
    @DisplayName("스트레칭 -> 무산소 수정 : 부위 업데이트, 상세 부위 있으면 추가")
    @Test
    void modifyExercise_from_STRETCHING_to_ANAEROBIC() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, null);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, UPPER_BODY, 0, 0, 0, 60);
        List<ExerciseInfo> exerciseInfos = List.of(stretching);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyPart(BodyPart.LOWER_BODY.name())
            .specificBodyParts(List.of(BodyPart.GLUTES.name(), BodyPart.QUADRICEPS.name(), BodyPart.HAMSTRINGS.name()))
            .weight(150)
            .times(20)
            .exerciseSet(10)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "weight", "exerciseSet", "times", "exerciseTime")
            .contains("무산소", ANAEROBIC, 150, 20, 10, 0);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.LOWER_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
        assertThat(exerciseInfo.getBodyCategory().getChildren()).hasSize(3)
            .extracting("name", "depth")
            .contains(
                tuple(BodyPart.GLUTES, 2),
                tuple(BodyPart.QUADRICEPS, 2),
                tuple(BodyPart.HAMSTRINGS, 2)
            );
    }

    /**
     * 스트레칭 -> 무산소 부위 업데이트, 상세 부위 있으면 추가
     */
    @DisplayName("스트레칭 -> 스트레칭 수정 : 부위, 운동명, 시간 업데이트")
    @Test
    void modifyExercise_from_STRETCHING_to_STRETCHING() {
        // given
        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        BodyCategory UPPER_BODY = createParentBodyCategory(BodyPart.UPPER_BODY, null);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, UPPER_BODY, 0, 0, 0, 60);
        List<ExerciseInfo> exerciseInfos = List.of(stretching);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise savedExercise = exerciseRepository.save(createExercise(member, uploadDate, exerciseInfos));

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(0).getId();
        final ExerciseEditRequest request = ExerciseEditRequest.builder()
            .title("스트레칭_수정")
            .exerciseType(STRETCHING)
            .bodyPart(BodyPart.LOWER_BODY.name())
            .exerciseTime(666)
            .build();

        // when
        exerciseService.modifyExercise(exerciseId, exerciseInfoId, request);

        // then
        ExerciseInfo exerciseInfo = exerciseRepository.findExerciseInfoById(exerciseInfoId).get();

        assertThat(exerciseInfo)
            .extracting("title", "exerciseType", "exerciseTime")
            .contains("스트레칭_수정", STRETCHING, 666);

        assertThat(exerciseInfo.getBodyCategory().getName()).isEqualTo(BodyPart.LOWER_BODY);
        assertThat(exerciseInfo.getBodyCategory().getDepth()).isEqualTo(1);
        assertThat(exerciseInfo.getBodyCategory().getChildren()).isEmpty();
    }

    @DisplayName("단일 운동 정보 삭제")
    @Test
    void deleteExerciseInfo() {
        // given
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

        Member member = saveMember();
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        ExerciseInfo aerobic = createExerciseInfo("유산소", AEROBIC, null, 0, 0, 0, 100);
        ExerciseInfo anaerobic1 = createExerciseInfo("무산소_전신", ANAEROBIC, FULL_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic2 = createExerciseInfo("무산소_상체", ANAEROBIC, UPPER_BODY, 100, 10, 5, 0);
        ExerciseInfo anaerobic3 = createExerciseInfo("무산소_하체", ANAEROBIC, LOWER_BODY, 100, 10, 5, 0);
        ExerciseInfo stretching = createExerciseInfo("스트레칭", STRETCHING, FULL_BODY, 0, 0, 0, 60);
        List<ExerciseInfo> exerciseInfos = List.of(aerobic, anaerobic1, anaerobic2, anaerobic3, stretching);
        exerciseInfoRepository.saveAll(exerciseInfos);

        Exercise exercise = createExercise(member, uploadDate, exerciseInfos);
        Exercise savedExercise = exerciseRepository.save(exercise);

        final Long exerciseId = savedExercise.getId();
        final Long exerciseInfoId = savedExercise.getExerciseInfos().get(2).getId();

        // when
        exerciseService.deleteExerciseInfo(exerciseId, exerciseInfoId);

        // then
        Exercise findExercise = exerciseRepository.findById(exerciseId).get();

        assertThat(findExercise.getUploadDate()).isEqualTo(uploadDate);
        assertThat(findExercise.getExerciseInfos()).hasSize(4)
            .extracting("title", "exerciseType")
            .containsExactlyInAnyOrder(
                tuple("유산소", AEROBIC),
                tuple("무산소_전신", ANAEROBIC),
                tuple("무산소_하체", ANAEROBIC),
                tuple("스트레칭", STRETCHING)
            );
    }

    private Exercise createExercise(Member member, LocalDate uploadDate, List<ExerciseInfo> exerciseInfos) {
        return Exercise.builder()
            .member(member)
            .uploadDate(uploadDate)
            .exerciseInfos(exerciseInfos)
            .build();
    }

    private ExerciseInfo createExerciseInfo(String title, ExerciseType exerciseType, BodyCategory bodyCategory, int weight, int exerciseSet,
        int times, int exerciseTime) {
        return ExerciseInfo.builder() // 유산소는 운동 시간만 작성
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(bodyCategory)
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
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