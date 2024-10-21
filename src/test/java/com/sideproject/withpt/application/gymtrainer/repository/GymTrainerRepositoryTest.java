package com.sideproject.withpt.application.gymtrainer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
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
class GymTrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @DisplayName("트레이너가 요청된 체육관에 소속되어 있는지 조회한다.")
    @Test
    void findByTrainerAndGym() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

        // when
        Optional<GymTrainer> optionalGymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym);

        // then
        assertThat(optionalGymTrainer).isPresent();

        GymTrainer gymTrainer = optionalGymTrainer.get();
        assertThat(gymTrainer.getGym()).isEqualTo(gym);
        assertThat(gymTrainer.getTrainer()).isEqualTo(trainer);
    }

    @DisplayName("트레이너가 요청된 체육관에 소속되어 있지 않을 수 있다.")
    @Test
    void findByTrainerAndGymIsEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

        Trainer requestTrainer = trainerRepository.save(createTrainer("test"));

        // when
        Optional<GymTrainer> optionalGymTrainer = gymTrainerRepository.findByTrainerAndGym(requestTrainer, gym);

        // then
        assertThat(optionalGymTrainer).isEmpty();
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

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer, LocalDate hireDate) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .hireDate(hireDate)
            .build();
    }
}