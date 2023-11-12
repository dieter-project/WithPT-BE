package com.sideproject.withpt.application.academic.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.controller.request.AcademicSaveRequest;
import com.sideproject.withpt.application.academic.controller.response.AcademicResponse;
import com.sideproject.withpt.application.academic.exception.AcademicException;
import com.sideproject.withpt.application.academic.repository.AcademicQueryRepository;
import com.sideproject.withpt.application.academic.repository.AcademicRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.trainer.Academic;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class AcademicQueryServiceTest {

    @Mock
    AcademicQueryRepository academicQueryRepository;
    @Mock
    AcademicRepository academicRepository;
    @Mock
    TrainerService trainerService;

    @InjectMocks
    AcademicQueryService academicQueryService;


    @Test
    public void getAll() {
        //given
        Long trainerId = 1L;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);

        List<AcademicResponse> content = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            content.add(createResponse((long) i));
        }

        SliceImpl<AcademicResponse> testContents = new SliceImpl<>(content, pageRequest, false);
        given(academicQueryRepository.findAllAcademicPageableByTrainerId(trainerId, pageRequest))
            .willReturn(testContents);

        //when
        Slice<AcademicResponse> result = academicQueryService.getAllAcademics(trainerId, pageRequest);

        //then
        assertThat(result.getSize()).isEqualTo(testContents.getSize());
        assertThat(result.getContent()).isEqualTo(content);
    }

    @Test
    public void getAcademic() {
        //given
        Long trainerId = 1L;
        Long academicId = 10L;

        Trainer trainer = createTrainer(trainerId);
        Academic academic = createAcademic(academicId);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(academicRepository.findByIdAndTrainer(academicId, trainer)).willReturn(Optional.of(academic));

        //when
        AcademicResponse response = academicQueryService.getAcademic(trainerId, academicId);

        //then
        assertThat(response.getName()).isEqualTo(academic.getName());
        assertThat(response.getMajor()).isEqualTo(academic.getMajor());
        assertThat(response.getEnrollmentYear()).isEqualTo(academic.getEnrollmentYear());
        assertThat(response.getGraduationYear()).isEqualTo(academic.getGraduationYear());
    }

    @Test
    public void save() {
        //given
        Long trainerId = 100L;
        Long academicId = 10L;

        Academic academic = createAcademic(academicId);

        AcademicSaveRequest request = AcademicSaveRequest.builder()
            .name(academic.getName())
            .major(academic.getMajor())
            .enrollmentYear("2023")
            .graduationYear("2025")
            .build();


        Trainer trainer = createTrainer(trainerId);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(academicQueryRepository.existAllColumns(any(Academic.class), eq(trainerId))).willReturn(false);
        given(academicRepository.save(any(Academic.class))).willReturn(academic);

        //when
        AcademicResponse response = academicQueryService.saveAcademic(trainerId, academic);

        //then
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getMajor()).isEqualTo(request.getMajor());
    }

    @Test
    public void saveDuplicateAcademic() {
        //given
        Long trainerId = 100L;
        Long academicId = 10L;

        Academic academic = createAcademic(academicId);

        AcademicSaveRequest request = AcademicSaveRequest.builder()
            .name(academic.getName())
            .major(academic.getMajor())
            .enrollmentYear("2023")
            .graduationYear("2025")
            .build();


        Trainer trainer = createTrainer(trainerId);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(academicQueryRepository.existAllColumns(any(Academic.class), eq(trainerId)))
            .willReturn(true);

        assertThatThrownBy(
            () -> academicQueryService.saveAcademic(trainerId, academic))
            .isInstanceOf(AcademicException.class);
    }

    @Test
    public void edit() {
        //given
        Long trainerId = 10L;
        Long academicId = 1L;
        Academic academic = createAcademic(academicId);
        Trainer trainer = createTrainer(trainerId);

        AcademicEditRequest request = AcademicEditRequest.builder()
            .id(academicId)
            .name("edit name")
            .major("edit major")
            .enrollmentYear("2023")
            .graduationYear("2025")
            .build();

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(academicRepository.findByIdAndTrainer(academicId, trainer)).willReturn(Optional.of(academic));

        //when
        AcademicResponse response = academicQueryService.editAcademic(trainerId, request);

        //then
        assertThat(response.getName()).isEqualTo("edit name");
    }

    public AcademicResponse createResponse(Long academicId) {
        return AcademicResponse.builder()
            .id(academicId)
            .name("test" + academicId)
            .major("major" + academicId)
            .enrollmentYear(Year.of(2023))
            .graduationYear(Year.of(2025))
            .build();
    }

    private Trainer createTrainer(Long trainerId) {
        return Trainer.builder()
            .id(trainerId)
            .name("Trainer" + trainerId)
            .academics(new ArrayList<>())
            .build();
    }

    private Academic createAcademic(Long academicId) {
        return Academic.builder()
            .id(academicId)
            .name("test" + academicId)
            .major("major" + academicId)
            .enrollmentYear(Year.of(2023))
            .graduationYear(Year.of(2025))
            .build();
    }
}