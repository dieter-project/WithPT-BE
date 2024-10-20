package com.sideproject.withpt.application.award.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.award.controller.reponse.AwardResponse;
import com.sideproject.withpt.application.award.controller.request.AwardEditRequest;
import com.sideproject.withpt.application.award.exception.AwardException;
import com.sideproject.withpt.application.award.repository.AwardRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.trainer.Award;
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
class AwardServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private AwardService awardService;

    @DisplayName("트레이너 모든 수상내역 조회")
    @Test
    void getAllAwards() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        awardRepository.saveAll(List.of(
            createAward("수상1", trainer),
            createAward("수상2", trainer)
        ));

        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<AwardResponse> responses = awardService.getAllAwards(trainerId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2)
            .extracting("name")
            .contains("수상1", "수상2");
    }

    @DisplayName("트레이너 수상내역 단건 조회")
    @Test
    void getAward() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        awardRepository.save(createAward("수상1", trainer));
        Award award = awardRepository.save(createAward("수상2", trainer));

        Long trainerId = trainer.getId();
        Long awardId = award.getId();

        // when
        AwardResponse response = awardService.getAward(trainerId, awardId);

        // then
        assertThat(response.getName()).isEqualTo("수상2");
    }

    @DisplayName("이미 입력된 수상 리스트에 트레이너 수상이력을 추가할 수 있다.")
    @Test
    void saveAward() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        awardRepository.saveAll(List.of(
            createAward("수상1", trainer),
            createAward("수상2", trainer)
        ));

        Award award = Award.builder()
            .name("수상3")
            .institution("수상 기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 8))
            .build();

        Long trainerId = trainer.getId();

        // when
        AwardResponse response = awardService.saveAward(trainerId, award);

        // then
        assertThat(response.getName()).isEqualTo("수상3");

        List<Award> awards = awardRepository.findAll();
        assertThat(awards).hasSize(3);
    }

    @DisplayName("수상 리스트가 비어있을 때 트레이너 수상이력을 추가할 수 있다.")
    @Test
    void saveAwardWhenAwardsEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Award award = Award.builder()
            .name("수상3")
            .institution("수상 기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 8))
            .build();

        Long trainerId = trainer.getId();

        // when
        AwardResponse response = awardService.saveAward(trainerId, award);

        // then
        assertThat(response.getName()).isEqualTo("수상3");

        List<Award> awards = awardRepository.findAll();
        assertThat(awards).hasSize(1);
    }

    @DisplayName("이미 동일한 수상이력이 있으면 저장할 수 없다.")
    @Test
    void validateDuplicationAllColumn() {
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        awardRepository.save(
            Award.builder()
                .trainer(trainer)
                .name("수상3")
                .institution("수상 기관명")
                .acquisitionYearMonth(YearMonth.of(2023, 8))
                .build()
        );

        Award award = Award.builder()
            .name("수상3")
            .institution("수상 기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 8))
            .build();

        Long trainerId = trainer.getId();

        // when // then
        assertThatThrownBy(() -> awardService.saveAward(trainerId, award))
            .isInstanceOf(AwardException.class)
            .hasMessage("이미 동일한 수상 내역이 존재합니다.");
    }

    @DisplayName("수상이력 수정")
    @Test
    void editAward() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Award award = awardRepository.save(
            Award.builder()
                .trainer(trainer)
                .name("수상3")
                .institution("수상 기관명")
                .acquisitionYearMonth(YearMonth.of(2023, 8))
                .build()
        );

        AwardEditRequest request = AwardEditRequest.builder()
            .id(award.getId())
            .name("수정된 수상")
            .institution("수상 기관명")
            .acquisitionYearMonth("2023-10")
            .build();

        Long trainerId = trainer.getId();

        // when
        AwardResponse response = awardService.editAward(trainerId, request);

        // then
        assertThat(response)
            .extracting("name", "acquisitionYearMonth")
            .contains("수정된 수상", YearMonth.of(2023, 10));
    }

    @DisplayName("수상내역 삭제")
    @Test
    void deleteAward() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Award award = awardRepository.save(
            Award.builder()
                .trainer(trainer)
                .name("수상3")
                .institution("수상 기관명")
                .acquisitionYearMonth(YearMonth.of(2023, 8))
                .build()
        );

        Long trainerId = trainer.getId();
        Long awardId = award.getId();

        // when
        awardService.deleteAward(trainerId, awardId);

        // then
        Optional<Award> result = awardRepository.findById(awardId);
        assertThat(result).isEmpty();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private Award createAward(String name, Trainer trainer) {
        return Award.builder()
            .trainer(trainer)
            .name(name)
            .institution("수상 기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 8))
            .build();
    }
}