package com.sideproject.withpt.application.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.career.repository.CareerRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.EmploymentStatus;
import com.sideproject.withpt.domain.user.trainer.Career;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class CareerServiceTest {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private CareerService careerService;

    @DisplayName("트레이너 모든 경력 조회")
    @Test
    void getAllCareers() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        careerRepository.saveAll(List.of(
            createCareer("센터1", trainer),
            createCareer("센터2", trainer)
        ));

        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<CareerResponse> responses = careerService.getAllCareers(trainerId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2)
            .extracting("centerName")
            .contains("센터1", "센터2");
    }

    @DisplayName("트레이너 경력 단건 조회")
    @Test
    void getCareer() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        careerRepository.save(createCareer("센터1", trainer));
        Career career = careerRepository.save(createCareer("센터2", trainer));

        Long trainerId = trainer.getId();
        Long careerId = career.getId();

        // when
        CareerResponse response = careerService.getCareer(trainerId, careerId);

        // then
        assertThat(response.getCenterName()).isEqualTo("센터2");
    }

    @DisplayName("이미 입력된 경력 리스트에 트레이너 경력을 추가할 수 있다.")
    @Test
    void saveCareer() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        careerRepository.saveAll(List.of(
            createCareer("센터1", trainer),
            createCareer("센터2", trainer)
        ));

        Career career = Career.builder()
            .centerName("센터3")
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth(YearMonth.of(2022, 1))
            .endOfWorkYearMonth(YearMonth.of(2023, 12))
            .build();

        Long trainerId = trainer.getId();

        // when
        CareerResponse response = careerService.saveCareer(trainerId, career);

        // then
        assertThat(response.getCenterName()).isEqualTo("센터3");

        List<Career> careers = careerRepository.findAll();
        assertThat(careers).hasSize(3);
    }

    @DisplayName("경력 리스트가 비어있을 때 트레이너 경력을 추가할 수 있다.")
    @Test
    void saveCareerWhenCareersEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Career career = Career.builder()
            .centerName("센터3")
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth(YearMonth.of(2022, 1))
            .endOfWorkYearMonth(YearMonth.of(2023, 12))
            .build();

        Long trainerId = trainer.getId();

        // when
        CareerResponse response = careerService.saveCareer(trainerId, career);

        // then
        assertThat(response.getCenterName()).isEqualTo("센터3");

        List<Career> careers = careerRepository.findAll();
        assertThat(careers).hasSize(1);
    }

    @DisplayName("이미 동일한 경력이 있으면 저장할 수 없다.")
    @Test
    void validateDuplicationAllColumn() {
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        careerRepository.save(
            Career.builder()
                .trainer(trainer)
                .centerName("센터3")
                .jobPosition("직책")
                .status(EmploymentStatus.EMPLOYED)
                .startOfWorkYearMonth(YearMonth.of(2022, 1))
                .endOfWorkYearMonth(YearMonth.of(2023, 12))
                .build()
        );

        Career career = Career.builder()
            .centerName("센터3")
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth(YearMonth.of(2022, 1))
            .endOfWorkYearMonth(YearMonth.of(2023, 12))
            .build();

        Long trainerId = trainer.getId();

        // when // then
        assertThatThrownBy(() -> careerService.saveCareer(trainerId, career))
            .isInstanceOf(CareerException.class)
            .hasMessage("이미 동일한 경력사항이 존재합니다.");
    }

    @DisplayName("경력 사항 수정")
    @Test
    void editCareer() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Career career = careerRepository.save(
            Career.builder()
                .trainer(trainer)
                .centerName("센터3")
                .jobPosition("직책")
                .status(EmploymentStatus.EMPLOYED)
                .startOfWorkYearMonth(YearMonth.of(2022, 1))
                .endOfWorkYearMonth(YearMonth.of(2023, 12))
                .build()
        );

        CareerEditRequest request = CareerEditRequest.builder()
            .id(career.getId())
            .centerName("수정된 센터")
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth("2022-01")
            .endOfWorkYearMonth("2023-11")
            .build();

        Long trainerId = trainer.getId();

        // when
        CareerResponse response = careerService.editCareer(trainerId, request);

        // then
        assertThat(response)
            .extracting("centerName", "endOfWorkYearMonth")
            .contains("수정된 센터", YearMonth.of(2023, 11));
    }

    @DisplayName("경력 삭제")
    @Test
    void deleteCareer() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Career career = careerRepository.save(
            Career.builder()
                .trainer(trainer)
                .centerName("센터3")
                .jobPosition("직책")
                .status(EmploymentStatus.EMPLOYED)
                .startOfWorkYearMonth(YearMonth.of(2022, 1))
                .endOfWorkYearMonth(YearMonth.of(2023, 12))
                .build()
        );

        Long trainerId = trainer.getId();
        Long careerId = career.getId();

        // when
        careerService.deleteCareer(trainerId, careerId);

        // then
        Optional<Career> result = careerRepository.findById(careerId);
        assertThat(result).isEmpty();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private Career createCareer(String name, Trainer trainer) {
        return Career.builder()
            .trainer(trainer)
            .centerName(name)
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth(YearMonth.of(2022, 1))
            .endOfWorkYearMonth(YearMonth.of(2023, 12))
            .build();
    }
}