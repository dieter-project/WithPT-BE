package com.sideproject.withpt.application.pt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class PersonalTrainingRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private PersonalTrainingRepository personalTrainingRepository;

    @DisplayName("이미 PT 가 등록되어 있으면 True 반환")
    @Test
    void existsByMemberAndGymTrainerReturnTrue() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

        LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);
        personalTrainingRepository.save(registerNewPersonalTraining(member, gymTrainer, ptRegistrationRequestDate));

        // when
        boolean result = personalTrainingRepository.existsByMemberAndGymTrainer(member, gymTrainer);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("이미 PT 가 등록되어 있으면 True 반환")
    @Test
    void existsByMemberAndGymTrainerReturnFalse() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

        // when
        boolean result = personalTrainingRepository.existsByMemberAndGymTrainer(member, gymTrainer);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("등록된 PT 삭제하기")
    @Test
    void deleteAllByIdInBatch() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));
        Member member3 = memberRepository.save(createMember("회원3"));
        Member member4 = memberRepository.save(createMember("회원4"));
        Member member5 = memberRepository.save(createMember("회원5"));
        Member member6 = memberRepository.save(createMember("회원6"));
        PersonalTraining personalTraining1 = personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer, PtRegistrationAllowedStatus.WAITING));
        PersonalTraining personalTraining2 = personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer, PtRegistrationAllowedStatus.WAITING));
        PersonalTraining personalTraining3 = personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer, PtRegistrationAllowedStatus.WAITING));
        PersonalTraining personalTraining4 = personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer, PtRegistrationAllowedStatus.WAITING));
        PersonalTraining personalTraining5 = personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer, PtRegistrationAllowedStatus.WAITING));
        PersonalTraining personalTraining6 = personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer, PtRegistrationAllowedStatus.WAITING));

        List<Long> ptIds = List.of(personalTraining1.getId(), personalTraining2.getId(), personalTraining3.getId(), personalTraining4.getId(), personalTraining5.getId());

        // when
        personalTrainingRepository.deleteAllByIdInBatch(ptIds);

        // then
        List<PersonalTraining> result = personalTrainingRepository.findAll();
        assertThat(result).hasSize(1);
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationAllowedStatus(registrationAllowedStatus)
            .build();
    }

    public PersonalTraining registerNewPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(registrationRequestDate)
            .infoInputStatus(PTInfoInputStatus.INFO_EMPTY)
            .registrationStatus(PtRegistrationStatus.ALLOWED_BEFORE)
            .registrationAllowedStatus(PtRegistrationAllowedStatus.WAITING)
            .build();
    }

    private Member createMember(String name) {
        Authentication authentication = Authentication.builder()
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .build();

        return Member.builder()
            .name(name)
            .authentication(authentication)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
            .build();
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