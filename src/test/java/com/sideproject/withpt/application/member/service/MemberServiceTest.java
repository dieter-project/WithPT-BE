package com.sideproject.withpt.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.controller.request.EditMemberDietTypeRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberExerciseFrequencyRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberTargetWeightRequest;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.member.service.response.MemberAndPTInfoResponse;
import com.sideproject.withpt.application.member.service.response.MemberSearchResponse;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private PersonalTrainingRepository personalTrainingRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원 정보 수정")
    public void editMemberInfo() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberInfoRequest request = new EditMemberInfoRequest(
            "test2",
            Sex.WOMAN,
            "2024-07-19",
            173.0,
            73.5
        );

        Long memberId = member.getId();

        //when
        memberService.editMemberInfo(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getHeight()).isEqualTo(request.getHeight());
        assertThat(findMember.getName()).isEqualTo("test2");
    }

    @Test
    @DisplayName("식단 수정")
    public void editDietType() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberDietTypeRequest request = new EditMemberDietTypeRequest(DietType.PROTEIN);
        Long memberId = member.getId();

        //when
        memberService.editDietType(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getDietType()).isEqualTo(DietType.PROTEIN);
    }

    @Test
    @DisplayName("운동목표 수정")
    public void editExerciseFrequency() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberExerciseFrequencyRequest request = new EditMemberExerciseFrequencyRequest(ExerciseFrequency.FIRST_TIME);
        Long memberId = member.getId();

        //when
        memberService.editExerciseFrequency(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getExerciseFrequency()).isEqualTo(ExerciseFrequency.FIRST_TIME);
    }

    @Test
    @DisplayName("목표체중 수정")
    public void editTargetWeight() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberTargetWeightRequest request = new EditMemberTargetWeightRequest(80.5);
        Long memberId = member.getId();

        //when
        memberService.editTargetWeight(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getTargetWeight()).isEqualTo(request.getTargetWeight());
    }

    @DisplayName("이름으로 회원 검색")
    @Test
    void searchMembers() {
        // given
        memberRepository.saveAll(
            List.of(
                createMember("test1", "test1@test.com"),
                createMember("test2", "test2@test.com"),
                createMember("test3", "test3@test.com"),
                createMember("test4", "test4@test.com"),
                createMember("test5", "test5@test.com"),
                createMember("test6", "test6@test.com"),
                createMember("test7", "test7@test.com"),
                createMember("test8", "test8@test.com"),
                createMember("test9", "test9@test.com"),
                createMember("test10", "test10@test.com")
            )
        );

        String name = "1";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberSearchResponse> result = memberService.searchMembers(pageable, name);

        // then
        assertThat(result.getContent()).hasSize(2)
            .extracting("name", "email")
            .containsExactly(
                tuple("test1", "test1@test.com"),
                tuple("test10", "test10@test.com")
            );

    }

    @DisplayName("회원 정보 조회 - PT 정보 없을 때")
    @Test
    void getMemberInfo() {
        // given
        Member member = memberRepository.save(createMember("회원", "member@test.com"));
//        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
//        Gym gym = gymRepository.save(createGym("체육관"));
//
//        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
//        PersonalTraining personalTraining = personalTrainingRepository.save(
//            createPersonalTraining(
//                member, gymTrainer,
//                LocalDateTime.of(2024, 10, 27, 12, 45),
//                PTInfoInputStatus.INFO_EMPTY,
//                PtRegistrationStatus.ALLOWED_BEFORE,
//                PtRegistrationAllowedStatus.WAITING)
//        );

        // when
        MemberAndPTInfoResponse response = memberService.getMemberInfo(member.getId());

        // then
        assertThat(response.getMemberInfo())
            .extracting("name", "email")
            .contains("회원", "member@test.com");

        assertThat(response.getPtInfos()).isEmpty();
    }

    @DisplayName("회원 정보 조회 - PT 정보 있을 때")
    @Test
    void getMemberInfoWithPTInfo() {
        // given
        Member member = memberRepository.save(createMember("회원", "member@test.com"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
        personalTrainingRepository.save(
            createPersonalTraining(
                member, gymTrainer,
                LocalDateTime.of(2024, 10, 27, 12, 45),
                PTInfoInputStatus.INFO_EMPTY,
                PtRegistrationStatus.ALLOWED_BEFORE,
                PtRegistrationAllowedStatus.WAITING)
        );

        // when
        MemberAndPTInfoResponse response = memberService.getMemberInfo(member.getId());

        // then
        assertThat(response.getMemberInfo())
            .extracting("name", "email")
            .contains("회원", "member@test.com");

        assertThat(response.getPtInfos()).hasSize(1)
            .extracting("pt.infoInputStatus", "pt.registrationStatus", "pt.registrationAllowedStatus")
            .contains(
                tuple(PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING)
            )
        ;
    }

    private Member createMember(String name, String email) {
        return Member.builder()
            .name(name)
            .email(email)
            .authProvider(AuthProvider.KAKAO)
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
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

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return createPersonalTraining(member, gymTrainer, null, 0, 0, registrationRequestDate, infoInputStatus, registrationStatus, registrationAllowedStatus, null, null, null);
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