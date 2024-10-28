package com.sideproject.withpt.application.lesson.repository;

import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.LESSON_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.LessonStatus;
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
import java.time.LocalTime;
import java.time.YearMonth;
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
class LessonQueryRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private LessonRepository lessonRepository;

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

        LocalDate requestDate = LocalDate.of(2024, 10, 5);

        // when
        List<Lesson> lessons = lessonRepository.getTrainerLessonScheduleByDate(List.of(gymTrainer1, gymTrainer2), requestDate);

        // then
        assertThat(lessons).hasSize(5)
            .extracting("member.name", "schedule.time", "status", "gymTrainer.gym.name")
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
                createLesson(member, gymTrainer1, lessonSchedule1, null, LessonStatus.RESERVED, trainer1, member, Role.TRAINER, null),
                createLesson(member, gymTrainer1, lessonSchedule2, null, LessonStatus.CANCELED, trainer1, member, Role.TRAINER, null)
            )
        );

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
        LessonSchedule lessonSchedule3 = createLessonSchedule(LocalDate.of(2024, 10, 7), LocalTime.of(12, 0), Day.MON);
        LessonSchedule lessonSchedule4 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(14, 0), Day.THU);
        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer2, lessonSchedule3, null, LessonStatus.RESERVED, trainer2, member, Role.TRAINER, null),
                createLesson(member, gymTrainer2, lessonSchedule4, null, LessonStatus.RESERVED, trainer2, member, Role.TRAINER, null)
            )
        );

        LocalDate date = LocalDate.of(2024, 10, 7);

        // when
        List<Lesson> lessons = lessonRepository.getMemberLessonScheduleByDate(member, date);

        // then
        assertThat(lessons).hasSize(2)
            .extracting("requester.name", "schedule.date", "schedule.time", "status", "gymTrainer.gym.name")
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

    @DisplayName("등록인 + 수업 상태에 따른 수업 목록 조회")
    @Test
    void findAllRegisteredByAndLessonStatus() {
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
                createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule3, LessonStatus.PENDING_APPROVAL, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule6, LessonStatus.PENDING_APPROVAL, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule7, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule8, LessonStatus.RESERVED, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule9, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule10, LessonStatus.PENDING_APPROVAL, Role.MEMBER)
            )
        );

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
        LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
        LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.PENDING_APPROVAL, Role.MEMBER),
                createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.PENDING_APPROVAL, Role.MEMBER)
            )
        );

        final List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Lesson> lessons = lessonRepository.findAllRegisteredByAndLessonStatus(Role.MEMBER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable);

        // then
        assertThat(lessons.getContent()).hasSize(5)
            .extracting("gymTrainer", "schedule")
            .containsExactly(
                tuple(gymTrainer2, lessonSchedule11),
                tuple(gymTrainer1, lessonSchedule3),
                tuple(gymTrainer2, lessonSchedule12),
                tuple(gymTrainer1, lessonSchedule6),
                tuple(gymTrainer1, lessonSchedule10)
            );
    }

    @DisplayName("수정인 + 수업 상태에 따른 수업 목록 조회")
    @Test
    void findAllModifiedByAndLessonStatus() {
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
                createLesson(member, gymTrainer1, lessonSchedule1, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule2, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule3, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED, Role.MEMBER, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED, Role.TRAINER, Role.MEMBER),
                createLesson(member, gymTrainer1, lessonSchedule6, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule7, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule8, LessonStatus.RESERVED, Role.MEMBER, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule9, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member, gymTrainer1, lessonSchedule10, LessonStatus.PENDING_APPROVAL, Role.MEMBER)
            )
        );

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2));
        LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
        LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
        lessonRepository.saveAll(List.of(
                createLesson(member, gymTrainer2, lessonSchedule11, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER),
                createLesson(member, gymTrainer2, lessonSchedule12, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER)
            )
        );

        final List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Lesson> lessons = lessonRepository.findAllModifiedByAndLessonStatus(Role.MEMBER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable);

        // then
        assertThat(lessons.getContent()).hasSize(4)
            .extracting("gymTrainer", "schedule")
            .containsExactly(
                tuple(gymTrainer1, lessonSchedule1),
                tuple(gymTrainer2, lessonSchedule11),
                tuple(gymTrainer1, lessonSchedule2),
                tuple(gymTrainer2, lessonSchedule12)
            );
    }

    @DisplayName("트레이너에 의해 수업 변경이 요청된 목록 조회")
    @Test
    void findAllModifiedByAndLessonStatusByTrainer() {
        // given
        Member member1 = memberRepository.save(createMember("회원"));
        Member member2 = memberRepository.save(createMember("회원"));

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
                createLesson(member1, gymTrainer1, lessonSchedule1, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER),
                createLesson(member1, gymTrainer1, lessonSchedule2, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER),
                createLesson(member1, gymTrainer1, lessonSchedule6, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER),
                createLesson(member1, gymTrainer1, lessonSchedule3, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER),
                createLesson(member1, gymTrainer1, lessonSchedule4, LessonStatus.CANCELED, Role.MEMBER, Role.MEMBER),
                createLesson(member1, gymTrainer1, lessonSchedule5, LessonStatus.RESERVED, Role.TRAINER, Role.MEMBER),
                createLesson(member2, gymTrainer1, lessonSchedule7, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member2, gymTrainer1, lessonSchedule8, LessonStatus.RESERVED, Role.MEMBER, Role.TRAINER),
                createLesson(member2, gymTrainer1, lessonSchedule9, LessonStatus.RESERVED, Role.TRAINER),
                createLesson(member2, gymTrainer1, lessonSchedule10, LessonStatus.PENDING_APPROVAL, Role.MEMBER)
            )
        );

        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer));
        LessonSchedule lessonSchedule11 = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(15, 0), Day.SAT);
        LessonSchedule lessonSchedule12 = createLessonSchedule(LocalDate.of(2024, 10, 10), LocalTime.of(18, 0), Day.SUN);
        lessonRepository.saveAll(List.of(
                createLesson(member1, gymTrainer2, lessonSchedule11, LessonStatus.PENDING_APPROVAL, Role.TRAINER, Role.MEMBER),
                createLesson(member2, gymTrainer2, lessonSchedule12, LessonStatus.PENDING_APPROVAL, Role.MEMBER, Role.TRAINER)
            )
        );

        final List<GymTrainer> gymTrainers = List.of(gymTrainer1, gymTrainer2);
        final Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Lesson> lessons = lessonRepository.findAllModifiedByAndLessonStatus(Role.TRAINER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable);

        // then
        assertThat(lessons.getContent()).hasSize(4)
            .extracting("gymTrainer", "schedule")
            .containsExactly(
                tuple(gymTrainer1, lessonSchedule1),
                tuple(gymTrainer1, lessonSchedule2),
                tuple(gymTrainer2, lessonSchedule12),
                tuple(gymTrainer1, lessonSchedule6)
            );
    }

    @DisplayName("")
    @Test
    void test() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));
        LessonSchedule lessonSchedule = createLessonSchedule(LocalDate.of(2024, 10, 5), LocalTime.of(9, 0), Day.SAT);
        Lesson lesson = lessonRepository.save(
            createLesson(member, gymTrainer, lessonSchedule, null, LessonStatus.RESERVED, member, trainer, Role.TRAINER, null)
        );

        Long userId = member.getId();
        Long lessonId = lesson.getId();

        // when
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        User receiver = lessonRepository.findById(lessonId)
            .map(findLesson -> {
                if (findLesson.getRequester().equals(requester)) {
                    return findLesson.getReceiver();
                }
                return findLesson.getRequester();
            })
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        // then
        assertThat(requester).isEqualTo(member);
        assertThat(receiver).isEqualTo(trainer);
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

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status) {
        return createLesson(member, gymTrainer, schedule, null, status, null, null, null, null);
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status, Role registeredBy) {
        return createLesson(member, gymTrainer, schedule, null, status, null, null, registeredBy, null);
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, LessonSchedule schedule, LessonStatus status, Role registeredBy, Role modifiedBy) {
        return createLesson(member, gymTrainer, schedule, null, status, null, null, registeredBy, modifiedBy);
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