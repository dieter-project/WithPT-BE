package com.sideproject.withpt.application.lesson.service;

import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.controller.response.AvailableLessonScheduleResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersInGymResponse;
import com.sideproject.withpt.application.lesson.controller.response.LessonMembersInGymResponse.LessonMember;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonQueryRepository;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingQueryRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final GymService gymService;
    private final MemberService memberService;
    private final TrainerService trainerService;

    private final PersonalTrainingRepository trainingRepository;
    private final PersonalTrainingQueryRepository trainingQueryRepository;
    private final GymQueryRepository gymQueryRepository;

    private final LessonQueryRepository lessonQueryRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public void registrationPtLesson(Long gymId, Long loginId, String loginRole, LessonRegistrationRequest request) {

        Member member = null;
        Trainer trainer = null;

        if(loginRole.contains(Role.TRAINER.name())) {
            trainer = trainerService.getTrainerById(loginId);
            member = memberService.getMemberById(request.getRegistrationRequestId());
        } else {
            member = memberService.getMemberById(loginId);
            trainer = trainerService.getTrainerById(request.getRegistrationRequestId());
        }

        Gym gym = gymService.getGymById(gymId);

        // 해당 PT 정보가 있는지 조회
        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        // PT 등록 허용 요청 시 아직 수락하기 전이면 수업 등록 X
        if(personalTraining.getRegistrationAllowedStatus() == PtRegistrationAllowedStatus.WAITING) {
            throw PTException.PT_REGISTRATION_NOT_ALLOWED;
        }
        // 트레이너 측에서 회원에 대한 PT 상세 정보를 입럭하지 않았으면 오류
        if(personalTraining.getInfoInputStatus() == PTInfoInputStatus.INFO_EMPTY) {
            throw PTException.MISSING_PT_DETAILS_INFO;
        }
        // 잔여 PT 횟수가 0 이하이면 수업 등록 X
        if(personalTraining.getRemainingPtCount() <= 0) {
            throw PTException.NO_REMAINING_PT;
        }

        // 트레이너의 예약 신청 - 날짜, 시간, 상태(예약)가 이미 등록되어 있으면 에러
        if(lessonQueryRepository.existsLessonByTrainer(trainer, request.getDate(), request.getTime(), LessonStatus.RESERVATION)) {
            throw LessonException.ALREADY_RESERVATION;
        }

        // TODO 수업 등록 - 예약 시스템이므로 동시성 고려하기
        lessonRepository.save(request.toEntity(personalTraining));
    }

    public Slice<SearchMemberResponse> searchMembersByGymIdAndName(Long gymId, Long trainerId, String name, Pageable pageable) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        // TODO : 예외 처리 추가
        return lessonQueryRepository.findAllMembersByGymIdAndName(trainer, gym, name, pageable);
    }


    public AvailableLessonScheduleResponse getTrainerWorkSchedule(Long gymId, Long trainerId, Day weekday, LocalDate date) {
        Gym gym = gymService.getGymById(gymId);

        return AvailableLessonScheduleResponse.of(
            trainerId, gymId, date, weekday,
            lessonQueryRepository.getAvailableTrainerLessonSchedule(trainerId, gym, weekday, date)
        );
    }

    public LessonMembersInGymResponse getLessonScheduleMembersInGym(Long trainerId, Long gymId, LocalDate date, Pageable pageable) {
        Gym gym = gymService.getGymById(gymId);

        return LessonMembersInGymResponse.of(
            gym,
            date,
            lessonQueryRepository.getLessonMembersReservationTotalCount(trainerId, gym, date),
            lessonQueryRepository.getLessonScheduleMembersInGym(trainerId, gym, date, pageable)
        );
    }
}
