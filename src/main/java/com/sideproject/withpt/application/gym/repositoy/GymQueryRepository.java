package com.sideproject.withpt.application.gym.repositoy;

import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GymQueryRepository {

    Slice<Gym> findAllTrainerGymsByPageable(Trainer trainer, Pageable pageable);
}
