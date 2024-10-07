package com.sideproject.withpt.application.lesson.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonScheduleOfMonthResponse;
import com.sideproject.withpt.application.lesson.service.response.MemberLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.service.response.TrainerLessonScheduleResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.schedule.repository.ScheduleRepository;
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
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
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
    private ScheduleRepository scheduleRepository;
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
        Role registrationRequestByRole = Role.TRAINER;

        // when
        LessonResponse response = lessonService.registrationPTLesson(gymId, registrationRequestByRole, request);

        // then
        assertThat(response)
            .extracting("requester", "receiver", "schedule.date", "schedule.time", "schedule.weekday", "beforeSchedule", "status", "registeredBy", "modifiedBy")
            .contains(trainer.getId() + "_TRAINER", member.getId() + "_MEMBER",
                LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, null, LessonStatus.RESERVED, Role.TRAINER, null);
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
        Role registrationRequestByRole = Role.MEMBER;

        // when
        LessonResponse response = lessonService.registrationPTLesson(gymId, registrationRequestByRole, request);

        // then
        assertThat(response)
            .extracting("requester", "receiver", "schedule.date", "schedule.time", "schedule.weekday", "beforeSchedule", "status", "registeredBy", "modifiedBy")
            .contains(member.getId() + "_MEMBER", trainer.getId() + "_TRAINER",
                LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, null, LessonStatus.PENDING_APPROVAL, Role.MEMBER, null);
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
        Role registrationRequestByRole = Role.MEMBER;

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, registrationRequestByRole, request))
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
        Role registrationRequestByRole = Role.MEMBER;

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, registrationRequestByRole, request))
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
        Role registrationRequestByRole = Role.MEMBER;

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, registrationRequestByRole, request))
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
            createLesson(member, gymTrainer, lessonSchedule, lessonStatus, Role.TRAINER)
        );

        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(member.getId())
            .registrationReceiverId(trainer.getId())
            .date(requestDate)
            .weekday(Day.FRI)
            .time(requestTime)
            .build();

        Long gymId = gym.getId();
        Role registrationRequestByRole = Role.MEMBER;

        // when // then
        assertThatThrownBy(() -> lessonService.registrationPTLesson(gymId, registrationRequestByRole, request))
            .isInstanceOf(LessonException.class)
            .hasMessage("이미 예약된 수업입니다.");
    }

    @DisplayName("수업 스케줄 변경")
    @Test
    void changePTLesson() {
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

        String requester = Lesson.getRequester(trainer.getId(), Role.MEMBER);
        String receiver = Lesson.getReceiver(member.getId(), Role.MEMBER);

        LessonSchedule lessonSchedule = LessonSchedule.builder()
            .date(LocalDate.of(2024, 10, 4))
            .time(LocalTime.of(20, 44))
            .weekday(Day.FRI)
            .build();

        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.RESERVED, requester, receiver, Role.TRAINER, null)
        );

        Role registrationRequestByRole = Role.MEMBER;
        LessonChangeRequest request = LessonChangeRequest.builder()
            .date(LocalDate.of(2024, 10, 5))
            .weekday(Day.SAT)
            .time(LocalTime.of(16, 0))
            .build();

        // when
        LessonResponse response = lessonService.changePTLesson(lesson.getId(), registrationRequestByRole, request);

        // then
        assertThat(response)
            .extracting("requester", "receiver", "status", "registeredBy", "modifiedBy")
            .contains(requester, receiver, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER);

        assertThat(response.getBeforeSchedule())
            .extracting("date", "time", "weekday")
            .contains(LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI);

        assertThat(response.getSchedule())
            .extracting("date", "time", "weekday")
            .contains(request.getDate(), request.getTime(), request.getWeekday());
    }

    @DisplayName("예약 상태가 아닌 수업은 스케줄 변경이 불가능합니다.")
    @EnumSource(mode = Mode.EXCLUDE, names = "RESERVED")
    @ParameterizedTest
    void changePTLessonWhenNonBook(LessonStatus lessonStatus) {
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

        String requester = Lesson.getRequester(trainer.getId(), Role.MEMBER);
        String receiver = Lesson.getReceiver(member.getId(), Role.MEMBER);

        LessonSchedule lessonSchedule = LessonSchedule.builder()
            .date(LocalDate.of(2024, 10, 4))
            .time(LocalTime.of(20, 44))
            .weekday(Day.FRI)
            .build();

        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, lessonStatus, Role.TRAINER)
        );

        Role registrationRequestByRole = Role.MEMBER;
        LessonChangeRequest request = LessonChangeRequest.builder()
            .date(LocalDate.of(2024, 10, 5))
            .weekday(Day.SAT)
            .time(LocalTime.of(16, 0))
            .build();

        // when // then
        assertThatThrownBy(() -> lessonService.changePTLesson(lesson.getId(), registrationRequestByRole, request))
            .isInstanceOf(LessonException.class)
            .hasMessage("예약 상태가 아닌 수업은 스케줄 변경이 불가능합니다.");
    }

    @DisplayName("수업관리/확정된 수업 - 수업 직접 취소하기")
    @Test
    void cancelLesson() {
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

        LessonSchedule lessonSchedule = LessonSchedule.builder()
            .date(LocalDate.of(2024, 10, 4))
            .time(LocalTime.of(20, 44))
            .weekday(Day.FRI)
            .build();

        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.RESERVED, Lesson.getRequester(trainer.getId(), Role.MEMBER), Lesson.getReceiver(member.getId(), Role.MEMBER), Role.TRAINER, null)
        );

        // when
        LessonResponse response = lessonService.cancelLesson(lesson.getId(), LessonStatus.CANCELED);

        // then
        assertThat(response.getStatus()).isEqualTo(LessonStatus.CANCELED);
    }

    @DisplayName("예약 상태가 아닌 수업은 취소가 불가능합니다.")
    @EnumSource(mode = Mode.EXCLUDE, names = "RESERVED")
    @ParameterizedTest
    void cancelLessonWhenNotCancel(LessonStatus lessonStatus) {
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

        LessonSchedule lessonSchedule = LessonSchedule.builder()
            .date(LocalDate.of(2024, 10, 4))
            .time(LocalTime.of(20, 44))
            .weekday(Day.FRI)
            .build();

        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, null, lessonStatus, Lesson.getRequester(trainer.getId(), Role.MEMBER), Lesson.getReceiver(member.getId(), Role.MEMBER), Role.TRAINER, null)
        );

        // when
        assertThatThrownBy(() -> lessonService.cancelLesson(lesson.getId(), LessonStatus.CANCELED))
            .isInstanceOf(LessonException.class)
            .hasMessage("예약 상태가 아닌 수업은 취소가 불가능합니다.");
    }

    @DisplayName("예약 가능한 수업 시간 조회")
    @Test
    void getTrainerAvailableLessonSchedule() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        LocalDate date = LocalDate.of(2024, 10, 4);

        LessonSchedule lessonSchedule1 = createLessonSchedule(date, LocalTime.of(9, 0), Day.FRI);
        LessonSchedule lessonSchedule2 = createLessonSchedule(date, LocalTime.of(10, 0), Day.FRI);
        LessonSchedule lessonSchedule3 = createLessonSchedule(date, LocalTime.of(12, 0), Day.FRI);
        LessonSchedule lessonSchedule4 = createLessonSchedule(date, LocalTime.of(15, 0), Day.FRI);
        LessonSchedule lessonSchedule5 = createLessonSchedule(date, LocalTime.of(17, 0), Day.FRI);
        LessonSchedule lessonSchedule6 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(21, 35), Day.FRI);

        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer, lessonSchedule1, LessonStatus.RESERVED),
                createLesson(member, gymTrainer, lessonSchedule2, LessonStatus.PENDING_APPROVAL),
                createLesson(member, gymTrainer, lessonSchedule3, LessonStatus.RESERVED),
                createLesson(member, gymTrainer, lessonSchedule4, LessonStatus.RESERVED),
                createLesson(member, gymTrainer, lessonSchedule5, LessonStatus.CANCELED),
                createLesson(member, gymTrainer, lessonSchedule6, LessonStatus.RESERVED)
            )
        );

        scheduleRepository.save(createWorkSchedule(Day.FRI, LocalTime.of(9, 0), LocalTime.of(18, 0), gymTrainer));

        // when
        AvailableLessonScheduleResponse response = lessonService.getTrainerAvailableLessonSchedule(gym.getId(), trainer.getId(), Day.FRI, date);

        // then
        assertThat(response.getDate()).isEqualTo(date);
        assertThat(response.getDay()).isEqualTo(Day.FRI);
        assertThat(response.getSchedules()).hasSize(9)
            .extracting("time", "isBooked")
            .containsExactly(
                Tuple.tuple(LocalTime.of(9, 0), true),
                Tuple.tuple(LocalTime.of(10, 0), true),
                Tuple.tuple(LocalTime.of(11, 0), false),
                Tuple.tuple(LocalTime.of(12, 0), true),
                Tuple.tuple(LocalTime.of(13, 0), false),
                Tuple.tuple(LocalTime.of(14, 0), false),
                Tuple.tuple(LocalTime.of(15, 0), true),
                Tuple.tuple(LocalTime.of(16, 0), false),
                Tuple.tuple(LocalTime.of(17, 0), false)
            );
    }

    @Nested
    @DisplayName("요청 날짜에 예약된 수업 목록 조회")
    class GetLessonScheduleByDate {

        @DisplayName("트레이너의 수업 목록 조회 - 체육관 필터X")
        @Test
        void getTrainerLessonScheduleByDate() {
            // given
            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));
            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(9, 0), Day.SAT);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(11, 0), Day.SAT);
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(12, 0), Day.SAT);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(14, 0), Day.SAT);
            lessonRepository.saveAll(List.of(
                    createLesson(member1, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member2, gymTrainer1, lessonSchedule2, LessonStatus.RESERVED),
                    createLesson(member3, gymTrainer1, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member4, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
            LessonSchedule lessonSchedule5 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule6 = createLessonSchedule(LocalDate.of(2024, 10, 6), LocalTime.of(18, 0), Day.SUN);
            lessonRepository.saveAll(List.of(
                    createLesson(member2, gymTrainer2, lessonSchedule5, LessonStatus.RESERVED),
                    createLesson(member4, gymTrainer2, lessonSchedule6, LessonStatus.RESERVED)
                )
            );

            final Long trainerId = trainer.getId();
            final Long gymId = -1L;
            final LocalDate date = LocalDate.of(2024, 10, 5);

            // when
            TrainerLessonScheduleResponse response = lessonService.getTrainerLessonScheduleByDate(trainerId, gymId, date);

            // then
            assertThat(response.getLessonInfos()).hasSize(5)
                .extracting("member.name", "lesson.schedule.time", "lesson.status", "gym.name")
                .contains(
                    tuple("회원1", LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원2", LocalTime.of(11, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원3", LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원4", LocalTime.of(14, 0), LessonStatus.CANCELED, "체육관1"),
                    tuple("회원2", LocalTime.of(15, 0), LessonStatus.RESERVED, "체육관2")
                );
        }

        @DisplayName("트레이너의 수업 목록 조회 - 특정 체육관 필터")
        @Test
        void getTrainerLessonScheduleByDateWhenSelectSpecificGym() {
            // given
            Member member1 = memberRepository.save(createMember("회원1"));
            Member member2 = memberRepository.save(createMember("회원2"));
            Member member3 = memberRepository.save(createMember("회원3"));
            Member member4 = memberRepository.save(createMember("회원4"));
            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(9, 0), Day.SAT);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(11, 0), Day.SAT);
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(12, 0), Day.SAT);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(14, 0), Day.SAT);
            lessonRepository.saveAll(List.of(
                    createLesson(member1, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member2, gymTrainer1, lessonSchedule2, LessonStatus.RESERVED),
                    createLesson(member3, gymTrainer1, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member4, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
            LessonSchedule lessonSchedule5 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule6 = createLessonSchedule(LocalDate.of(2024, 10, 6), LocalTime.of(18, 0), Day.SUN);
            lessonRepository.saveAll(List.of(
                    createLesson(member2, gymTrainer2, lessonSchedule5, LessonStatus.RESERVED),
                    createLesson(member4, gymTrainer2, lessonSchedule6, LessonStatus.RESERVED)
                )
            );

            final Long trainerId = trainer.getId();
            final Long gymId = gym1.getId();
            final LocalDate date = LocalDate.of(2024, 10, 5);

            // when
            TrainerLessonScheduleResponse response = lessonService.getTrainerLessonScheduleByDate(trainerId, gymId, date);

            // then
            assertThat(response.getLessonInfos()).hasSize(4)
                .extracting("member.name", "lesson.schedule.time", "lesson.status", "gym.name")
                .contains(
                    tuple("회원1", LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원2", LocalTime.of(11, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원3", LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("회원4", LocalTime.of(14, 0), LessonStatus.CANCELED, "체육관1")
                );
        }

        @DisplayName("회원의 수업 목록 조회 - 날짜 필터 X")
        @Test
        void getMemberLessonScheduleByDate() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer1 = trainerRepository.save(createTrainer("트레이너1"));
            Trainer trainer2 = trainerRepository.save(createTrainer("트레이너2"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer1));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(9, 0), Day.MON);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 9), LocalTime.of(11, 0), Day.WED);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.CANCELED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), Day.MON);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(14, 0), Day.THU);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer2, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer2, lessonSchedule4, LessonStatus.RESERVED)
                )
            );

            final Long memberId = member.getId();
            final LocalDate date = null;

            // when
            MemberLessonScheduleResponse response = lessonService.getMemberLessonScheduleByDate(memberId, date);

            // then
            assertThat(response.getLessonInfos()).hasSize(3)
                .extracting("trainer.name", "lesson.schedule.date", "lesson.schedule.time", "lesson.status", "gym.name")
                .containsExactly(
                    tuple("트레이너1", LocalDate.of(2024, 10, 7), LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("트레이너2", LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관2"),
                    tuple("트레이너2", LocalDate.of(2024, 10, 10), LocalTime.of(14, 0), LessonStatus.RESERVED, "체육관2")
                );
        }

        @DisplayName("회원의 수업 목록 조회 - 날짜 필터")
        @Test
        void getMemberLessonScheduleByDateWhenFilteringDate() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer1 = trainerRepository.save(createTrainer("트레이너1"));
            Trainer trainer2 = trainerRepository.save(createTrainer("트레이너2"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer1));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(9, 0), Day.MON);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 9), LocalTime.of(11, 0), Day.WED);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.CANCELED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), Day.MON);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(14, 0), Day.THU);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer2, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer2, lessonSchedule4, LessonStatus.RESERVED)
                )
            );

            final Long memberId = member.getId();
            final LocalDate date = LocalDate.of(2024, 10, 7);

            // when
            MemberLessonScheduleResponse response = lessonService.getMemberLessonScheduleByDate(memberId, date);

            // then
            assertThat(response.getLessonInfos()).hasSize(2)
                .extracting("trainer.name", "lesson.schedule.date", "lesson.schedule.time", "lesson.status", "gym.name")
                .contains(
                    tuple("트레이너1", LocalDate.of(2024, 10, 7), LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                    tuple("트레이너2", LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관2")
                );
        }
    }

    @Nested
    @DisplayName("월(Month) 전체 체육관 수업 일정 달력 조회")
    class GetLessonScheduleOfMonth {

        @DisplayName("트레이너 - 체육관 필터링 X")
        @Test
        void getTrainerLessonScheduleOfMonth() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(9, 0), Day.SAT);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 6), LocalTime.of(11, 0), Day.SAT);
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), Day.SAT);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 8), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule5 = createLessonSchedule(LocalDate.of(2024, 10, 11), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule6 = createLessonSchedule(LocalDate.of(2024, 10, 15), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule7 = createLessonSchedule(LocalDate.of(2024, 10, 21), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule8 = createLessonSchedule(LocalDate.of(2024, 10, 24), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule9 = createLessonSchedule(LocalDate.of(2024, 10, 29), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule10 = createLessonSchedule(LocalDate.of(2024, 11, 3), LocalTime.of(14, 0), Day.SAT);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED),
                    createLesson(member, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule6, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule7, LessonStatus.TIME_OUT_CANCELED),
                    createLesson(member, gymTrainer1, lessonSchedule8, LessonStatus.COMPLETION),
                    createLesson(member, gymTrainer1, lessonSchedule9, LessonStatus.PENDING_APPROVAL),
                    createLesson(member, gymTrainer1, lessonSchedule10, LessonStatus.RESERVED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
            LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.RESERVED)
                )
            );

            final Long trainerId = trainer.getId();
            final Long gymId = -1L;
            final YearMonth yearMonth = YearMonth.of(2024, 10);

            // when
            LessonScheduleOfMonthResponse response = lessonService.getTrainerLessonScheduleOfMonth(trainerId, gymId, yearMonth);

            // then
            assertThat(response.getFilteringBy()).hasSize(2)
                .contains("체육관1", "체육관2");

            assertThat(response.getDates()).hasSize(9)
                .containsExactly(
                    LocalDate.of(2024, 10, 5),
                    LocalDate.of(2024, 10, 6),
                    LocalDate.of(2024, 10, 7),
                    LocalDate.of(2024, 10, 8),
                    LocalDate.of(2024, 10, 10),
                    LocalDate.of(2024, 10, 11),
                    LocalDate.of(2024, 10, 15),
                    LocalDate.of(2024, 10, 21),
                    LocalDate.of(2024, 10, 24)
                );
        }

        @DisplayName("트레이너 - 체육관 필터링")
        @Test
        void getTrainerLessonScheduleOfMonthWhenFilteringGym() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
            Gym gym1 = gymRepository.save(createGym("체육관1"));
            Gym gym2 = gymRepository.save(createGym("체육관2"));

            GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer));
            LessonSchedule lessonSchedule1 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(9, 0), Day.SAT);
            LessonSchedule lessonSchedule2 = createLessonSchedule(LocalDate.of(2024, 10, 6), LocalTime.of(11, 0), Day.SAT);
            LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), Day.SAT);
            LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 8), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule5 = createLessonSchedule(LocalDate.of(2024, 10, 11), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule6 = createLessonSchedule(LocalDate.of(2024, 10, 15), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule7 = createLessonSchedule(LocalDate.of(2024, 10, 21), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule8 = createLessonSchedule(LocalDate.of(2024, 10, 24), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule9 = createLessonSchedule(LocalDate.of(2024, 10, 29), LocalTime.of(14, 0), Day.SAT);
            LessonSchedule lessonSchedule10 = createLessonSchedule(LocalDate.of(2024, 11, 3), LocalTime.of(14, 0), Day.SAT);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule3, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED),
                    createLesson(member, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule6, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer1, lessonSchedule7, LessonStatus.TIME_OUT_CANCELED),
                    createLesson(member, gymTrainer1, lessonSchedule8, LessonStatus.COMPLETION),
                    createLesson(member, gymTrainer1, lessonSchedule9, LessonStatus.PENDING_APPROVAL),
                    createLesson(member, gymTrainer1, lessonSchedule10, LessonStatus.RESERVED)
                )
            );

            GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
            LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
            lessonRepository.saveAll(List.of(
                    createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.RESERVED),
                    createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.RESERVED)
                )
            );

            final Long trainerId = trainer.getId();
            final Long gymId = gym2.getId();
            final YearMonth yearMonth = YearMonth.of(2024, 10);

            // when
            LessonScheduleOfMonthResponse response = lessonService.getTrainerLessonScheduleOfMonth(trainerId, gymId, yearMonth);

            // then
            assertThat(response.getFilteringBy()).hasSize(1)
                .contains("체육관2");

            assertThat(response.getDates()).hasSize(2)
                .containsExactly(
                    LocalDate.of(2024, 10, 5),
                    LocalDate.of(2024, 10, 10)
                );
        }
    }

    private LessonSchedule createLessonSchedule(LocalDate date, LocalTime time, Day day) {
        return LessonSchedule.builder()
            .date(date)
            .time(time)
            .weekday(day)
            .build();
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, String requester, String receiver, Role registeredBy, Role modifiedBy) {
        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .schedule(schedule)
            .beforeSchedule(beforeSchedule)
            .status(status)
            .requester(requester)
            .receiver(receiver)
            .registeredBy(registeredBy)
            .modifiedBy(modifiedBy)
            .build();
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status, Role registeredBy) {
        return createLesson(member, gymTrainer, schedule, null, status, null, null, registeredBy, null);
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status) {
        return createLesson(member, gymTrainer, schedule, null, status, null, null, null, null);
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

    private WorkSchedule createWorkSchedule(Day day, LocalTime inTime, LocalTime outTime, GymTrainer gymTrainer) {
        return WorkSchedule.builder()
            .weekday(day)
            .inTime(inTime)
            .outTime(outTime)
            .gymTrainer(gymTrainer)
            .build();
    }
}