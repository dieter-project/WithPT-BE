package com.sideproject.withpt.application.schedule.repository;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    // TODO : GymTrainer 로 관리하므로 Trainer 랑 Gym 은 삭제하기
    List<WorkSchedule> findAllByTrainerAndGym(Trainer trainer, Gym gym);
    Optional<WorkSchedule> findByGymTrainerAndWeekday(GymTrainer gymTrainer, Day weekDay);
}
