package com.sideproject.withpt.application.schedule.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class ScheduleRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @DisplayName("해당 요일에 등록된 트레이너의 근무 날짜 조회")
    @Test
    void findByGymTrainerAndWeekday() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        saveWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer);
        saveWorkSchedule(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer);
        saveWorkSchedule(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer);
        saveWorkSchedule(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer);
        saveWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer);

        // when
        WorkSchedule workSchedule = scheduleRepository.findByGymTrainerAndWeekday(gymTrainer, Day.WED).get();

        // then
        assertThat(workSchedule.getWeekday()).isEqualTo(Day.WED);
        assertThat(workSchedule.getInTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(workSchedule.getOutTime()).isEqualTo(LocalTime.of(18, 0));
    }

    @DisplayName("해당 근무 시간이 존재하지 않습니다")
    @Test
    void findByGymTrainerAndWeekdayWhenNotWork() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        saveWorkSchedule(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer);
        saveWorkSchedule(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer);
        saveWorkSchedule(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0), gymTrainer);
        saveWorkSchedule(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0), gymTrainer);

        // when // then
        Optional<WorkSchedule> optionalWorkSchedule = scheduleRepository.findByGymTrainerAndWeekday(gymTrainer, Day.WED);

        assertThat(optionalWorkSchedule).isEmpty();
    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .build();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .build();
    }

    private WorkSchedule saveWorkSchedule(Day day, LocalTime inTime, LocalTime outTime, GymTrainer gymTrainer) {
        return scheduleRepository.save(WorkSchedule.builder()
            .weekday(day)
            .inTime(inTime)
            .outTime(outTime)
            .gymTrainer(gymTrainer)
            .build()
        );
    }
}