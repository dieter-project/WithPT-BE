package com.sideproject.withpt.application.education.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.education.controller.reponse.EducationResponse;
import com.sideproject.withpt.application.education.controller.request.EducationEditRequest;
import com.sideproject.withpt.application.education.exception.EducationException;
import com.sideproject.withpt.application.education.repository.EducationRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.trainer.Education;
import com.sideproject.withpt.domain.trainer.Trainer;
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
class EducationServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private EducationService educationService;

    @DisplayName("트레이너 모든 교육내역 조회")
    @Test
    void getAllEducations() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<EducationResponse> responses = educationService.getAllEducations(trainerId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2)
            .extracting("name")
            .contains("교육1", "교육2");
    }

    @DisplayName("트레이너 교육내역 단건 조회")
    @Test
    void getEducation() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        List<Education> educations = educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        Long trainerId = trainer.getId();
        Long educationId = educations.get(0).getId();

        // when
        EducationResponse response = educationService.getEducation(trainerId, educationId);

        // then
        assertThat(response.getName()).isEqualTo("교육1");
    }

    @DisplayName("이미 입력된 교육내역 리스트에 트레이너 교육내역을 추가할 수 있다.")
    @Test
    void saveEducation() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        Education education = Education.builder()
            .name("교육3")
            .institution("교육기관")
            .acquisitionYearMonth(YearMonth.of(2023, 10))
            .build();

        Long trainerId = trainer.getId();

        // when
        EducationResponse response = educationService.saveEducation(trainerId, education);

        // then
        assertThat(response.getName()).isEqualTo("교육3");

        List<Education> educations = educationRepository.findAll();
        assertThat(educations).hasSize(3);
    }

    @DisplayName("교육내역 리스트가 비어있을 때 트레이너 교육내역을 추가할 수 있다.")
    @Test
    void saveEducationWhenEducationsEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Education education = Education.builder()
            .name("교육3")
            .institution("교육기관")
            .acquisitionYearMonth(YearMonth.of(2023, 10))
            .build();

        Long trainerId = trainer.getId();

        // when
        EducationResponse response = educationService.saveEducation(trainerId, education);

        // then
        assertThat(response.getName()).isEqualTo("교육3");

        List<Education> educations = educationRepository.findAll();
        assertThat(educations).hasSize(1);
    }

    @DisplayName("이미 동일한 교육 내역이 있으면 저장할 수 없다.")
    @Test
    void validateDuplicationAllColumn() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        Education education = Education.builder()
            .name("교육2")
            .institution("교육기관")
            .acquisitionYearMonth(YearMonth.of(2023, 10))
            .build();

        Long trainerId = trainer.getId();

        // when // then
        assertThatThrownBy(() -> educationService.saveEducation(trainerId, education))
            .isInstanceOf(EducationException.class)
            .hasMessage("이미 동일한 교육 내역이 존재합니다.");
    }

    @DisplayName("교육 내역 수정")
    @Test
    void editEducation() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        List<Education> educations = educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        EducationEditRequest request = EducationEditRequest.builder()
            .id(educations.get(0).getId())
            .name("수정된 교육")
            .institution("교육기관")
            .acquisitionYearMonth("2023-11")
            .build();

        Long trainerId = trainer.getId();

        // when
        EducationResponse response = educationService.editEducation(trainerId, request);

        // then
        assertThat(response.getName()).isEqualTo("수정된 교육");
    }

    @DisplayName("교육내역 삭제")
    @Test
    void deleteEducation() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        List<Education> educations = educationRepository.saveAll(List.of(
            createEducation("교육1", trainer),
            createEducation("교육2", trainer)
        ));

        Long trainerId = trainer.getId();
        Long educationId = educations.get(0).getId();

        // when
        educationService.deleteEducation(trainerId, educationId);

        // then
        Optional<Education> result = educationRepository.findById(educationId);
        assertThat(result).isEmpty();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private Education createEducation(String name, Trainer trainer) {
        return Education.builder()
            .trainer(trainer)
            .name(name)
            .institution("교육기관")
            .acquisitionYearMonth(YearMonth.of(2023, 10))
            .build();
    }
}