package com.sideproject.withpt.application.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.application.career.exception.CareerErrorCode;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.career.repository.CareerQueryRepository;
import com.sideproject.withpt.application.career.repository.CareerRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.domain.trainer.Career;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class CareerQueryServiceTest {

    @Mock
    CareerQueryRepository careerQueryRepository;
    @Mock
    CareerRepository careerRepository;
    @Mock
    TrainerService trainerService;

    @InjectMocks
    CareerQueryService careerQueryService;

    @Test
    void getAllCareers() {

        Long trainerId = 1L;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);

        List<CareerResponse> content = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            content.add(CareerResponse.builder()
                    .id((long) i)
                    .centerName("test" + i)
                    .jobPosition("직책" + i)
                    .startOfWorkYearMonth(YearMonth.of(2022, i))
                    .endOfWorkYearMonth(YearMonth.of(2023, i))
                .build());
        }

        Slice<CareerResponse> testContents = new SliceImpl<>(content, pageRequest, false);
        given(careerQueryRepository.findAllCareerPageableByTrainerId(trainerId, pageRequest))
            .willReturn(testContents);

        Slice<CareerResponse> result = careerQueryService.getAllCareers(trainerId, pageRequest);

        assertThat(result.getSize()).isEqualTo(testContents.getSize());
        assertThat(result.getContent()).extracting("centerName")
            .containsExactly("test1",
                "test2",
                "test3",
                "test4",
                "test5",
                "test6",
                "test7",
                "test8",
                "test9",
                "test10");
    }

    @Test
    void getCareer() {
        //given
        Long trainerId = 1L;
        Long careerId = 11L;

        Trainer trainer = getTrainer(trainerId);
        Career career = getCareerEntity(careerId);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(careerRepository.findByIdAndTrainer(careerId, trainer)).willReturn(Optional.of(career));

        //when
        CareerResponse careerResponse = careerQueryService.getCareer(trainerId, careerId);

        //then
        assertThat(careerResponse.getCenterName()).isEqualTo(career.getCenterName());
        assertThat(careerResponse.getJobPosition()).isEqualTo(career.getJobPosition());
        assertThat(careerResponse.getStartOfWorkYearMonth()).isEqualTo(career.getStartOfWorkYearMonth());
        assertThat(careerResponse.getEndOfWorkYearMonth()).isEqualTo(career.getEndOfWorkYearMonth());
    }
    @Test
    void saveCareer() {
        // given
        Long trainerId = 100L;
        Long careerId = 10L;
        CareerDto careerDto = CareerDto.builder()
            .centerName("test")
            .jobPosition("직책")
            .startOfWorkYearMonth(YearMonth.of(2022, 10))
            .endOfWorkYearMonth(YearMonth.of(2023, 11))
            .build();

        Career career = getCareerEntity(careerId);

        Trainer trainer = getTrainer(trainerId);

        given(trainerService.getTrainerById(trainerId))
            .willReturn(trainer);
        given(careerQueryRepository.existAllColumns(any(Career.class), eq(trainerId)))
            .willReturn(false);

        given(careerRepository.save(any(Career.class))).willReturn(career);

        // when
        CareerResponse careerResponse = careerQueryService.saveCareer(trainerId, careerDto);

        assertThat(careerResponse.getCenterName()).isEqualTo(career.getCenterName());
        assertThat(careerResponse.getJobPosition()).isEqualTo(career.getJobPosition());
        assertThat(careerResponse.getStartOfWorkYearMonth()).isEqualTo(career.getStartOfWorkYearMonth());
        assertThat(careerResponse.getEndOfWorkYearMonth()).isEqualTo(career.getEndOfWorkYearMonth());
    }

    @Test
    void saveDuplicateCareer() {
        // given
        Long trainerId = 100L;
        CareerDto careerDto = CareerDto.builder()
            .centerName("test")
            .jobPosition("직책")
            .startOfWorkYearMonth(YearMonth.of(2022, 10))
            .endOfWorkYearMonth(YearMonth.of(2023, 11))
            .build();

        Trainer trainer = getTrainer(trainerId);

        given(trainerService.getTrainerById(trainerId))
            .willReturn(trainer);
        given(careerQueryRepository.existAllColumns(any(Career.class), eq(trainerId)))
            .willReturn(true);

        // when
        assertThatThrownBy(
            () -> careerQueryService.saveCareer(trainerId, careerDto))
            .isExactlyInstanceOf(CareerException.class)
            .hasMessage(CareerErrorCode.DUPLICATE_CAREER.getMessage());
    }

    @Test
    void editCareer() {
        Long trainerId = 10L;
        Long careerId = 1L;
        Career career = getCareerEntity(careerId);
        Trainer trainer = getTrainer(trainerId);

        CareerEditRequest request = CareerEditRequest.builder()
            .id(careerId)
            .centerName("editTest")
            .startOfWorkYearMonth(String.valueOf(career.getStartOfWorkYearMonth()))
            .endOfWorkYearMonth(String.valueOf(career.getEndOfWorkYearMonth()))
            .build();

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(careerRepository.findByIdAndTrainer(careerId, trainer)).willReturn(Optional.of(career));

        CareerResponse careerResponse = careerQueryService.editCareer(trainerId, request);

        assertThat(careerResponse.getCenterName()).isEqualTo(request.getCenterName());

    }

    private static Trainer getTrainer(Long trainerId) {
        return Trainer.builder()
            .id(trainerId)
            .name("Trainer" + trainerId)
            .careers(new ArrayList<>())
            .build();
    }

    private static Career getCareerEntity(Long careerId) {
        return Career.builder()
            .id(careerId)
            .centerName("test")
            .jobPosition("직책")
            .startOfWorkYearMonth(YearMonth.of(2022, 10))
            .endOfWorkYearMonth(YearMonth.of(2023, 11))
            .build();
    }
}