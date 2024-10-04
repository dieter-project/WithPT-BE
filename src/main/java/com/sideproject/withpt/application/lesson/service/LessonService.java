package com.sideproject.withpt.application.lesson.service;

import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.LESSON_NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonInfo;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersResponse;
import com.sideproject.withpt.application.lesson.controller.response.PendingLessonInfo;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonRegistrationResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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

    private final GymService gymService;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    private final GymTrainerRepository gymTrainerRepository;
    private final PersonalTrainingRepository personalTrainingRepository;

    private final LessonRepository lessonRepository;

    @Transactional
    public LessonRegistrationResponse registrationPTLesson(Long gymId, String loginRole, LessonRegistrationRequest request) {
        log.info("=================== 수업 등록 =======================\n");
        Member member = getMemberBasedOnRole(request, loginRole);
        Trainer trainer = getTrainerBasedOnRole(request, loginRole);
        Gym gym = gymService.getGymById(gymId);

        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        validationPersonalTraining(member, gymTrainer);

        validationLessonTime(gymTrainer, request.getDate(), request.getTime());

        // TODO TEST 작성 수업 등록 - 예약 시스템이므로 동시성 고려하기
        Lesson lesson = lessonRepository.save(
            Lesson.createNewLessonRegistration(member, gymTrainer, request.getDate(), request.getTime(), request.getWeekday(), loginRole)
        );

        return LessonRegistrationResponse.of(lesson, request.getRegistrationRequestId(), request.getRegistrationReceiverId());
    }

    public AvailableLessonScheduleResponse getTrainerWorkSchedule(Long gymId, Long trainerId, Day weekday, LocalDate date) {
        Gym gym = gymService.getGymById(gymId);

        return AvailableLessonScheduleResponse.of(
            trainerId, gymId, date, weekday,
            lessonRepository.getAvailableTrainerLessonSchedule(trainerId, gym, weekday, date)
        );
    }

    public LessonMembersResponse getLessonScheduleMembers(Long trainerId, Long gymId, LocalDate date, LessonStatus status) {
        return new LessonMembersResponse(
            lessonRepository.getLessonScheduleMembers(trainerId, gymId, date, status)
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

    public LessonInfo getLessonSchedule(Long lessonId) {
        return lessonRepository.getLessonSchedule(lessonId);
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

    @Transactional
    public void changeLessonStatus(Long lessonId, LessonStatus status) {
        lessonRepository.findById(lessonId)
            .ifPresentOrElse(lesson ->
                    lesson.changeLessonStatus(status),
                () -> {
                    throw new LessonException(LESSON_NOT_FOUND);
                }
            );
    }

    @Transactional
    public void changePtLesson(Long lessonId, String loginRole, LessonChangeRequest request) {

        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        validationLessonTime(lesson.getGymTrainer(), request.getDate(), request.getTime());

        lesson.changeLessonSchedule(request.getDate(), request.getTime(), request.getWeekday(), loginRole);
    }

    private Member getMemberBasedOnRole(LessonRegistrationRequest request, String loginRole) {
        if (loginRole.equals(Role.TRAINER.name())) {
            return memberRepository.findById(request.getRegistrationReceiverId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        } else {
            return memberRepository.findById(request.getRegistrationRequestId())
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        }
    }

    private Trainer getTrainerBasedOnRole(LessonRegistrationRequest request, String loginRole) {
        if (loginRole.equals(Role.TRAINER.name())) {
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
}
