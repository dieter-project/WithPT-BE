package com.sideproject.withpt.application.pt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.repository.dto.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.ExerciseFrequency;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class PersonalTrainingQueryRepositoryTest {

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


    @DisplayName("체육관 목록(페이징)과 트레이너로 승인된(PtRegistrationAllowedStatus.APPROVED) PT 목록들을 가져온다.")
    @Test
    @Transactional
    void getGymMemberCountBy() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Gym gym3 = gymRepository.save(createGym("체육관3"));

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer, LocalDate.of(2024, 9, 27)));
        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));
        Member member3 = memberRepository.save(createMember("회원3"));
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer1, LocalDateTime.of(2024, 9, 22, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer1, LocalDateTime.of(2024, 9, 23, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer1, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 40000), PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer, LocalDate.of(2024, 9, 27)));
        Member member4 = memberRepository.save(createMember("회원4"));
        Member member5 = memberRepository.save(createMember("회원5"));
        personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer2, LocalDateTime.of(2024, 9, 25, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer2, LocalDateTime.of(2024, 9, 25, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));

        GymTrainer gymTrainer3 = gymTrainerRepository.save(createGymTrainer(gym3, trainer, LocalDate.of(2024, 9, 27)));
        Member member6 = memberRepository.save(createMember("회원6"));
        Member member7 = memberRepository.save(createMember("회원7"));
        personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member7, gymTrainer3, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.WAITING));

        LocalDateTime currentDateTime = LocalDateTime.of(2024, 9, 27, 12, 45, 1, 30000);

        // when
        List<GymMemberCountDto> gymMemberCountDtos = personalTrainingRepository.getGymMemberCountBy(List.of(gymTrainer1, gymTrainer2, gymTrainer3), currentDateTime);

        // then
        assertThat(gymMemberCountDtos).hasSize(3)
            .extracting("gymName", "memberCount")
            .containsExactlyInAnyOrder(
                tuple("체육관1", 2L),
                tuple("체육관2", 2L),
                tuple("체육관3", 1L)
            );
    }

    @DisplayName("PT 등록 허용 상태에 따른 회원 리스트 조회 - PtRegistrationAllowedStatus.WAITING")
    @Test
    void findAllPtMembersByRegistrationAllowedStatusAndDateWhenPtRegistrationAllowedStatusIsWAITING() {
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
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));
        personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));
        personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));
        personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));
        personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));

        final LocalDateTime registrationAllowedDate = null;
        final PtRegistrationAllowedStatus allowedStatus = PtRegistrationAllowedStatus.WAITING;
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        EachGymMemberListResponse response = personalTrainingRepository.findAllPtMembersByRegistrationAllowedStatusAndDate(gymTrainer, allowedStatus, registrationAllowedDate, pageable);

        // then
        assertThat(response.getTotalMembers()).isEqualTo(5);
        assertThat(response.getMemberList().getContent()).hasSize(5)
            .extracting("member.name", "member.pt.registrationAllowedStatus")
            .containsExactlyInAnyOrder(
                tuple("회원1", PtRegistrationAllowedStatus.WAITING),
                tuple("회원2", PtRegistrationAllowedStatus.WAITING),
                tuple("회원3", PtRegistrationAllowedStatus.WAITING),
                tuple("회원5", PtRegistrationAllowedStatus.WAITING),
                tuple("회원6", PtRegistrationAllowedStatus.WAITING)
            );
    }

    @DisplayName("PT 등록 허용 상태에 따른 회원 리스트 조회 - PtRegistrationAllowedStatus.ALLOWED")
    @Test
    void findAllPtMembersByRegistrationAllowedStatusAndDateWhenPtRegistrationAllowedStatusIsALLOWED() {
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
        personalTrainingRepository.save(createPersonalTraining(member1, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member2, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member3, gymTrainer, null, PtRegistrationAllowedStatus.WAITING));
        personalTrainingRepository.save(createPersonalTraining(member4, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member5, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 10000), PtRegistrationAllowedStatus.ALLOWED));
        personalTrainingRepository.save(createPersonalTraining(member6, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45, 1, 40000), PtRegistrationAllowedStatus.ALLOWED));

        final LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1, 30000);
        final PtRegistrationAllowedStatus allowedStatus = PtRegistrationAllowedStatus.ALLOWED;
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        EachGymMemberListResponse response = personalTrainingRepository.findAllPtMembersByRegistrationAllowedStatusAndDate(gymTrainer, allowedStatus, registrationAllowedDate, pageable);

        // then
        assertThat(response.getTotalMembers()).isEqualTo(4);
        List<PtMemberListDto> content = response.getMemberList().getContent();
        assertThat(content).hasSize(4)
            .extracting("member.name", "member.pt.registrationAllowedStatus")
            .containsExactlyInAnyOrder(
                tuple("회원1", PtRegistrationAllowedStatus.ALLOWED),
                tuple("회원2", PtRegistrationAllowedStatus.ALLOWED),
                tuple("회원4", PtRegistrationAllowedStatus.ALLOWED),
                tuple("회원5", PtRegistrationAllowedStatus.ALLOWED)
            );
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationAllowedDate, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationAllowedStatus(registrationAllowedStatus)
            .registrationAllowedDate(registrationAllowedDate)
            .build();
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