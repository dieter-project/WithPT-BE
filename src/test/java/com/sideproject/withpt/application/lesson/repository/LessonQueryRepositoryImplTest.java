package com.sideproject.withpt.application.lesson.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.repository.dto.MemberLessonInfoResponse;
import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
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
import java.time.YearMonth;
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
class LessonQueryRepositoryImplTest {

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

    @DisplayName("등록/취소 수업 스케줄 조회")
    @Test
    void findLessonScheduleInfoBy() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        LessonSchedule lessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 4), LocalTime.of(21, 35), Day.FRI);

        Role registrationRequestByRole = Role.TRAINER;
        String requester = Lesson.getRequester(trainer.getId(), registrationRequestByRole);
        String receiver = Lesson.getReceiver(member.getId(), registrationRequestByRole);

        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.RESERVED, requester, receiver, registrationRequestByRole, null)
        );

        // when
        TrainerLessonInfoResponse response = lessonRepository.findLessonScheduleInfoBy(lesson.getId());

        // then
        assertThat(response.getLesson())
            .extracting("schedule.date", "schedule.time", "beforeSchedule",
                "status", "requester", "receiver", "registeredBy", "modifiedBy")
            .contains(
                LocalDate.of(2024, 10, 4), LocalTime.of(21, 35), null,
                LessonStatus.RESERVED, requester, receiver, Role.TRAINER, null);

        assertThat(response.getMember().getName()).isEqualTo("회원");
        assertThat(response.getGym().getName()).isEqualTo("체육관");
    }

    @DisplayName("요청 날짜에 예약된 수업 목록 조회")
    @Test
    void getBookedLessonBy() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        LocalDate date = LocalDate.of(2024, 10, 4);

        LessonSchedule lessonSchedule1 = createLessonSchedule(date, LocalTime.of(12, 0), Day.FRI);
        LessonSchedule lessonSchedule2 = createLessonSchedule(date, LocalTime.of(15, 10), Day.FRI);
        LessonSchedule lessonSchedule3 = createLessonSchedule(date, LocalTime.of(18, 0), Day.FRI);
        LessonSchedule lessonSchedule4 = createLessonSchedule(date, LocalTime.of(21, 35), Day.FRI);
        LessonSchedule lessonSchedule5 = createLessonSchedule(date, LocalTime.of(21, 35), Day.FRI);
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

        // when
        List<Lesson> bookedLesson = lessonRepository.getBookedLessonBy(gymTrainer, date);

        // then
        assertThat(bookedLesson).hasSize(4)
            .extracting("schedule.time")
            .contains(
                LocalTime.of(12, 0),
                LocalTime.of(15, 10),
                LocalTime.of(18, 0),
                LocalTime.of(21, 35)
            );
    }

    @DisplayName("요청 날짜에 예약된 트레이너의 수업 목록 조회 - 체육관 필터X")
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

        // when
        List<TrainerLessonInfoResponse> responses = lessonRepository.getTrainerLessonScheduleByDate(List.of(gymTrainer1, gymTrainer2), LocalDate.of(2024, 10, 5));

        // then
        assertThat(responses).hasSize(5)
            .extracting("member.name", "lesson.schedule.time", "lesson.status", "gym.name")
            .contains(
                tuple("회원1", LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                tuple("회원2", LocalTime.of(11, 0), LessonStatus.RESERVED, "체육관1"),
                tuple("회원3", LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관1"),
                tuple("회원4", LocalTime.of(14, 0), LessonStatus.CANCELED, "체육관1"),
                tuple("회원2", LocalTime.of(15, 0), LessonStatus.RESERVED, "체육관2")
            );
    }

    @DisplayName("요청 날짜에 예약된 회원의 수업 목록 조회")
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

        LocalDate date = LocalDate.of(2024, 10, 7);

        // when
        List<MemberLessonInfoResponse> responses = lessonRepository.getMemberLessonScheduleByDate(member, date);

        // then
        assertThat(responses).hasSize(2)
            .extracting("trainer.name", "lesson.schedule.date", "lesson.schedule.time", "lesson.status", "gym.name")
            .contains(
                tuple("트레이너1", LocalDate.of(2024, 10, 7), LocalTime.of(9, 0), LessonStatus.RESERVED, "체육관1"),
                tuple("트레이너2", LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), LessonStatus.RESERVED, "체육관2")
            );
    }

    @DisplayName("트레이너 - 월(Month) 전체 체육관 수업 일정 달력 조회")
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
        LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 6), LocalTime.of(18, 0), Day.SUN);
        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.RESERVED),
                createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.RESERVED)
            )
        );

        final YearMonth yearMonth = YearMonth.of(2024, 10);
        final List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);

        // when
        List<LocalDate> result = lessonRepository.getTrainerLessonScheduleOfMonth(gymTrainers, yearMonth);

        // then
        assertThat(result).hasSize(8)
            .containsExactly(
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 6),
                LocalDate.of(2024, 10, 7),
                LocalDate.of(2024, 10, 8),
                LocalDate.of(2024, 10, 11),
                LocalDate.of(2024, 10, 15),
                LocalDate.of(2024, 10, 21),
                LocalDate.of(2024, 10, 24)
            );
    }

    @DisplayName("회원 - 월(Month) 전체 체육관 수업 일정 달력 조회")
    @Test
    void getMemberLessonScheduleOfMonth() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        Trainer trainer1 = trainerRepository.save(createTrainer("트레이너1"));
        Trainer trainer2 = trainerRepository.save(createTrainer("트레이너2"));
        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Gym gym2 = gymRepository.save(createGym("체육관2"));

        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer1));
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
                createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.COMPLETION),
                createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.COMPLETION),
                createLesson(member, gymTrainer1, lessonSchedule3, LessonStatus.RESERVED),
                createLesson(member, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED),
                createLesson(member, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED),
                createLesson(member, gymTrainer1, lessonSchedule6, LessonStatus.RESERVED),
                createLesson(member, gymTrainer1, lessonSchedule7, LessonStatus.TIME_OUT_CANCELED),
                createLesson(member, gymTrainer1, lessonSchedule8, LessonStatus.RESERVED),
                createLesson(member, gymTrainer1, lessonSchedule9, LessonStatus.PENDING_APPROVAL),
                createLesson(member, gymTrainer1, lessonSchedule10, LessonStatus.RESERVED)
            )
        );

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
        LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
        LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.RESERVED),
                createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.RESERVED)
            )
        );

        final YearMonth yearMonth = YearMonth.of(2024, 10);
        final List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);

        // when
        List<LocalDate> result = lessonRepository.getMemberLessonScheduleOfMonth(gymTrainers, member, yearMonth);

        // then
        assertThat(result).hasSize(7)
            .containsExactly(
                LocalDate.of(2024, 10, 5),
                LocalDate.of(2024, 10, 6),
                LocalDate.of(2024, 10, 7),
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 11),
                LocalDate.of(2024, 10, 15),
                LocalDate.of(2024, 10, 24)
            );
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
}