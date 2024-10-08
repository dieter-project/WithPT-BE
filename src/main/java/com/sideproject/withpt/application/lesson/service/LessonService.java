package com.sideproject.withpt.application.lesson.service;

import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.LESSON_NOT_FOUND;
import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.ONLY_CANCELLED_OR_AUTO_CANCELLED;
import static com.sideproject.withpt.application.schedule.exception.ScheduleErrorCode.WORK_SCHEDULE_NOT_FOUND;
import static java.util.stream.Collectors.toList;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse.LessonTime;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.repository.dto.TrainerLessonInfoResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.lesson.service.response.LessonScheduleOfMonthResponse;
import com.sideproject.withpt.application.lesson.service.response.MemberLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.service.response.TrainerLessonScheduleResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.schedule.exception.ScheduleException;
import com.sideproject.withpt.application.schedule.repository.ScheduleRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.LessonRequestStatus;
import com.sideproject.withpt.common.type.LessonStatus;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.Role;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    /*
    트레이너 - 월(Month) 전체 체육관 수업 일정 달력 조회
     */
    public LessonScheduleOfMonthResponse getTrainerLessonScheduleOfMonth(Long trainerId, Long gymId, YearMonth yearMonth) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElse(null);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllTrainerAndGym(trainer, gym);

        return new LessonScheduleOfMonthResponse(
            extractGymNames(gymTrainers),
            lessonRepository.getTrainerLessonScheduleOfMonth(gymTrainers, yearMonth)
        );
    }

    /*
       회원 - 월(Month) 전체 체육관 수업 일정 달력 조회
    */
    public LessonScheduleOfMonthResponse getMemberLessonScheduleOfMonth(Long memberId, Long gymId, YearMonth yearMonth) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<GymTrainer> gymTrainers = filteringGymTrainerBy(gymId, member);

        return new LessonScheduleOfMonthResponse(
            extractGymNames(gymTrainers),
            lessonRepository.getMemberLessonScheduleOfMonth(gymTrainers, member, yearMonth)
        );
    }

    public Map<LessonRequestStatus, Slice<LessonResponse>> getReceivedLessonRequests(Long trainerId, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllByTrainer(trainer);

        Slice<Lesson> registrationRequestedLessons = lessonRepository.findAllRegisteredByAndLessonStatus(Role.MEMBER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable);
        Slice<Lesson> changeRequestedLessons = lessonRepository.findAllModifiedByAndLessonStatus(Role.MEMBER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable);

        return Map.of(
            LessonRequestStatus.REGISTRATION, convertToLessonResponses(registrationRequestedLessons),
            LessonRequestStatus.CHANGE, convertToLessonResponses(changeRequestedLessons)
        );
    }

    public Slice<LessonResponse> getSentLessonRequests(Long trainerId, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllByTrainer(trainer);

        return convertToLessonResponses(
            lessonRepository.findAllModifiedByAndLessonStatus(Role.TRAINER, LessonStatus.PENDING_APPROVAL, gymTrainers, pageable)
        );
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        lessonRepository.findById(lessonId)
            .ifPresentOrElse(
                lesson -> {
                    if (!LessonStatus.isCanceled(lesson)) {
                        throw new LessonException(ONLY_CANCELLED_OR_AUTO_CANCELLED);
                    }
                    lessonRepository.delete(lesson);
                },
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

    private List<GymTrainer> getAssignedGymTrainerBy(Member member) {
        return personalTrainingRepository.findPtAssignedTrainerInformation(member).stream()
            .map(assignedPTInfoResponse -> {
                Long trainerId = assignedPTInfoResponse.getTrainer().getId();
                Long gymId = assignedPTInfoResponse.getGym().getId();
                return getGymTrainerBy(gymId, trainerId);
            })
            .collect(toList());
    }

    private List<GymTrainer> filteringGymTrainerBy(Long gymId, Member member) {
        List<GymTrainer> gymTrainers = getAssignedGymTrainerBy(member);

        return gymTrainers.stream()
            .filter(gymTrainer -> gymTrainer.getGym().getId().equals(gymId))
            .findFirst()  // 첫 번째로 매칭된 GymTrainer 반환
            .map(List::of)  // GymTrainer를 리스트로 감쌈
            .orElse(gymTrainers);  // 매칭되는 값이 없을 경우 원래 리스트 반환
    }

    private List<String> extractGymNames(List<GymTrainer> gymTrainers) {
        return gymTrainers.stream()
            .map(gymTrainer -> gymTrainer.getGym().getName())
            .collect(toList());
    }

    private Slice<LessonResponse> convertToLessonResponses(Slice<Lesson> lessons) {
        List<LessonResponse> contents = lessons.getContent().stream()
            .map(LessonResponse::of)
            .collect(toList());
        return new SliceImpl<>(contents, lessons.getPageable(), lessons.hasNext());
    }
}
