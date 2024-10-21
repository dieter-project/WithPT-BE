package com.sideproject.withpt.application.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.repository.WorkScheduleRepository;
import com.sideproject.withpt.application.schedule.service.response.WorkScheduleResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class WorkScheduleServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private WorkScheduleService workScheduleService;

    @DisplayName("트레이너 특정 체육관 근무 스케줄 조회")
    @Test
    void getAllWorkSchedule() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
        workScheduleRepository.saveAll(List.of(
            createWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer1),
            createWorkSchedule(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer1),
            createWorkSchedule(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer1),
            createWorkSchedule(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer1),
            createWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer1)
        ));

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
        workScheduleRepository.saveAll(List.of(
            createWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer2),
            createWorkSchedule(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer2),
            createWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer2)
        ));

        Long gymId = gym1.getId();
        Long trainerId = trainer.getId();

        // when
        List<WorkScheduleResponse> responses = workScheduleService.getAllWorkScheduleByGym(gymId, trainerId);

        // then
        assertThat(responses).hasSize(5)
            .extracting("weekday", "inTime", "outTime")
            .contains(
                Tuple.tuple(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                Tuple.tuple(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                Tuple.tuple(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                Tuple.tuple(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                Tuple.tuple(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0))
            );
    }

    @DisplayName("트레이너 근무 스케줄 단건 조회")
    @Test
    void getWorkSchedule() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Gym gym = gymRepository.save(createGym("체육관"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        List<WorkSchedule> workSchedules = workScheduleRepository.saveAll(List.of(
            createWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer),
            createWorkSchedule(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer),
            createWorkSchedule(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer),
            createWorkSchedule(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer),
            createWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer)
        ));

        Long scheduleId = workSchedules.get(2).getId();

        // when
        WorkScheduleResponse response = workScheduleService.getWorkSchedule(scheduleId);

        // then
        assertThat(response)
            .extracting("weekday", "inTime", "outTime")
            .contains(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0));
    }

    @DisplayName("요청 정보를 통해 트레이너의 체육관 별 스케줄을 등록할 수 있다.")
    @Test
    void registerWorkSchedules() {
        // given
        Trainer trainer = createTrainer("test");
        trainerRepository.save(trainer);

        String gymName1 = "체육관1";
        Gym gym1 = createGym(gymName1);
        gymRepository.save(gym1);
        String gymName2 = "체육관2";
        Gym gym2 = createGym(gymName2);
        gymRepository.save(gym2);

        GymTrainer gymTrainer1 = createGymTrainer(gym1, trainer);
        GymTrainer gymTrainer2 = createGymTrainer(gym2, trainer);

        List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);
        gymTrainerRepository.saveAll(gymTrainers);

        List<WorkScheduleDto> workSchedules1 = List.of(
            createWorkScheduleDto(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)),
            createWorkScheduleDto(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)),
            createWorkScheduleDto(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0)),
            createWorkScheduleDto(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0)),
            createWorkScheduleDto(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0))
        );

        GymScheduleDto gymSchedule1 = createTrainerGymDto(gymName1, workSchedules1);

        List<WorkScheduleDto> workSchedules2 = List.of(
            createWorkScheduleDto(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)),
            createWorkScheduleDto(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0)),
            createWorkScheduleDto(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0)));

        GymScheduleDto gymSchedule2 = createTrainerGymDto(gymName2, workSchedules2);

        List<GymScheduleDto> gymScheduleDtos = List.of(gymSchedule1, gymSchedule2);

        // when
        workScheduleService.registerWorkSchedules(gymScheduleDtos, gymTrainers);

        // then
        List<WorkSchedule> result = workScheduleRepository.findAll();
        assertThat(result).hasSize(8);

    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .build();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email("test@test.com")
            .name(name)
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .hireDate(LocalDate.of(2024, 9, 18))
            .build();
    }

    private WorkSchedule createWorkSchedule(Day weekday, LocalTime inTime, LocalTime outTime, GymTrainer gymTrainer) {
        return WorkSchedule.builder()
            .gymTrainer(gymTrainer)
            .weekday(weekday)
            .inTime(inTime)
            .outTime(outTime)
            .build();
    }

    private WorkScheduleDto createWorkScheduleDto(Day day, LocalTime inTime, LocalTime outTime) {
        return WorkScheduleDto.builder()
            .day(day)
            .inTime(inTime)
            .outTime(outTime)
            .build();
    }

    private GymScheduleDto createTrainerGymDto(String name, List<WorkScheduleDto> workSchedules) {
        return GymScheduleDto.builder()
            .name(name)
            .address("경기도 김포시 풍무동 231-413")
            .latitude(3.143151)
            .longitude(4.151661)
            .workSchedules(workSchedules)
            .build();
    }
}