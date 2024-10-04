package com.sideproject.withpt.application.pt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.MonthlyStatisticsResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingInfoRepository;
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
import com.sideproject.withpt.domain.pt.PersonalTrainingInfo;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private PersonalTrainingInfoRepository personalTrainingInfoRepository;

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

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));
        Member member3 = memberRepository.save(createMember("회원3"));
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, LocalDateTime.of(2024, 9, 22, 12, 45, 1, 1000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, LocalDateTime.of(2024, 9, 23, 12, 45, 1, 1000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer1, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 4000), PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
        Member member4 = memberRepository.save(createMember("회원4"));
        Member member5 = memberRepository.save(createMember("회원5"));
        personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, LocalDateTime.of(2024, 9, 25, 12, 45, 1, 1000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, LocalDateTime.of(2024, 9, 25, 12, 45, 1, 1000), PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer3 = gymTrainerRepository.save(createGymTrainer(gym3, trainer));
        Member member6 = memberRepository.save(createMember("회원6"));
        Member member7 = memberRepository.save(createMember("회원7"));
        personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 1000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member7, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 1000), PtRegistrationAllowedStatus.WAITING));

        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1, 3000);
        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        CountOfMembersAndGymsResponse response = personalTrainingService.listOfGymsAndNumberOfMembers(trainerId, registrationAllowedDate, pageable);

        // then
        assertThat(response.getTotalMemberCount()).isEqualTo(5);
        assertThat(response.getDate()).isEqualTo(registrationAllowedDate.toLocalDate());
        assertThat(response.getGyms().getContent()).hasSize(3)
            .extracting("name", "memberCount")
            .containsExactlyInAnyOrder(
                tuple("체육관1", 2L),
                tuple("체육관2", 2L),
                tuple("체육관3", 1L)
            );
    }

    @DisplayName("체육관 목록 및 PT 회원 수 조회 시 등록된 회원들이 없을 수 있다.")
    @Test
    void listOfGymsAndNumberOfMembersWhenPTMemberEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
        Member member = memberRepository.save(createMember("회원1"));
        personalTrainingRepository.save(createPersonalTraining(member, gymTrainer1, LocalDateTime.of(2024, 9, 22, 12, 45, 1, 1000), PtRegistrationAllowedStatus.WAITING));
        gymTrainerRepository.save(createGymTrainer(gym2, trainer));
        gymTrainerRepository.save(createGymTrainer(gym3, trainer));

        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1, 3000);
        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        CountOfMembersAndGymsResponse response = personalTrainingService.listOfGymsAndNumberOfMembers(trainerId, registrationAllowedDate, pageable);

        // then
        assertThat(response.getTotalMemberCount()).isEqualTo(0);
        assertThat(response.getDate()).isEqualTo(registrationAllowedDate.toLocalDate());
        assertThat(response.getGyms().getContent()).hasSize(3)
            .extracting("name", "memberCount")
            .containsExactlyInAnyOrder(
                tuple("체육관1", 0L),
                tuple("체육관2", 0L),
                tuple("체육관3", 0L)
            );
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

    @DisplayName("잔여 PT 횟수가 모두 0일때 등록된 PT 삭제하기 - ALLOWED")
    @Test
    void deletePersonalTrainingMembers() {
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
        PersonalTraining savePersonalTraining1 = personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer, 30, 0));
        PersonalTraining savePersonalTraining2 = personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer, 25, 0));
        PersonalTraining savePersonalTraining3 = personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer, 25, 0));
        PersonalTraining savePersonalTraining4 = personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer, 25, 0));
        PersonalTraining savePersonalTraining5 = personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer, 10, 0));
        PersonalTraining savePersonalTraining6 = personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer, 10, 0));

        List<Long> ptIds = List.of(savePersonalTraining1.getId(), savePersonalTraining2.getId(), savePersonalTraining3.getId(), savePersonalTraining4.getId(), savePersonalTraining5.getId(), savePersonalTraining6.getId());

        // when
        personalTrainingService.deletePersonalTrainingMembers(ptIds, PtRegistrationAllowedStatus.ALLOWED);

        // then
        List<PersonalTraining> personalTrainings = personalTrainingRepository.findAll();
        assertThat(personalTrainings.size()).isZero();
    }

    @DisplayName("등록된 PT 삭제하기 - ALLOWED")
    @Test
    void deletePersonalTrainingMembersWhenPtRegistrationAllowedStatusIsALLOWED() {
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
        PersonalTraining savePersonalTraining1 = personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer, 30, 0));
        PersonalTraining savePersonalTraining2 = personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer, 25, 0));
        PersonalTraining savePersonalTraining3 = personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer, 25, 0));
        PersonalTraining savePersonalTraining4 = personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer, 25, 10));
        PersonalTraining savePersonalTraining5 = personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer, 10, 0));
        PersonalTraining savePersonalTraining6 = personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer, 10, 0));

        List<Long> ptIds = List.of(savePersonalTraining1.getId(), savePersonalTraining2.getId(), savePersonalTraining3.getId(), savePersonalTraining4.getId(), savePersonalTraining5.getId(), savePersonalTraining6.getId());

        // when // then
        assertThatThrownBy(() -> personalTrainingService.deletePersonalTrainingMembers(ptIds, PtRegistrationAllowedStatus.ALLOWED))
            .isInstanceOf(GlobalException.class)
            .hasMessage(savePersonalTraining4.getId() + "번 PT는 잔여 PT 횟수가 남아 있습니다. 정말 해제하시겠습니까?");
    }

    @DisplayName("신규 PT 회원 세부 정보 입력")
    @Test
    void savePtMemberDetailInfo() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member1 = memberRepository.save(createMember("회원1"));
        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member1, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1),
                PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED, PtRegistrationAllowedStatus.ALLOWED)
        );

        final Long ptId = savedPersonalTraining.getId();
        final SavePtMemberDetailInfoRequest request = SavePtMemberDetailInfoRequest.builder()
            .ptCount(30)
            .centerFirstRegistrationMonth("2023-11")
            .note("다이어트를 원하심. 척추 측만증이 있음.")
            .build();

        // when
        personalTrainingService.savePtMemberDetailInfo(ptId, request);

        // then
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId).get();
        assertThat(personalTraining)
            .extracting("totalPtCount", "remainingPtCount", "note", "registrationStatus", "infoInputStatus")
            .contains(30, 30, "다이어트를 원하심. 척추 측만증이 있음.", PtRegistrationStatus.NEW_REGISTRATION, PTInfoInputStatus.INFO_REGISTERED);
        assertThat(personalTraining.getCenterFirstRegistrationMonth().toLocalDate()).isEqualTo(LocalDate.of(2023, 11, 1));

        List<PersonalTrainingInfo> personalTrainingInfoList = personalTrainingInfoRepository.findAll();
        assertThat(personalTrainingInfoList).hasSize(1)
            .extracting("ptCount", "registrationStatus")
            .containsOnly(
                tuple(30, PtRegistrationStatus.NEW_REGISTRATION)
            );

        PersonalTrainingInfo personalTrainingInfo = personalTrainingInfoList.get(0);
        assertThat(personalTrainingInfo.getPersonalTraining()).isNotNull();
    }

    @DisplayName("등록 허용되지 않은 회원이면 신규 PT 회원 세부 정보 입력이 불가능하다.")
    @Test
    void savePtMemberDetailInfoWhen_PT_REGISTRATION_NOT_ALLOWED() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member1 = memberRepository.save(createMember("회원1"));
        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member1, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1),
                PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.WAITING)
        );

        final Long ptId = savedPersonalTraining.getId();
        final SavePtMemberDetailInfoRequest request = SavePtMemberDetailInfoRequest.builder()
            .ptCount(30)
            .centerFirstRegistrationMonth("2023-11")
            .note("다이어트를 원하심. 척추 측만증이 있음.")
            .build();

        // when // then
        assertThatThrownBy(() -> personalTrainingService.savePtMemberDetailInfo(ptId, request))
            .isInstanceOf(PTException.class)
            .hasMessage("아직 PT 등록을 허용하지 않은 회원입니다.");
    }

    @DisplayName("이미 초기 정보가 입력된 회원이면 신규 PT 회원 세부 정보 입력이 불가능하다.")
    @Test
    void savePtMemberDetailInfoWhen_AlREADY_REGISTERED_FIRST_PT_INFO() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member1 = memberRepository.save(createMember("회원1"));
        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member1, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1),
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED)
        );

        final Long ptId = savedPersonalTraining.getId();
        final SavePtMemberDetailInfoRequest request = SavePtMemberDetailInfoRequest.builder()
            .ptCount(30)
            .centerFirstRegistrationMonth("2023-11")
            .note("다이어트를 원하심. 척추 측만증이 있음.")
            .build();

        // when // then
        assertThatThrownBy(() -> personalTrainingService.savePtMemberDetailInfo(ptId, request))
            .isInstanceOf(PTException.class)
            .hasMessage("이미 초기 PT 정보가 등록되어 있습니다.");
    }

    @DisplayName("재등록일이 센터 등록일 보다 이전이면 PT 연장을 할수 없다.")
    @Test
    void extendPtCountWhenReRegistrationCannotBeBeforeFirstRegistration() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member = memberRepository.save(createMember("회원1"));
        LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

        LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);

        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, "노트", 30, 30, registrationRequestDate,
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED,
                centerFirstRegistrationMonth, null, registrationAllowedDate)
        );

        final Long ptId = savedPersonalTraining.getId();
        final ExtendPtRequest request = ExtendPtRequest.builder()
            .ptCount(20)
            .reRegistrationDate("2024-09")
            .build();

        // when // then
        assertThatThrownBy(() -> personalTrainingService.extendPtCount(ptId, request))
            .isInstanceOf(PTException.class)
            .hasMessage("재 등록일을 잘못 입력하셨습니다")
        ;
    }

    @DisplayName("PT 횟수 연장하기")
    @Test
    void extendPtCount() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member = memberRepository.save(createMember("회원1"));
        LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

        LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);

        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, "노트", 30, 12, registrationRequestDate,
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED,
                centerFirstRegistrationMonth, null, registrationAllowedDate)
        );

        personalTrainingInfoRepository.save(PersonalTrainingInfo.createPTInfo(30, centerFirstRegistrationMonth, PtRegistrationStatus.NEW_REGISTRATION, savedPersonalTraining));

        final Long ptId = savedPersonalTraining.getId();
        final ExtendPtRequest request = ExtendPtRequest.builder()
            .ptCount(20)
            .reRegistrationDate("2024-12")
            .build();

        // when
        personalTrainingService.extendPtCount(ptId, request);

        // then
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId).get();
        assertThat(personalTraining)
            .extracting("totalPtCount", "remainingPtCount", "centerLastReRegistrationMonth", "registrationStatus")
            .contains(50, 32, LocalDateTime.of(2024, 12, 1, 0, 0, 0), PtRegistrationStatus.RE_REGISTRATION);

        List<PersonalTrainingInfo> personalTrainingInfos = personalTrainingInfoRepository.findAll();
        assertThat(personalTrainingInfos).hasSize(2)
            .extracting("ptCount", "registrationDate", "registrationStatus")
            .containsExactly(
                tuple(30, centerFirstRegistrationMonth, PtRegistrationStatus.NEW_REGISTRATION),
                tuple(20, LocalDateTime.of(2024, 12, 1, 0, 0, 0), PtRegistrationStatus.RE_REGISTRATION)
            );
    }

    @DisplayName("PT 회원 세부 정보 수정")
    @Test
    void updatePtMemberDetailInfo() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member = memberRepository.save(createMember("회원"));
        LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

        LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, "노트", 50, 32, registrationRequestDate,
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED,
                centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate)
        );

        final Long ptId = savedPersonalTraining.getId();
        final UpdatePtMemberDetailInfoRequest request = UpdatePtMemberDetailInfoRequest.builder()
            .remainingPtCount(35)
            .totalPtCount(50)
            .note("PT 정보 수정")
            .build();

        // when
        personalTrainingService.updatePtMemberDetailInfo(ptId, request);

        // then
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId).get();
        assertThat(personalTraining)
            .extracting("totalPtCount", "remainingPtCount", "note")
            .contains(50, 35, "PT 정보 수정");
    }

    @DisplayName("PT 회원 세부 정보 수정 -PT 잔여 횟수는 전체 횟수보다 많을 수 없습니다.")
    @Test
    void updatePtMemberDetailInfo_REMAINING_PT_CANNOT_EXCEED_THE_TOTAL_PT_NUMBER() {
        // given
        Gym gym = gymRepository.save(createGym("체육관"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 30)));

        Member member = memberRepository.save(createMember("회원"));
        LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

        LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

        PersonalTraining savedPersonalTraining = personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, "노트", 50, 32, registrationRequestDate,
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED,
                centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate)
        );

        final Long ptId = savedPersonalTraining.getId();
        final UpdatePtMemberDetailInfoRequest request = UpdatePtMemberDetailInfoRequest.builder()
            .remainingPtCount(51)
            .totalPtCount(50)
            .note("PT 정보 수정")
            .build();

        // when // then
        assertThatThrownBy(() -> personalTrainingService.updatePtMemberDetailInfo(ptId, request))
            .isInstanceOf(PTException.class)
            .hasMessage("PT 잔여 횟수는 전체 횟수보다 많을 수 없습니다.");
    }

    @Nested
    @DisplayName("트레이너의 담당 회원 조회")
    class getPtAssignedMembers {

        @DisplayName("특정 체육관 필터링, 이름X")
        @Test
        void getPtAssignedMembersInformationWhenSelectGym() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = gym2.getId();
            final String name = null;

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(3)
                .extracting("member.name", "gym.name", "pt.infoInputStatus")
                .containsExactly(
                    tuple("회원4", "체육관2", PTInfoInputStatus.INFO_EMPTY),
                    tuple("회원3", "체육관2", PTInfoInputStatus.INFO_REGISTERED),
                    tuple("회원5", "체육관2", PTInfoInputStatus.INFO_REGISTERED)
                );
        }

        @DisplayName("특정 체육관 필터링, 이름 검색")
        @Test
        void getPtAssignedMembersInformationWhenSelectGymAndSearchName() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = gym2.getId();
            final String name = "회원5";

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(1)
                .extracting("member.name", "gym.name", "pt.infoInputStatus")
                .containsExactly(
                    tuple("회원5", "체육관2", PTInfoInputStatus.INFO_REGISTERED)
                );
        }

        @DisplayName("특정 체육관 필터링, 이름 검색(검색 결과 X)")
        @Test
        void getPtAssignedMembersInformationWhenSelectGymAndSearchNameNotFound() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = gym2.getId();
            final String name = "회원11";

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(0);
        }

        @DisplayName("체육관 필터링 X, 이름X")
        @Test
        void getPtAssignedMembersInformation() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = -1L;
            final String name = null;

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(5)
                .extracting("member.name", "gym.name", "pt.infoInputStatus")
                .containsExactly(
                    tuple("회원2", "체육관1", PTInfoInputStatus.INFO_EMPTY),
                    tuple("회원4", "체육관2", PTInfoInputStatus.INFO_EMPTY),
                    tuple("회원1", "체육관1", PTInfoInputStatus.INFO_REGISTERED),
                    tuple("회원3", "체육관2", PTInfoInputStatus.INFO_REGISTERED),
                    tuple("회원5", "체육관2", PTInfoInputStatus.INFO_REGISTERED)
                );
        }

        @DisplayName("체육관 필터링 X, 이름 검색")
        @Test
        void getPtAssignedMembersInformationSearchName() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = -1L;
            final String name = "회원3";

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(1)
                .extracting("member.name", "gym.name", "pt.infoInputStatus")
                .containsExactly(
                    tuple("회원3", "체육관2", PTInfoInputStatus.INFO_REGISTERED)
                );
        }

        @DisplayName("체육관 필터링 X, 이름 검색(검색 결과 X)")
        @Test
        void getPtAssignedMembersInformationAndSearchNameNotFound() {
            // given
            LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
            LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

            LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
            LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));

            Gym gym1 = gymRepository.save(createGym("체육관1"));
            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.NEW_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, "노트", 50, 32, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));

            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Member member5 = memberRepository.save(createMember("회원5"));

            Gym gym2 = gymRepository.save(createGym("체육관2"));
            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

            personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, null, PtRegistrationAllowedStatus.ALLOWED, null, null, registrationAllowedDate));
            personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, "노트", 40, 20, registrationRequestDate, PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED, centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate));

            final Long trainerId = trainer.getId();
            final Long gymId = -1L;
            final String name = "회원43";

            // when
            List<MemberDetailInfoResponse> responses = personalTrainingService.searchPtMembersInformation(trainerId, gymId, name);

            // then
            assertThat(responses).hasSize(0);
        }
    }

    @DisplayName("PT 회원 통계 정보 조회")
    @Test
    void getPtStatistics() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Trainer trainer2 = trainerRepository.save(createTrainer("트레이너2"));

        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));
        Member member3 = memberRepository.save(createMember("회원3"));
        Member member4 = memberRepository.save(createMember("회원4"));
        Member member5 = memberRepository.save(createMember("회원5"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));

        GymTrainer gymTrainer_ = gymTrainerRepository.save(createGymTrainer(gym1, trainer2, LocalDate.of(2024, 9, 30)));
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer_, PtRegistrationStatus.NEW_REGISTRATION, YearMonth.of(2024, 9), null));
        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 30)));
        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 30)));

        PersonalTraining personalTraining1 = personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, PtRegistrationStatus.NEW_REGISTRATION, YearMonth.of(2024, 9), null));
        PersonalTraining personalTraining2 = personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, PtRegistrationStatus.RE_REGISTRATION, YearMonth.of(2024, 7), YearMonth.of(2024, 9)));
        PersonalTraining personalTraining3 = personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer2, PtRegistrationStatus.RE_REGISTRATION, YearMonth.of(2024, 7), YearMonth.of(2024, 9)));
        PersonalTraining personalTraining4 = personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, PtRegistrationStatus.RE_REGISTRATION, YearMonth.of(2024, 2), YearMonth.of(2024, 7)));
        PersonalTraining personalTraining5 = personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, PtRegistrationStatus.RE_REGISTRATION, YearMonth.of(2023, 12), YearMonth.of(2024, 8)));

        personalTrainingInfoRepository.save(createPTInfo(2023, 12, PtRegistrationStatus.NEW_REGISTRATION, personalTraining5));
        personalTrainingInfoRepository.save(createPTInfo(2024, 2, PtRegistrationStatus.NEW_REGISTRATION, personalTraining4));
        personalTrainingInfoRepository.save(createPTInfo(2024, 6, PtRegistrationStatus.RE_REGISTRATION, personalTraining4));
        personalTrainingInfoRepository.save(createPTInfo(2024, 6, PtRegistrationStatus.RE_REGISTRATION, personalTraining5));
        personalTrainingInfoRepository.save(createPTInfo(2024, 7, PtRegistrationStatus.NEW_REGISTRATION, personalTraining3));
        personalTrainingInfoRepository.save(createPTInfo(2024, 7, PtRegistrationStatus.RE_REGISTRATION, personalTraining4));
        personalTrainingInfoRepository.save(createPTInfo(2024, 7, PtRegistrationStatus.NEW_REGISTRATION, personalTraining2));
        personalTrainingInfoRepository.save(createPTInfo(2024, 8, PtRegistrationStatus.RE_REGISTRATION, personalTraining5));
        personalTrainingInfoRepository.save(createPTInfo(2024, 9, PtRegistrationStatus.RE_REGISTRATION, personalTraining2));
        personalTrainingInfoRepository.save(createPTInfo(2024, 9, PtRegistrationStatus.RE_REGISTRATION, personalTraining3));
        personalTrainingInfoRepository.save(createPTInfo(2024, 9, PtRegistrationStatus.NEW_REGISTRATION, personalTraining1));

        LocalDate date = LocalDate.of(2024, 9, 10);
        int size = 5;

        // when
        MonthlyStatisticsResponse response = personalTrainingService.getPtStatistics(trainer.getId(), date, size);

        // then
        System.out.println("================\n");
        System.out.println(response);
        assertThat(response.getStatistics()).hasSize(5)
            .extracting("date", "total", "existingMemberCount", "reEnrolledMemberCount", "newMemberCount")
            .containsExactly(
                tuple(YearMonth.of(2024, 9), 5L, 2L, 2L, 1L),
                tuple(YearMonth.of(2024, 8), 4L, 3L, 1L, 0L),
                tuple(YearMonth.of(2024, 7), 4L, 1L, 1L, 2L),
                tuple(YearMonth.of(2024, 6), 2L, 0L, 2L, 0L),
                tuple(YearMonth.of(2024, 5), 2L, 2L, 0L, 0L)
            );
    }

    private PersonalTrainingInfo createPTInfo(int year, int month, PtRegistrationStatus registrationStatus, PersonalTraining personalTraining) {
        return PersonalTrainingInfo.builder()
            .registrationDate(LocalDateTime.of(year, month, 1, 0, 0))
            .registrationStatus(registrationStatus)
            .personalTraining(personalTraining)
            .build();
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, String note, int totalPtCount, int remainingPtCount, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus, LocalDateTime centerFirstRegistrationMonth, LocalDateTime centerLastReRegistrationMonth, LocalDateTime registrationAllowedDate) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .note(note)
            .centerFirstRegistrationMonth(centerFirstRegistrationMonth)
            .centerLastReRegistrationMonth(centerLastReRegistrationMonth)
            .registrationRequestDate(registrationRequestDate)
            .registrationAllowedDate(registrationAllowedDate)
            .registrationStatus(registrationStatus)
            .infoInputStatus(infoInputStatus)
            .registrationAllowedStatus(registrationAllowedStatus)
            .build();
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, int totalPtCount, int remainingPtCount) {
        return createPersonalTraining(member, gymTrainer, null, totalPtCount, remainingPtCount, null, null, null, PtRegistrationAllowedStatus.ALLOWED, null, null, null);
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationAllowedDate, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return createPersonalTraining(member, gymTrainer, null, 0, 0, null, null, null, registrationAllowedStatus, null, null, registrationAllowedDate);
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return createPersonalTraining(member, gymTrainer, null, 0, 0, registrationRequestDate, infoInputStatus, registrationStatus, registrationAllowedStatus, null, null, null);
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, PtRegistrationStatus registrationStatus, YearMonth centerFirstRegistrationYearMonth, YearMonth centerLastReRegistrationYearMonth) {
        return createPersonalTraining(member, gymTrainer, null, 0, 0, null, null, registrationStatus, null,
            LocalDateTime.of(centerFirstRegistrationYearMonth.atDay(1), LocalTime.of(0, 0, 0)),
            centerLastReRegistrationYearMonth != null ? LocalDateTime.of(centerLastReRegistrationYearMonth.atDay(1), LocalTime.of(0, 0, 0)) : null, null);
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

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .hireDate(LocalDate.of(2024, 9, 27))
            .build();
    }
}