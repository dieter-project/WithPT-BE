package com.sideproject.withpt.application.lesson.service;

import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.LESSON_NOT_FOUND;
import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse.LessonTime;
import com.sideproject.withpt.application.lesson.controller.response.PendingLessonInfo;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.lesson.service.response.MemberLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.service.response.TrainerLessonScheduleResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.ScheduleRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonRequestStatus;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.Lesson;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import com.sideproject.withpt.domain.trainer.WorkSchedule;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final GymRepository gymRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    private final GymTrainerRepository gymTrainerRepository;
    private final ScheduleRepository scheduleRepository;
    private final PersonalTrainingRepository personalTrainingRepository;

    private final LessonRepository lessonRepository;

    @Transactional
    public LessonResponse registrationPTLesson(Long gymId, Role requestByRole, LessonRegistrationRequest request) {
        log.info("=================== 수업 등록 =======================\n");
        Member member = getMemberBasedOnRole(request, requestByRole);
        Trainer trainer = getTrainerBasedOnRole(request, requestByRole);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        validationPersonalTraining(member, gymTrainer);

        validationLessonTime(gymTrainer, request.getDate(), request.getTime());

        // TODO TEST 작성 수업 등록 - 예약 시스템이므로 동시성 고려하기
        // TODO : 회원이 수업 등록 요청을 했을 경우 "대기 중 수업 - 받은 요청" 에 표시
        // TODO : 알림 기능 추가
        Lesson lesson = lessonRepository.save(
            Lesson.createNewLessonRegistration(member, gymTrainer,
                request.getDate(), request.getTime(), request.getWeekday(), requestByRole, request.getRegistrationRequestId(), request.getRegistrationReceiverId())
        );

        return LessonResponse.of(lesson);
    }

    public TrainerLessonInfoResponse getLessonSchedule(Long lessonId) {
        return lessonRepository.findLessonScheduleInfoBy(lessonId);
    }

    @Transactional
    public LessonResponse changePTLesson(Long lessonId, Role requestByRole, LessonChangeRequest request) {

        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        validationLessonTime(lesson.getGymTrainer(), request.getDate(), request.getTime());

        if (LessonStatus.isScheduleChangeNotAllowed(lesson.getStatus())) {
            throw LessonException.NON_BOOKED_SESSION;
        }

        // TODO 예약 시스템이므로 동시성 고려하기
        // TODO : 알림 기능 추가
        lesson.changeLessonSchedule(request.getDate(), request.getTime(), request.getWeekday(), requestByRole);

        return LessonResponse.of(lesson);
    }

    @Transactional
    public LessonResponse cancelLesson(Long lessonId, LessonStatus status) {
        // TODO 알림 기능
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> LessonException.LESSON_NOT_FOUND);

        if (LessonStatus.isScheduleChangeNotAllowed(lesson.getStatus())) {
            throw LessonException.NON_CANCEL_SESSION;
        }

        lesson.cancel(status);

        return LessonResponse.of(lesson);
    }

    public AvailableLessonScheduleResponse getTrainerAvailableLessonSchedule(Long gymId, Long trainerId, Day weekday, LocalDate date) {
        GymTrainer gymTrainer = getGymTrainerBy(gymId, trainerId);

        List<LocalTime> times = findBookedLessonTimesBy(date, gymTrainer);

        List<LessonTime> lessonTimes = generateLessonTimes(weekday, gymTrainer, times);

        return AvailableLessonScheduleResponse.of(trainerId, gymId, date, weekday, lessonTimes);
    }

    /*
     * 트레이너가 모든 수업일정 조회 -> trainer != null, gym == null, member == null
     * 트레이너가 A 체육관 수업일정 조회 -> trainer != null, gym != null, member == null
     */
    public TrainerLessonScheduleResponse getTrainerLessonScheduleByDate(Long trainerId, Long gymId, LocalDate date) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElse(null);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllTrainerAndGym(trainer, gym);

        return new TrainerLessonScheduleResponse(
            lessonRepository.getTrainerLessonScheduleByDate(gymTrainers, date)
        );
    }

    /*
     trainer == null, 체육관 == null -> 회원이 조회
     */
    public MemberLessonScheduleResponse getMemberLessonScheduleByDate(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return new MemberLessonScheduleResponse(
            lessonRepository.getMemberLessonScheduleByDate(member, date)
        );

    }

    public Map<LessonRequestStatus, Map<LessonRequestStatus, List<PendingLessonInfo>>> getPendingLessons(Long trainerId) {
        List<Lesson> allByTrainerId = lessonRepository.findAllByTrainerIdAndStatus(trainerId, LessonStatus.PENDING_APPROVAL);
        allByTrainerId.forEach(System.out::println);
        System.out.println("===================\n");

        Map<LessonRequestStatus, Map<LessonRequestStatus, List<PendingLessonInfo>>> collect = allByTrainerId.stream()
            .collect(
                groupingBy(LessonService::groupByRequestStatus,
                    groupingBy(LessonService::groupByRegistrationStatus,
                        mapping(lesson ->
                                PendingLessonInfo.from(lesson, lesson.getMember(), lesson.getGym()),
                            toList())
                    )
                )
            );

        return collect;
    }

    private static LessonRequestStatus groupByRegistrationStatus(Lesson lesson) {
        if (lesson.getModifiedBy() == null) {
            return LessonRequestStatus.REGISTRATION;
        } else {
            return LessonRequestStatus.CHANGE;
        }
    }

    private static LessonRequestStatus groupByRequestStatus(Lesson lesson) {
        if (lesson.getRegisteredBy().equals("MEMBER") || lesson.getModifiedBy().equals("MEMBER")) {
            return LessonRequestStatus.RECEIVED;
        } else {
            return LessonRequestStatus.SENT;
        }
    }

    public List<LocalDate> getLessonScheduleOfMonth(Long trainerId, Long gymId, YearMonth date) {
        return lessonRepository.getLessonScheduleOfMonth(trainerId, gymId, date);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        lessonRepository.findById(lessonId)
            .ifPresentOrElse(
                lessonRepository::delete,
                () -> {
                    throw new LessonException(LESSON_NOT_FOUND);
                }
            );
    }

    private Member getMemberBasedOnRole(LessonRegistrationRequest request, Role loginRole) {
        if (loginRole == Role.TRAINER) {
            return memberRepository.findById(request.getRegistrationReceiverId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        } else {
            return memberRepository.findById(request.getRegistrationRequestId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        }
    }

    private Trainer getTrainerBasedOnRole(LessonRegistrationRequest request, Role loginRole) {
        if (loginRole == Role.TRAINER) {
            return trainerRepository.findById(request.getRegistrationRequestId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        } else {
            return trainerRepository.findById(request.getRegistrationReceiverId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        }
    }

    private void validationPersonalTraining(Member member, GymTrainer gymTrainer) {
        personalTrainingRepository.findByMemberAndGymTrainer(member, gymTrainer)
            .ifPresentOrElse(this::validationPersonalTraining,
                () -> {
                    throw PTException.PT_NOT_FOUND;
                });
    }

    private void validationPersonalTraining(PersonalTraining personalTraining) {
        // PT 등록 허용 요청 시 아직 수락하기 전이면 수업 등록 X
        if (personalTraining.getRegistrationAllowedStatus() == PtRegistrationAllowedStatus.WAITING) {
            throw PTException.PT_REGISTRATION_NOT_ALLOWED;
        }
        // 트레이너 측에서 회원에 대한 PT 상세 정보를 입럭하지 않았으면 오류
        if (personalTraining.getInfoInputStatus() == PTInfoInputStatus.INFO_EMPTY) {
            throw PTException.MISSING_PT_DETAILS_INFO;
        }
        // 잔여 PT 횟수가 0 이하이면 수업 등록 X
        if (personalTraining.getRemainingPtCount() <= 0) {
            throw PTException.NO_REMAINING_PT;
        }
    }

    private void validationLessonTime(GymTrainer gymTrainer, LocalDate date, LocalTime time) {
        lessonRepository.findByGymTrainerAndDateAndTime(gymTrainer, date, time)
            .ifPresent(lesson -> {
                if (LessonStatus.isReserved(lesson.getStatus()) || LessonStatus.isPendingApproval(lesson.getStatus())) {
                    throw LessonException.ALREADY_RESERVATION;
                }
            });
    }


    private GymTrainer getGymTrainerBy(Long gymId, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        return gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);
    }

    private List<LocalTime> findBookedLessonTimesBy(LocalDate date, GymTrainer gymTrainer) {
        return lessonRepository.getBookedLessonBy(gymTrainer, date).stream()
            .map(lesson -> lesson.getSchedule().getTime())
            .collect(toList());
    }

    private List<LessonTime> generateLessonTimes(Day weekday, GymTrainer gymTrainer, List<LocalTime> times) {
        WorkSchedule workSchedule = scheduleRepository.findByGymTrainerAndWeekday(gymTrainer, weekday)
            .orElseThrow(() -> new ScheduleException(WORK_SCHEDULE_NOT_FOUND));

        LocalTime startTime = workSchedule.getInTime();
        LocalTime endTime = workSchedule.getOutTime();
        Duration interval = Duration.ofHours(1);

        List<LessonTime> lessonTimes = new ArrayList<>();
        while (startTime.isBefore(endTime)) {
            lessonTimes.add(
                LessonTime.of(startTime, times.contains(startTime))
            );
            startTime = startTime.plus(interval);
        }
        return lessonTimes;
    }
}
