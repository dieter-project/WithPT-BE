package com.sideproject.withpt.application.gymtrainer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.groups.Tuple;
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
class GymTrainerQueryRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;


    @DisplayName("트레이너가 소속된 체육관 목록 조회 With Pageable")
    @Test
    void findAllPageableByTrainer() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        List<GymTrainer> gymTrainers = List.of(
            createGymTrainer(gym1, trainer),
            createGymTrainer(gym2, trainer),
            createGymTrainer(gym3, trainer)
        );
        gymTrainerRepository.saveAll(gymTrainers);

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Slice<GymTrainer> result = gymTrainerRepository.findAllPageableByTrainer(trainer, pageable);

        // then
        log.info("결과 {}", result.getSize());
        log.info("결과 {}", result.getPageable());
        log.info("결과 {}", result.getNumber());
        log.info("결과 {}", result.getContent());
        log.info("결과 {}", result.getSort());
        log.info("결과 {}", result.getNumberOfElements());
        assertThat(result.getContent()).hasSize(2);
    }

    @DisplayName("트레이너가 소속된 특정 하나의 체육관만 조회할 수 있다.")
    @Test
    void findAllTrainerAndOptionalGym() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        List<GymTrainer> gymTrainers = List.of(
            createGymTrainer(gym1, trainer),
            createGymTrainer(gym2, trainer),
            createGymTrainer(gym3, trainer)
        );
        gymTrainerRepository.saveAll(gymTrainers);

        // when
        List<GymTrainer> result = gymTrainerRepository.findAllTrainerAndOptionalGym(trainer, gym1);

        // then
        assertThat(result).hasSize(1);

        GymTrainer gymTrainer = result.get(0);
        assertThat(gymTrainer.getGym().getName()).isEqualTo("체육관1");
        assertThat(gymTrainer.getTrainer().getName()).isEqualTo("test 트레이너");
    }

    @DisplayName("Gym 이 NULL 일 때 트레이너가 소속된 체육관 전체가 조회된다.")
    @Test
    void findAllTrainerAndOptionalGymWhenGymIsNULL() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        List<GymTrainer> gymTrainers = List.of(
            createGymTrainer(gym1, trainer),
            createGymTrainer(gym2, trainer),
            createGymTrainer(gym3, trainer)
        );
        gymTrainerRepository.saveAll(gymTrainers);

        // when
        List<GymTrainer> result = gymTrainerRepository.findAllTrainerAndOptionalGym(trainer, null);

        // then
        assertThat(result).hasSize(3)
            .extracting("gym.name", "trainer.name")
            .contains(
                Tuple.tuple("체육관1", "test 트레이너"),
                Tuple.tuple("체육관2", "test 트레이너"),
                Tuple.tuple("체육관3", "test 트레이너")
            );
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
            .hireDate(LocalDate.of(2024, 9, 27))
            .build();
    }
}