package com.sideproject.withpt.application.gym.repositoy;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class GymQueryRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @DisplayName("트레이너가 소속된 체육관 목록들을 가져온다.")
    @Test
    void findAllTrainerGymsByPageable_firstPage() {
        // given
        Trainer trainer = createTrainer();
        Trainer savedTrainer = trainerRepository.save(trainer);

        int gymCount = 7;
        List<Gym> gyms = new ArrayList<>();
        for (int i = 0; i < gymCount; i++) {
            gyms.add(createGym("체육관" + (i + 1)));
        }
        gymRepository.saveAll(gyms);

        List<GymTrainer> gymTrainers = new ArrayList<>();
        for (int i = 0; i < gymCount; i++) {
            gymTrainers.add(createGymTrainer(gyms.get(i), trainer, LocalDate.of(2024, 9, 25)));
        }
        gymTrainerRepository.saveAll(gymTrainers);

        // when
        PageRequest pageRequest = PageRequest.of(0, 5);
        Slice<Gym> gymSlice = gymRepository.findAllTrainerGymsByPageable(savedTrainer, pageRequest);

        // then
        assertThat(gymSlice.getContent()).hasSize(5)
            .extracting("name")
            .contains("체육관1", "체육관2", "체육관3", "체육관4", "체육관5");
        assertThat(gymSlice.getNumberOfElements()).isEqualTo(5);

        Pageable pageable = gymSlice.getPageable();
        assertThat(pageable.getOffset()).isEqualTo(0);
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
    }

    @DisplayName("트레이너가 소속된 체육관 목록들을 가져온다.")
    @Test
    void findAllTrainerGymsByPageable_secondPage() {
        // given
        Trainer trainer = createTrainer();
        Trainer savedTrainer = trainerRepository.save(trainer);

        int gymCount = 7;
        List<Gym> gyms = new ArrayList<>();
        for (int i = 0; i < gymCount; i++) {
            gyms.add(createGym("체육관" + (i + 1)));
        }
        gymRepository.saveAll(gyms);

        List<GymTrainer> gymTrainers = new ArrayList<>();
        for (int i = 0; i < gymCount; i++) {
            gymTrainers.add(createGymTrainer(gyms.get(i), trainer, LocalDate.of(2024, 9, 25)));
        }
        gymTrainerRepository.saveAll(gymTrainers);

        // when
        PageRequest pageRequest = PageRequest.of(1, 5);
        Slice<Gym> gymSlice = gymRepository.findAllTrainerGymsByPageable(savedTrainer, pageRequest);

        // then
        assertThat(gymSlice.getContent()).hasSize(2)
            .extracting("name")
            .contains("체육관6", "체육관7");
        assertThat(gymSlice.getNumberOfElements()).isEqualTo(2);

        Pageable pageable = gymSlice.getPageable();
        assertThat(pageable.getOffset()).isEqualTo(5);
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .build();
    }

    private Trainer createTrainer() {
        return Trainer.signUpBuilder()
            .email("test@test.com")
            .name("test")
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