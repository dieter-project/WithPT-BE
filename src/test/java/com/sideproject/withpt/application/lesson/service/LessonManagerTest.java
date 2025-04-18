package com.sideproject.withpt.application.lesson.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.event.model.LessonNotificationEvent;
import com.sideproject.withpt.application.lesson.event.model.LessonRegistrationNotificationEvent;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RecordApplicationEvents
@ActiveProfiles("test")
@SpringBootTest
class LessonManagerTest {

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
    private LessonManager lessonManager;

    @Autowired
    private ApplicationEvents applicationEvents;

    private static final String MEMBER_REGISTRATION_REQUEST_MSG = "%s 회원 님으로부터 수업 등록 요청이 왔어요.";

    @DisplayName("수업 등록 알림 이벤트")
    @Nested
    class RegistrationPTLesson {

        @DisplayName("트레이너가 수업 등록을 하면 알림 이벤트가 발생")
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

            // when
            LessonResponse response = lessonManager.registrationPTLesson(gymId, request, LocalDateTime.of(2024, 10, 29, 3, 22));

            // then
            assertThat(response)
                .extracting("requester.id", "requester.role", "receiver.id", "receiver.role", "registeredBy", "modifiedBy")
                .contains(trainer.getId(), Role.TRAINER, member.getId(), Role.MEMBER, Role.TRAINER, null);

            assertThat(response)
                .extracting("schedule.date", "schedule.time", "schedule.weekday", "beforeSchedule", "status")
                .contains(LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, null, LessonStatus.RESERVED);

            // 이벤트가 발생여부 확인
            assertThat(applicationEvents.stream(LessonRegistrationNotificationEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                    assertAll(
                        () -> assertThat(event.getRequester()).isEqualTo(trainer),
                        () -> assertThat(event.getReceiver()).isEqualTo(member),
                        () -> assertThat(event.getMessage()).isEqualTo(String.format(MEMBER_REGISTRATION_REQUEST_MSG, trainer.getName())),
                        () -> assertThat(event.getGymId()).isEqualTo(gymId),
                        () -> assertThat(event.getDate()).isEqualTo(LocalDate.of(2024, 10, 4)),
                        () -> assertThat(event.getTime()).isEqualTo(LocalTime.of(20, 44))
                    );
                });
        }

        @DisplayName("회원이 수업 등록 요청을 하면 \"승인 대기 중\" 상태로 등록된다.")
        @Test
        void registrationPTLessonByMember() {
            //given
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

            // when
            LessonResponse response = lessonManager.registrationPTLesson(gymId, request, LocalDateTime.of(2024, 10, 29, 3, 22));

            // then
            assertThat(response)
                .extracting("requester.id", "requester.role", "receiver.id", "receiver.role", "registeredBy", "modifiedBy")
                .contains(member.getId(), Role.MEMBER, trainer.getId(), Role.TRAINER, Role.MEMBER, null);

            assertThat(response)
                .extracting("schedule.date", "schedule.time", "schedule.weekday", "beforeSchedule", "status")
                .contains(LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI, null, LessonStatus.PENDING_APPROVAL);

            // 이벤트가 발행되었는지 확인
            assertThat(applicationEvents.stream(LessonRegistrationNotificationEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                    assertAll(
                        () -> assertThat(event.getRequester()).isEqualTo(member),
                        () -> assertThat(event.getReceiver()).isEqualTo(trainer),
                        () -> assertThat(event.getMessage()).isEqualTo(String.format(MEMBER_REGISTRATION_REQUEST_MSG, member.getName())),
                        () -> assertThat(event.getGymId()).isEqualTo(gymId),
                        () -> assertThat(event.getDate()).isEqualTo(LocalDate.of(2024, 10, 4)),
                        () -> assertThat(event.getTime()).isEqualTo(LocalTime.of(20, 44))
                    );
                });
        }
    }

    private static final String LESSON_CHANGE_REQUEST_MSG = "%s 님으로부터 수업 변경 요청이 도착했어요.";

    @DisplayName("수업 스케줄 변경 이벤트 발행 여부")
    @Test
    void changePTLesson() {
        // given
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
            createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.RESERVED, member, trainer, Role.TRAINER, null)
        );

        LessonChangeRequest request = LessonChangeRequest.builder()
            .date(LocalDate.of(2024, 10, 5))
            .weekday(Day.SAT)
            .time(LocalTime.of(16, 0))
            .build();

        // when
        LessonResponse response = lessonManager.changePTLesson(lesson.getId(), member.getId(), request, LocalDateTime.of(2024, 10, 29, 3, 22));

        // then
        assertThat(response)
            .extracting("requester.role", "receiver.role", "status", "registeredBy", "modifiedBy")
            .contains(Role.MEMBER, Role.TRAINER, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER);

        assertThat(response.getBeforeSchedule())
            .extracting("date", "time", "weekday")
            .contains(LocalDate.of(2024, 10, 4), LocalTime.of(20, 44), Day.FRI);

        assertThat(response.getSchedule())
            .extracting("date", "time", "weekday")
            .contains(request.getDate(), request.getTime(), request.getWeekday());

        // 이벤트가 발생여부 확인
        assertThat(applicationEvents.stream(LessonNotificationEvent.class))
            .hasSize(1)
            .anySatisfy(event -> {
                assertAll(
                    () -> assertThat(event.getRequester()).isEqualTo(member),
                    () -> assertThat(event.getReceiver()).isEqualTo(trainer),
                    () -> assertThat(event.getMessage()).isEqualTo(String.format(LESSON_CHANGE_REQUEST_MSG, member.getName())),
                    () -> assertThat(event.getNotificationType()).isEqualTo(NotificationType.LESSON_CHANGE_REQUEST),
                    () -> assertThat(event.getLesson()).isEqualTo(lesson)
                );
            });
    }

    private String createAcceptanceMessage(Lesson lesson) {
        LessonSchedule schedule = lesson.getSchedule();
        LocalDate date = schedule.getDate();
        LocalTime time = schedule.getTime();

        return String.format("%s 회원님의 %d년 %d월 %d일 %d시 %d분 수업이 예약되었습니다.",
            lesson.getMember().getName(),
            date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            time.getHour(), time.getMinute());
    }

    @DisplayName("수업 등록 or 수업 스케줄 변경 수락하기")
    @Nested
    class RegistrationOrScheduleChangeLessonAccept {

        @DisplayName("회원 -> 트레이너, 수업 등록 수락하기 (트레이너가 수락)")
        @Test
        void registrationOrScheduleChangeLessonAccept() {
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

            LessonSchedule lessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            Lesson savedLesson = lessonRepository.save(createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.PENDING_APPROVAL, member, trainer, Role.MEMBER, null));

            // when
            LessonResponse response = lessonManager.registrationOrScheduleChangeLessonAccept(member.getId(), savedLesson.getId(), LocalDateTime.of(2024, 10, 29, 3, 22));

            // then
            assertThat(response)
                .extracting("schedule.date", "schedule.time", "schedule.weekday", "beforeSchedule", "status", "registeredBy", "modifiedBy")
                .contains(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT, null, LessonStatus.RESERVED, Role.MEMBER, null);

            assertThat(applicationEvents.stream(LessonNotificationEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                    assertAll(
                        () -> assertThat(event.getRequester()).isEqualTo(member),
                        () -> assertThat(event.getReceiver()).isEqualTo(trainer),
                        () -> assertThat(event.getMessage()).isEqualTo(createAcceptanceMessage(savedLesson)),
                        () -> assertThat(event.getNotificationType()).isEqualTo(NotificationType.LESSON_REGISTRATION_COMPLETION),
                        () -> assertThat(event.getLesson()).isEqualTo(savedLesson)
                    );
                });
        }

        @DisplayName("회원 -> 트레이너 수업 변경 요청 수락하기 (트레이너가 수락)")
        @Test
        void registrationOrScheduleChangeLessonAcceptWhenMemberChange() {
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

            LessonSchedule boforeLessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(15, 0), Day.SAT);
            Lesson savedLesson = lessonRepository.save(createLesson(member, gymTrainer, lessonSchedule, boforeLessonSchedule, LessonStatus.PENDING_APPROVAL, trainer, member, Role.TRAINER, Role.MEMBER));

            // when
            LessonResponse response = lessonManager.registrationOrScheduleChangeLessonAccept(member.getId(), savedLesson.getId(), LocalDateTime.of(2024, 10, 29, 3, 22));

            // then
            assertThat(response)
                .extracting(
                    "schedule.date", "schedule.time", "schedule.weekday",
                    "beforeSchedule.date", "beforeSchedule.time", "beforeSchedule.weekday",
                    "status", "registeredBy", "modifiedBy")
                .contains(
                    LocalDate.of(2024, 10, 10), LocalTime.of(15, 0), Day.SAT,
                    LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT,
                    LessonStatus.RESERVED, Role.TRAINER, Role.MEMBER);

            assertThat(applicationEvents.stream(LessonNotificationEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                    assertAll(
                        () -> assertThat(event.getRequester()).isEqualTo(member),
                        () -> assertThat(event.getReceiver()).isEqualTo(trainer),
                        () -> assertThat(event.getMessage()).isEqualTo(createAcceptanceMessage(savedLesson)),
                        () -> assertThat(event.getNotificationType()).isEqualTo(NotificationType.LESSON_REGISTRATION_COMPLETION),
                        () -> assertThat(event.getLesson()).isEqualTo(savedLesson)
                    );
                });
        }

        @DisplayName("트레이너 -> 회원 수업 변경 요청 수락하기 (회원이 수락)")
        @Test
        void registrationOrScheduleChangeLessonAcceptWhenTrainerChange() {
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

            LessonSchedule boforeLessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
            LessonSchedule lessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(15, 0), Day.SAT);
            Lesson savedLesson = lessonRepository.save(createLesson(member, gymTrainer, lessonSchedule, boforeLessonSchedule, LessonStatus.PENDING_APPROVAL, trainer, member, Role.TRAINER, Role.MEMBER));

            // when
            LessonResponse response = lessonManager.registrationOrScheduleChangeLessonAccept(trainer.getId(), savedLesson.getId(), LocalDateTime.of(2024, 10, 29, 3, 22));

            // then
            assertThat(response)
                .extracting(
                    "schedule.date", "schedule.time", "schedule.weekday",
                    "beforeSchedule.date", "beforeSchedule.time", "beforeSchedule.weekday",
                    "status", "registeredBy", "modifiedBy")
                .contains(
                    LocalDate.of(2024, 10, 10), LocalTime.of(15, 0), Day.SAT,
                    LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT,
                    LessonStatus.RESERVED, Role.TRAINER, Role.MEMBER);

            assertThat(applicationEvents.stream(LessonNotificationEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                    assertAll(
                        () -> assertThat(event.getRequester()).isEqualTo(trainer),
                        () -> assertThat(event.getReceiver()).isEqualTo(member),
                        () -> assertThat(event.getMessage()).isEqualTo(createAcceptanceMessage(savedLesson)),
                        () -> assertThat(event.getNotificationType()).isEqualTo(NotificationType.LESSON_REGISTRATION_COMPLETION),
                        () -> assertThat(event.getLesson()).isEqualTo(savedLesson)
                    );
                });
        }

    }

    private LessonSchedule createLessonSchedule(LocalDate date, LocalTime time, Day day) {
        return LessonSchedule.builder()
            .date(date)
            .time(time)
            .weekday(day)
            .build();
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonSchedule beforeSchedule, LessonStatus status, User requester, User receiver, Role registeredBy, Role modifiedBy) {
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
        return Member.builder()
            .name(name)
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
            .role(Role.MEMBER)
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
            .role(Role.TRAINER)
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .build();
    }

}