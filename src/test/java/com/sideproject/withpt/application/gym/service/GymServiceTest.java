package com.sideproject.withpt.application.gym.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.domain.gym.Gym;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class GymServiceTest {

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymService gymService;

    @DisplayName("기존 체크 - 요청으로 주어진 체육관 스케줄에 따라 체육관 등록")
    @Test
    void registerGymsWhenAlreadyGymExist() {
        // given
        Gym gym = createGymEntity("이미 저장된 체육관");
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
        Gym gym = createGymEntity("이미 저장된 체육관");
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

    private Gym createGymEntity(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .latitude(3.1415)
            .longitude(4.1425)
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