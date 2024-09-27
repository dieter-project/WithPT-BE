package com.sideproject.withpt.application.pt.service;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.response.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse.MonthStatistics;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse.MonthlyMemberCount;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalAndRemainingPtCountResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PTCountLogRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingInfoRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingQueryRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PTCountLog;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.pt.PersonalTrainingInfo;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class PersonalTrainingService {

    private final GymRepository gymRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final GymTrainerRepository gymTrainerRepository;

    private final PersonalTrainingRepository personalTrainingRepository;
    private final PersonalTrainingInfoRepository trainingInfoRepository;
    private final PersonalTrainingQueryRepository trainingQueryRepository;
    private final GymQueryRepository gymQueryRepository;

    private final PTCountLogRepository ptCountLogRepository;

    public Slice<CountOfMembersAndGymsResponse> listOfGymsAndNumberOfMembers(Long trainerId, LocalDate date, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Slice<Gym> gyms = gymQueryRepository.findAllTrainerGymsByPageable(trainer, pageable);

        List<GymMemberCountDto> gymMemberCountDtos = trainingQueryRepository.findAllPersonalTrainingsPageableBy(gyms, trainer);

        List<CountOfMembersAndGymsResponse> contents = gyms.stream()
            .map(gym -> {
                Long memberCount = gymMemberCountDtos.stream()
                    .filter(dto -> gym.getName().equals(dto.getGymName()))
                    .findFirst()
                    .map(GymMemberCountDto::getMemberCount)
                    .orElse(0L);
                return CountOfMembersAndGymsResponse.from(gym, memberCount);
            })
            .collect(Collectors.toList());

        return new SliceImpl<>(contents, pageable, gyms.hasNext());
    }

    @Transactional
    public PersonalTrainingMemberResponse registerPersonalTraining(Long gymId, Long memberId, Long trainerId, LocalDateTime ptRegistrationRequestDate) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        GymTrainer gymTrainer = gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);

        if (personalTrainingRepository.existsByMemberAndGymTrainer(member, gymTrainer)) {
            throw PTException.AlREADY_REGISTERED_PT_MEMBER;
        }

        personalTrainingRepository.save(PersonalTraining.registerNewPersonalTraining(member, gymTrainer, ptRegistrationRequestDate));

        // TODO : PUSH 알림 전송
        return PersonalTrainingMemberResponse.from(member.getName(), trainer.getName(), gym.getName());
    }

    @Transactional
    public void deletePersonalTrainingMembers(List<Long> ptIds) {
        personalTrainingRepository.deleteAllByIdInBatch(ptIds);
    }

    public TotalPtsCountResponse countOfAllPtMembers(Long trainerId) {
        return TotalPtsCountResponse.from(
            trainingQueryRepository.countOfAllPtMembers(trainerId)
        );
    }

    public GymMemberCountDto getGymAndNumberOfMembers(Long trainerId, Long gymId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        return GymMemberCountDto.builder()
            .gymName(gym.getName())
            .memberCount(trainingQueryRepository.countByGymAndTrainer(gym, trainer))
            .build();
    }

    public EachGymMemberListResponse listOfPtMembersByRegistrationAllowedStatus(Long gymId, Long trainerId,
        PtRegistrationAllowedStatus registrationAllowedStatus, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        return trainingQueryRepository.findAllPtMembersByRegistrationAllowedStatus(gym, trainer,
            registrationAllowedStatus, pageable);
    }

    @Transactional
    public void approvedPersonalTrainingRegistration(Long ptId, LocalDateTime registrationAllowedDate) {

        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        // 이미 등록을 허용한 상태면 에러
        if (personalTraining.getRegistrationAllowedStatus() == PtRegistrationAllowedStatus.ALLOWED) {
            throw PTException.AlREADY_ALLOWED_PT_REGISTRATION;
        }

        personalTraining.approvedPersonalTrainingRegistration(registrationAllowedDate);
    }

    public MemberDetailInfoResponse getPtMemberDetailInfo(Long ptId) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return trainingQueryRepository.findPtMemberDetailInfo(personalTraining);
    }

    @Transactional
    public void savePtMemberDetailInfo(Long ptId, SavePtMemberDetailInfoRequest request) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        // 등록 허용되지 않은 회원이면
        if (personalTraining.getRegistrationAllowedStatus() == PtRegistrationAllowedStatus.WAITING) {
            throw PTException.PT_REGISTRATION_NOT_ALLOWED;
        }

        // 이미 초기 입력이 됐으면 에러
        if (personalTraining.getRegistrationStatus() == PtRegistrationStatus.NEW_REGISTRATION) {
            throw PTException.AlREADY_REGISTERED_FIRST_PT_INFO;
        }

        PersonalTraining.saveFirstPtDetailInfo(personalTraining, request.getPtCount(),
            request.getFirstRegistrationDate(), request.getNote());

        trainingInfoRepository.save(
            PersonalTrainingInfo.saveTrainingInfo(
                personalTraining,
                request.getPtCount(),
                request.getFirstRegistrationDate(),
                PtRegistrationStatus.NEW_REGISTRATION
            )
        );

        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                personalTraining,
                request.getPtCount(),
                request.getPtCount(),
                request.getFirstRegistrationDate(), PtRegistrationStatus.NEW_REGISTRATION)
        );
    }

    public TotalAndRemainingPtCountResponse getPtTotalAndRemainingCount(Long ptId) {

        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return TotalAndRemainingPtCountResponse.of(
            ptId,
            personalTraining.getTotalPtCount(),
            personalTraining.getRemainingPtCount()
        );
    }

    @Transactional
    public void updatePtMemberDetailInfo(Long ptId, UpdatePtMemberDetailInfoRequest request) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        int beforeTotalPtCount = personalTraining.getTotalPtCount();
        int beforeRemainingPtCount = personalTraining.getRemainingPtCount();

        PersonalTraining.updatePtDetailInfo(personalTraining, request.getTotalPtCount(), request.getRemainingPtCount(),
            request.getNote());

        // 로그 기록
        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                personalTraining,
                request.getTotalPtCount() - beforeTotalPtCount,
                request.getRemainingPtCount() - beforeRemainingPtCount,
                LocalDateTime.now(),
                PtRegistrationStatus.PT_COUNT_UPDATE
            )
        );
    }

    @Transactional
    public void extendPt(Long ptId, ExtendPtRequest request) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        PersonalTraining.extendPt(personalTraining, request.getPtCount(), request.getPtCount(),
            request.getReRegistrationDate());

        trainingInfoRepository.save(
            PersonalTrainingInfo.saveTrainingInfo(
                personalTraining,
                request.getPtCount(),
                request.getReRegistrationDate(),
                PtRegistrationStatus.RE_REGISTRATION
            )
        );

        // 로그 기록
        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                personalTraining,
                request.getPtCount(),
                request.getPtCount(),
                request.getReRegistrationDate(),
                PtRegistrationStatus.RE_REGISTRATION
            )
        );
    }

    public Slice<ReRegistrationHistoryResponse> getReRegistrationHistory(Long ptId, Pageable pageable) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return trainingQueryRepository.findRegistrationHistory(personalTraining, pageable);
    }

    public List<AssignedPTInfoResponse> getPtAssignedTrainerInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        return trainingQueryRepository.findPtAssignedTrainerInformation(member);
    }

    public List<MemberDetailInfoResponse> getPtAssignedMemberInformation(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        return trainingQueryRepository.findPtAssignedMemberInformation(trainer);
    }

    public PtStatisticResponse getPtStatistics(Long trainerId, LocalDate current, int size) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Map<YearMonth, Long> monthlyCountsMap = trainingQueryRepository.calculatePTStatistic(trainer, current)
            .stream()
            .collect(Collectors.toMap(
                monthlyMemberCount -> YearMonth.parse(monthlyMemberCount.getDate()),
                PtStatisticResponse.MonthlyMemberCount::getCount,
                (existing, replacement) -> replacement,
                LinkedHashMap::new
            ));

        List<MonthlyMemberCount> statistic = IntStream.range(0, size)
            .mapToObj(i -> {
                YearMonth yearMonth = YearMonth.from(current.minusMonths(i));
                long count = monthlyCountsMap.getOrDefault(yearMonth, 0L);
                return new MonthlyMemberCount(yearMonth.toString(), count);
            })
            .collect(Collectors.toList());

        return PtStatisticResponse.of(
            MonthStatistics.builder()
                .currentDate(current)
                .existingMemberCount(
                    trainingQueryRepository.getExistingMemberCount(trainer, current)
                        .orElse(0L)
                )
                .reEnrolledMemberCount(
                    trainingQueryRepository.getMemberCountThisMonthByRegistrationStatus(trainer, current, PtRegistrationStatus.RE_REGISTRATION)
                        .orElse(0L)
                )
                .newMemberCount(
                    trainingQueryRepository.getMemberCountThisMonthByRegistrationStatus(trainer, current, PtRegistrationStatus.NEW_REGISTRATION)
                        .orElse(0L)
                )
                .build(),
            statistic
        );
    }
}
