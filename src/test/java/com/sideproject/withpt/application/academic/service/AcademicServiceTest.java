package com.sideproject.withpt.application.academic.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.repository.AcademicRepository;
import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.domain.trainer.Academic;
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
class AcademicServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private AcademicRepository academicRepository;

    @Autowired
    private AcademicService academicService;

    @DisplayName("트레이너 모든 학력 조회")
    @Test
    void getAllAcademics() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        academicRepository.saveAll(List.of(
            createAcademic("학교1", trainer),
            createAcademic("학교2", trainer)
        ));

        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<AcademicResponse> responses = academicService.getAllAcademics(trainerId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2)
            .extracting("name")
            .contains("학교1", "학교2");
    }

    @DisplayName("트레이너 학력 단건 조회")
    @Test
    void getAcademic() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        academicRepository.save(createAcademic("학교1", trainer));
        Academic academic = academicRepository.save(createAcademic("학교2", trainer));

        Long trainerId = trainer.getId();
        Long academicId = academic.getId();

        // when
        AcademicResponse response = academicService.getAcademic(trainerId, academicId);

        // then
        assertThat(response.getName()).isEqualTo("학교2");
    }

    @DisplayName("이미 입력된 학력 리스트에 트레이너 학력을 추가할 수 있다.")
    @Test
    void saveAcademic() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        academicRepository.saveAll(List.of(
            createAcademic("학교1", trainer),
            createAcademic("학교2", trainer)
        ));

        Academic academic = Academic.builder()
            .name("학교3")
            .major("전공")
            .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
            .degree(Degree.BACHELOR)
            .country("한국")
            .enrollmentYearMonth(YearMonth.of(2015, 2))
            .graduationYearMonth(YearMonth.of(2020, 3))
            .build();

        Long trainerId = trainer.getId();
        // when
        AcademicResponse response = academicService.saveAcademic(trainerId, academic);

        // then
        assertThat(response.getName()).isEqualTo("학교3");

        List<Academic> academics = academicRepository.findAll();
        assertThat(academics).hasSize(3);
    }

    @DisplayName("학력 리스트가 비어있을 때 트레이너 학력을 추가할 수 있다.")
    @Test
    void saveAcademicWhenAcademicsEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Academic academic = Academic.builder()
            .name("학교3")
            .major("전공")
            .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
            .degree(Degree.BACHELOR)
            .country("한국")
            .enrollmentYearMonth(YearMonth.of(2015, 2))
            .graduationYearMonth(YearMonth.of(2020, 3))
            .build();

        Long trainerId = trainer.getId();
        // when
        AcademicResponse response = academicService.saveAcademic(trainerId, academic);

        // then
        assertThat(response.getName()).isEqualTo("학교3");

        List<Academic> academics = academicRepository.findAll();
        assertThat(academics).hasSize(1);
    }

    @DisplayName("학력 사항 수정")
    @Test
    void editAcademic() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Academic academic = academicRepository.save(
            Academic.builder()
                .trainer(trainer)
                .name("학교3")
                .major("전공")
                .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
                .degree(Degree.BACHELOR)
                .country("Korea")
                .enrollmentYearMonth(YearMonth.of(2015, 2))
                .graduationYearMonth(YearMonth.of(2020, 3))
                .build()
        );

        AcademicEditRequest request = AcademicEditRequest.builder()
            .id(academic.getId())
            .name("수정된 학교")
            .major("전공")
            .institution(AcademicInstitution.GRADUATE_SCHOOL)
            .degree(Degree.MASTER)
            .country("Korea")
            .enrollmentYearMonth("2015-02")
            .graduationYearMonth("2020-03")
            .build();

        Long trainerId = trainer.getId();

        // when
        AcademicResponse response = academicService.editAcademic(trainerId, request);

        // then
        assertThat(response)
            .extracting("name", "institution", "degree")
            .contains("수정된 학교", AcademicInstitution.GRADUATE_SCHOOL, Degree.MASTER);
    }

    @DisplayName("학력 삭제")
    @Test
    void deleteAcademic() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Academic academic = academicRepository.save(
            Academic.builder()
                .trainer(trainer)
                .name("학교3")
                .major("전공")
                .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
                .degree(Degree.BACHELOR)
                .country("Korea")
                .enrollmentYearMonth(YearMonth.of(2015, 2))
                .graduationYearMonth(YearMonth.of(2020, 3))
                .build()
        );

        Long trainerId = trainer.getId();
        Long academicId = academic.getId();

        // when
        academicService.deleteAcademic(trainerId, academicId);

        // then
        Optional<Academic> result = academicRepository.findById(academicId);
        assertThat(result).isEmpty();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private Academic createAcademic(String name, Trainer trainer) {
        return Academic.builder()
            .trainer(trainer)
            .name(name)
            .major("전공")
            .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
            .degree(Degree.BACHELOR)
            .country("한국")
            .enrollmentYearMonth(YearMonth.of(2015, 2))
            .graduationYearMonth(YearMonth.of(2020, 3))
            .build();
    }
}