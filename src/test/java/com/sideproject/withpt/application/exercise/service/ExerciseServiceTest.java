package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseResponse;
import com.sideproject.withpt.application.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Bookmark;
import com.sideproject.withpt.domain.record.Exercise;
import com.sideproject.withpt.domain.record.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    List<MultipartFile> multipartFiles = new ArrayList<>();

    @Test
    @DisplayName("오늘 날짜로 운동 리스트 조회하기")
    void findExerciseList() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.findByMemberIdAndExerciseDate(anyLong(), any()))
                .willReturn(List.of(createAddExerciseRequest().toExerciseEntity(createMember())));
        given(imageRepository.findByMemberIdAndUploadDateAndUsage(anyLong(), any(), any()))
                .willReturn(List.of(createImage()));

        // when
        ExerciseListResponse exerciseList = exerciseService.findAllExerciseList(1L, "2023-09-21");

        // then
        then(exerciseRepository).should(times(1)).findByMemberIdAndExerciseDate(anyLong(), any());
        assertThat(exerciseList.getExercise().size()).isEqualTo(1);
        assertThat(exerciseList.getExercise().get(0).getTitle()).isEqualTo("운동명");
    }

    @Test
    @DisplayName("해당하는 운동 리스트 조회하기")
    void findOneExercise() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(createAddExerciseRequest().toExerciseEntity(createMember())));

        // when
        ExerciseResponse exercise = exerciseService.findOneExercise(1L, 1L);

        // then
        then(exerciseRepository).should(times(1)).findById(anyLong());
        assertThat(exercise.getTitle()).isEqualTo("운동명");
    }

    @Test
    @DisplayName("운동 기록 리스트 받아서 저장하기")
    void saveExerciseList() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.save(any(Exercise.class))).willReturn(createAddExerciseRequest().toExerciseEntity(createMember()));

        // when
        exerciseService.saveExercise(1L, List.of(createAddExerciseRequest()), multipartFiles);

        // then
        then(exerciseRepository).should(times(1)).save(any(Exercise.class));
    }

    @Test
    @DisplayName("운동 기록 단건 수정하기")
    void modifyExercise() {
        // given
        ExerciseRequest exerciseRequest = ExerciseRequest.builder().title("수정 운동명").build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(createAddExerciseRequest().toExerciseEntity(createMember())));

        // when
        exerciseService.modifyExercise(1L, 1L, exerciseRequest);
        ExerciseResponse oneExercise = exerciseService.findOneExercise(1L, 1L);

        // then
        assertThat(oneExercise.getTitle()).isEqualTo("수정 운동명");
    }

    @Test
    @DisplayName("운동 기록 단건 삭제하기")
    void deleteExercise() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.findById(anyLong())).willReturn(Optional.of(createAddExerciseRequest().toExerciseEntity(createMember())));

        // when
        exerciseService.deleteExercise(1L, 1L);

        // then
        then(exerciseRepository).should(times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("운동 입력시 북마크로 저장하기")
    void saveExerciseBookmark() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(exerciseRepository.save(any(Exercise.class)))
                .willReturn(createAddExerciseRequest().toExerciseEntity(createMember()));

        // when
        exerciseService.saveExercise(1L, List.of(createAddExerciseRequest()), multipartFiles);

        // then
        then(bookmarkRepository).should(times(1)).save(any(Bookmark.class));
    }

    private ExerciseRequest createAddExerciseRequest() {
        return ExerciseRequest.builder()
                .title("운동명")
                .weight(300)
                .set(3)
                .hour(3)
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .exerciseDate(LocalDate.parse("2023-09-21"))
                .bookmarkYn("Y")
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .nickname("test")
                .build();
    }

    private Image createImage() {
        return Image.builder()
                .id(1L)
                .entityId(1L)
                .url("test")
                .attachType("jpg")
                .usage(Usages.EXERCISE)
                .build();
    }

}