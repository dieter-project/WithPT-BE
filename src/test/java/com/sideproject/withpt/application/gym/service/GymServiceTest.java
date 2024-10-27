package com.sideproject.withpt.application.gym.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gym.service.response.GymResponse;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.model.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.model.single.WorkScheduleDto;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.util.List;
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
class GymServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private GymService gymService;

    @DisplayName("체육관 리스트 조회")
    @Test
    void listOfAllGymsByPageable() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer());
        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));

        gymTrainerRepository.saveAll(
            List.of(
                createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 25)),
                createGymTrainer(gym2, trainer, LocalDate.of(2024, 7, 2))
            )
        );

        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<GymResponse> gymResponses = gymService.listOfAllGymsByPageable(trainerId, pageable);

        // then
        assertThat(gymResponses.getContent()).hasSize(2)
            .extracting("name")
            .contains("체육관1", "체육관2");
    }

    @DisplayName("기존 체크 - 요청으로 주어진 체육관 스케줄에 따라 체육관 등록")
    @Test
    void registerGymsWhenAlreadyGymExist() {
        // given
        Gym gym = createGym("이미 저장된 체육관");
        gymRepository.save(gym);

        GymScheduleDto gymSchedule = createTrainerGymScheduleDto("이미 저장된 체육관", List.of());

        // when
        List<Gym> gyms = gymService.registerGyms(List.of(gymSchedule));

        // then
        assertThat(gyms).hasSize(1)
            .extracting("name")
            .contains("이미 저장된 체육관");
    }

    @DisplayName("신규 저장 - 요청으로 주어진 체육관 스케줄에 따라 체육관 등록")
    @Test
    void registerGymsWhenNewSave() {
        // given
        Gym gym = createGym("이미 저장된 체육관");
        gymRepository.save(gym);

        GymScheduleDto gymSchedule1 = createTrainerGymScheduleDto("아자아자 피트니스 센터", List.of());
        GymScheduleDto gymSchedule2 = createTrainerGymScheduleDto("이미 저장된 체육관", List.of());

        // when
        List<Gym> gyms = gymService.registerGyms(List.of(gymSchedule1, gymSchedule2));

        // then
        assertThat(gyms).hasSize(2)
            .extracting("name")
            .contains("이미 저장된 체육관", "아자아자 피트니스 센터");
    }

    private Trainer createTrainer() {
        return Trainer.signUpBuilder()
            .email("test@test.com")
            .name("test")
            .build();
    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .latitude(3.1415)
            .longitude(4.1425)
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer, LocalDate hireDate) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .hireDate(hireDate)
            .build();
    }

    private GymScheduleDto createTrainerGymScheduleDto(String name, List<WorkScheduleDto> workSchedules) {
        return GymScheduleDto.builder()
            .name(name)
            .address("경기도 김포시 풍무동 231-413")
            .latitude(3.143151)
            .longitude(4.151661)
            .workSchedules(workSchedules)
            .build();
    }

}