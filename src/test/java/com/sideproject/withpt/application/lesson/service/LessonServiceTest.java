package com.sideproject.withpt.application.lesson.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonRegistrationResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.ExerciseFrequency;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.LessonSchedule;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class LessonServiceTest {

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
    private LessonRepository lessonRepository;

    @Autowired
    private LessonService lessonService;

    @DisplayName("트레이너가 수업 등록을 하면 바로 수업 예약된다.")
    @Test
    void registrationPTLessonByTrainer() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 30, 10,
                PTInfoInputStatus.INFO_REGISTERED,
                PtRegistrationStatus.NEW_REGISTRATION,
                PtRegistrationAllowedStatus.ALLOWED)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(trainer.getId())
            .registrationReceiverId(member.getId())
            .date(LocalDate.of(2024, 10, 4))
            .weekday(Day.FRI)
            .time(LocalTime.of(20, 44))
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.TRAINER.name();

        // when
        LessonRegistrationResponse response = lessonService.registrationPTLesson(gymId, loginRole, request);

        // then
        assertThat(response)
            .extracting("from", "to", "schedule.date", "schedule.time", "schedule.weekday", "status", "registeredBy")
            .contains(trainer.getId(), member.getId(),
                LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, LessonStatus.RESERVED, Role.TRAINER.name());
    }

    @DisplayName("회원이 수업 등록 요청을 하면 \"승인 대기 중\" 상태로 등록된다.")
    @Test
    void registrationPTLessonByMember() {
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 30, 10,
                PTInfoInputStatus.INFO_REGISTERED,
                PtRegistrationStatus.NEW_REGISTRATION,
                PtRegistrationAllowedStatus.ALLOWED)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(LocalDate.of(2024, 10, 4))
            .weekday(Day.FRI)
            .time(LocalTime.of(20, 44))
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.MEMBER.name();

        // when
        LessonRegistrationResponse response = lessonService.registrationPTLesson(gymId, loginRole, request);

        // then
        assertThat(response)
            .extracting("from", "to", "schedule.date", "schedule.time", "schedule.weekday", "status", "registeredBy")
            .contains(member.getId(), trainer.getId(),
                LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, LessonStatus.PENDING_APPROVAL, Role.MEMBER.name());
    }

    @DisplayName("회원이 PT 등록을 허용하지 않으면 수업 등록 요청이 불가능하다.")
    @Test
    void registrationPTLessonValidationPT1() {
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 0, 0,
                PTInfoInputStatus.INFO_EMPTY,
                PtRegistrationStatus.ALLOWED_BEFORE,
                PtRegistrationAllowedStatus.WAITING)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(LocalDate.of(2024, 10, 4))
            .weekday(Day.FRI)
            .time(LocalTime.of(20, 44))
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.MEMBER.name();

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, loginRole, request))
            .isInstanceOf(PTException.class)
            .hasMessage("아직 PT 등록을 허용하지 않은 회원입니다.");
    }

    @DisplayName("트레이너 측에서 회원에 대한 PT 상세 정보를 입럭하지 않았으면 오류")
    @Test
    void registrationPTLessonValidationPT2() {
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 0, 0,
                PTInfoInputStatus.INFO_EMPTY,
                PtRegistrationStatus.ALLOWED,
                PtRegistrationAllowedStatus.ALLOWED)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(LocalDate.of(2024, 10, 4))
            .weekday(Day.FRI)
            .time(LocalTime.of(20, 44))
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.MEMBER.name();

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, loginRole, request))
            .isInstanceOf(PTException.class)
            .hasMessage("PT 상세 정보를 입력하지 않으셨습니다.");
    }

    @DisplayName("잔여 PT 횟수가 0 이하이면 수업 등록 X")
    @Test
    void registrationPTLessonValidationPT3() {
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 30, 0,
                PTInfoInputStatus.INFO_REGISTERED,
                PtRegistrationStatus.ALLOWED,
                PtRegistrationAllowedStatus.ALLOWED)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(LocalDate.of(2024, 10, 4))
            .weekday(Day.FRI)
            .time(LocalTime.of(20, 44))
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.MEMBER.name();

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, loginRole, request))
            .isInstanceOf(PTException.class)
            .hasMessage("잔여 PT 횟수가 남아 있지 않습니다.");
    }

    @DisplayName("(날짜 + 시간) 수업이 이미 예약 or 승인 대기 중이면 요청이 거부된다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"RESERVED", "PENDING_APPROVAL"})
    @ParameterizedTest
    void registrationPTLessonValidationLessonTime(LessonStatus lessonStatus) {
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer, 30, 10,
                PTInfoInputStatus.INFO_REGISTERED,
                PtRegistrationStatus.ALLOWED,
                PtRegistrationAllowedStatus.ALLOWED)
        );
        LocalDate requestDate = LocalDate.of(2024, 10, 4);
        LocalTime requestTime = LocalTime.of(20, 44);

        LessonSchedule lessonSchedule = LessonSchedule.builder()
            .date(requestDate)
            .time(requestTime)
            .weekday(Day.FRI)
            .build();

        lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, lessonStatus, "TRAINER")
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(requestDate)
            .weekday(Day.FRI)
            .time(requestTime)
            .build();

        Long gymId = gym.getId();
        String loginRole = Role.MEMBER.name();

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, loginRole, request))
            .isInstanceOf(LessonException.class)
            .hasMessage("이미 예약된 수업입니다.");
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status, String registeredBy) {
        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .schedule(schedule)
            .status(status)
            .registeredBy(registeredBy)
            .build();
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, int totalPtCount, int remainingPtCount, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .registrationStatus(registrationStatus)
            .infoInputStatus(infoInputStatus)
            .registrationAllowedStatus(registrationAllowedStatus)
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

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .build();
    }
}