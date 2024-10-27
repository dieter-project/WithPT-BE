package com.sideproject.withpt.application.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.controller.request.WorkScheduleEditRequest;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.WorkScheduleRepository;
import com.sideproject.withpt.application.schedule.service.response.WorkScheduleResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.model.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.model.single.WorkScheduleDto;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import com.sideproject.withpt.domain.gym.WorkSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
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
                tuple(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                tuple(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                tuple(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                tuple(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                tuple(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0))
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

    @DisplayName("트레이너 근무 스케줄 수정")
    @Test
    void editWorkSchedule() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Gym gym = gymRepository.save(createGym("체육관"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        WorkSchedule workSchedule1 = workScheduleRepository.save(createWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer));
        WorkSchedule workSchedule2 = workScheduleRepository.save(createWorkSchedule(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer));
        WorkSchedule workSchedule3 = workScheduleRepository.save(createWorkSchedule(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer));
        WorkSchedule workSchedule4 = workScheduleRepository.save(createWorkSchedule(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer));
        WorkSchedule workSchedule5 = workScheduleRepository.save(createWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer));

        WorkScheduleEditRequest request = new WorkScheduleEditRequest(
            List.of(
                createEditWorkSchedule(workSchedule1.getId(), Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)), // 원본 그대로
                createEditWorkSchedule(workSchedule2.getId(), Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)), // 원본 그대로
                createEditWorkSchedule(workSchedule3.getId(), Day.WED, LocalTime.of(10, 0), LocalTime.of(22, 0)), // 기존 스케줄 시간 수정
                createEditWorkSchedule(null, Day.THU, LocalTime.of(8, 0), LocalTime.of(17, 0)), // 화면에서 지웠다가 새로 추가
                createEditWorkSchedule(null, Day.FRI, LocalTime.of(8, 0), LocalTime.of(17, 0)), // 화면에서 지웠다가 새로 추가
                createEditWorkSchedule(null, Day.SAT, LocalTime.of(12, 0), LocalTime.of(18, 0)) // 화면에서 지웠다가 새로 추가
            ));

        Long trainerId = trainer.getId();
        Long gymId = gym.getId();

        // when
        workScheduleService.editWorkSchedule(trainerId, gymId, request);

        // then
        List<WorkSchedule> result = workScheduleRepository.findAll();
        assertThat(result).hasSize(6)
            .extracting("weekday", "inTime", "outTime")
            .contains(
                tuple(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0)),
                tuple(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0)),
                tuple(Day.WED, LocalTime.of(10, 0), LocalTime.of(22, 0)),
                tuple(Day.THU, LocalTime.of(8, 0), LocalTime.of(17, 0)),
                tuple(Day.FRI, LocalTime.of(8, 0), LocalTime.of(17, 0)),
                tuple(Day.SAT, LocalTime.of(12, 0), LocalTime.of(18, 0))
            );
    }

    @DisplayName("트레이너 근무 스케줄 수정 요청에서 같은 요일이 2건 이상이면 에러")
    @Test
    void validateUniqueWeekdays() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Gym gym = gymRepository.save(createGym("체육관"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        WorkScheduleEditRequest request = new WorkScheduleEditRequest(
            List.of(
                createEditWorkSchedule(null, Day.THU, LocalTime.of(8, 0), LocalTime.of(17, 0)), // 화면에서 지웠다가 새로 추가
                createEditWorkSchedule(null, Day.THU, LocalTime.of(8, 0), LocalTime.of(17, 0)), // 화면에서 지웠다가 새로 추가
                createEditWorkSchedule(null, Day.SAT, LocalTime.of(12, 0), LocalTime.of(18, 0)) // 화면에서 지웠다가 새로 추가
            ));

        Long trainerId = trainer.getId();
        Long gymId = gym.getId();

        // when // then
        assertThatThrownBy(() -> workScheduleService.editWorkSchedule(trainerId, gymId, request))
            .isInstanceOf(ScheduleException.class)
            .hasMessage("동일한 요일이 존재하여 수정 및 저장이 불가능합니다.");
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

    private WorkScheduleEditRequest.Schedule createEditWorkSchedule(Long id, Day weekday, LocalTime inTime, LocalTime outTime) {
        return WorkScheduleEditRequest.Schedule.builder()
            .id(id)
            .weekday(weekday)
            .inTime(inTime)
            .outTime(outTime)
            .build();
    }
}