package com.sideproject.withpt.application.pt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.ExerciseFrequency;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class PersonalTrainingServiceTest {

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

    @Autowired
    private GymQueryRepository gymQueryRepository;

    @Autowired
    private PersonalTrainingService personalTrainingService;


    @DisplayName("체육관 목록 및 PT 회원 수 조회 시 등록 허용된 PT 회원들만 조회된다.")
    @Test
    void listOfGymsAndNumberOfMembers() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 27)));
        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));
        Member member3 = memberRepository.save(createMember("회원3"));
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer1, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 27)));
        Member member4 = memberRepository.save(createMember("회원4"));
        Member member5 = memberRepository.save(createMember("회원5"));
        personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer3 = gymTrainerRepository.save(createGymTrainer(gym3, trainer, LocalDate.of(2024, 9, 27)));
        Member member6 = memberRepository.save(createMember("회원6"));
        Member member7 = memberRepository.save(createMember("회원7"));
        personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member7, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));

        // when

        // then
    }

    @DisplayName("특정 체육관에 신규 PT 회원을 등록한다.")
    @Test
    void registerPersonalTraining() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

        final Long gymId = gym.getId();
        final Long memberId = member.getId();
        final Long trainerId = trainer.getId();
        LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

        // when
        PersonalTrainingMemberResponse response = personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate);

        // then
        assertThat(response)
            .extracting("member", "trainer", "gym")
            .contains("회원", "test 트레이너", "체육관1");

        PersonalTraining savedPersonalTraining = personalTrainingRepository.findAll().get(0);

        assertThat(savedPersonalTraining)
            .extracting("totalPtCount", "infoInputStatus", "registrationRequestDate", "registrationStatus", "registrationAllowedStatus")
            .contains(0, 0, ptRegistrationRequestDate, PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING);
    }

    @Nested
    @DisplayName("신규 PT 회원 등록 - 예외 상황")
    class RegisterPersonalTraining {

        @DisplayName("회원이 존재하지 않을 때.")
        @Test
        void WhenMemberNotFound() {
            // given
            Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
            Gym gym = gymRepository.save(createGym("체육관1"));

            gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

            final Long gymId = gym.getId();
            final Long trainerId = trainer.getId();
            LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

            final Long memberId = 1L;

            // when // then
            assertThatThrownBy(() -> personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate))
                .isInstanceOf(GlobalException.class)
                .hasMessage("존재하지 않은 회원입니다.");
        }

        @DisplayName("트레이너가 존재하지 않을 때.")
        @Test
        void WhenTrainerNotFound() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Gym gym = gymRepository.save(createGym("체육관1"));

            gymTrainerRepository.save(createGymTrainer(gym, trainerRepository.save(createTrainer("test 트레이너")), LocalDate.of(2024, 9, 27)));

            final Long gymId = gym.getId();
            final Long memberId = member.getId();
            LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

            final Long trainerId = 11L;

            // when // then
            assertThatThrownBy(() -> personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate))
                .isInstanceOf(GlobalException.class)
                .hasMessage("존재하지 않은 회원입니다.");
        }

        @DisplayName("체육관이 존재하지 않을 때.")
        @Test
        void WhenGymNotFound() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
            gymTrainerRepository.save(createGymTrainer(gymRepository.save(createGym("체육관1")), trainer, LocalDate.of(2024, 9, 27)));

            final Long memberId = member.getId();
            final Long trainerId = trainer.getId();
            LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

            final Long gymId = 11L;

            // when // then
            assertThatThrownBy(() -> personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate))
                .isInstanceOf(GymException.class)
                .hasMessage("존재하지 않는 체육관 입니다.");
        }

        @DisplayName("트레이너가 소속된 체육관이 아닐 때")
        @Test
        void WhenGymTrainerNotFound() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
            Gym gym = gymRepository.save(createGym("체육관1"));

            gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

            Trainer trainer2 = trainerRepository.save(createTrainer("test 트레이너2"));
            final Long gymId = gym.getId();
            final Long memberId = member.getId();
            final Long trainerId = trainer2.getId();
            LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

            // when // then
            assertThatThrownBy(() -> personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate))
                .isInstanceOf(GymTrainerException.class)
                .hasMessage("트레이너가 소속된 체육관이 존재하지 않습니다.");
        }

        @DisplayName("이미 PT 등록된 회원일 때")
        @Test
        void WhenAlreadyPTMember() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
            Gym gym = gymRepository.save(createGym("체육관1"));

            GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));

            LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);
            personalTrainingRepository.save(createPersonalTraining(member, gymTrainer, ptRegistrationRequestDate, PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING));

            final Long gymId = gym.getId();
            final Long memberId = member.getId();
            final Long trainerId = trainer.getId();

            // when // then
            assertThatThrownBy(() -> personalTrainingService.registerPersonalTraining(gymId, memberId, trainerId, ptRegistrationRequestDate))
                .isInstanceOf(PTException.class)
                .hasMessage("이미 등록된 PT 회원입니다.");
        }
    }

    @DisplayName("회원 측에서 PT 등록을 허용한다.")
    @Test
    void approvedPersonalTrainingRegistration() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING));

        final Long personalTrainingId = personalTraining.getId();
        final LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 9, 51);

        // when
        personalTrainingService.approvedPersonalTrainingRegistration(personalTrainingId, registrationAllowedDate);

        // then
        PersonalTraining savedPersonalTraining = personalTrainingRepository.findAll().get(0);
        assertThat(savedPersonalTraining)
            .extracting("registrationAllowedStatus", "registrationStatus", "registrationAllowedDate")
            .contains(PtRegistrationAllowedStatus.ALLOWED, PtRegistrationStatus.ALLOWED, registrationAllowedDate);
    }

    @DisplayName("이미 PT 등록이 승인 됐으면 에러를 응답한다.")
    @Test
    void approvedPersonalTrainingRegistrationThrow_AlREADY_ALLOWED_PT_REGISTRATION() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED));

        final Long personalTrainingId = personalTraining.getId();
        final LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 9, 51);

        // when // then
        assertThatThrownBy(() -> personalTrainingService.approvedPersonalTrainingRegistration(personalTrainingId, registrationAllowedDate))
            .isInstanceOf(PTException.class)
            .hasMessage("이미 PT 등록을 허용한 상태입니다.");
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus ptRegistrationStatus, PtRegistrationAllowedStatus ptRegistrationAllowedStatus) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(registrationRequestDate)
            .infoInputStatus(infoInputStatus)
            .registrationStatus(ptRegistrationStatus)
            .registrationAllowedStatus(ptRegistrationAllowedStatus)
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