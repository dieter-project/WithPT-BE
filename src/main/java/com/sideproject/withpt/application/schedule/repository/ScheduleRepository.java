package com.sideproject.withpt.application.schedule.repository;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    List<WorkSchedule> findAllByTrainerAndGym(Trainer trainer, Gym gym);
}
