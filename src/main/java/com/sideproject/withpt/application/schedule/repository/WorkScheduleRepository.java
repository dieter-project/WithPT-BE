package com.sideproject.withpt.application.schedule.repository;

import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.gym.WorkSchedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    List<WorkSchedule> findAllByGymTrainer(GymTrainer gymTrainer);

    Optional<WorkSchedule> findByGymTrainerAndWeekday(GymTrainer gymTrainer, Day weekDay);
}
