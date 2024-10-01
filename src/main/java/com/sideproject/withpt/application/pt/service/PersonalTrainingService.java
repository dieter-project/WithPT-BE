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
import com.sideproject.withpt.application.pt.controller.response.GymResponse;
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
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.repository.dto.EachGymMemberListResponse;
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
import org.springframework.http.HttpStatus;
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
    private final GymQueryRepository gymQueryRepository;

    private final PTCountLogRepository ptCountLogRepository;

    public CountOfMembersAndGymsResponse listOfGymsAndNumberOfMembers(Long trainerId, LocalDateTime currentDateTime, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Slice<GymTrainer> gymTrainersByPageable = gymTrainerRepository.findAllPageableByTrainer(trainer, pageable);

        Map<String, Long> gymMemberCountMap = createGymMemberCountMapBy(gymTrainersByPageable, currentDateTime);

        List<Gym> gyms = extractGymsBy(gymTrainersByPageable);

        int totalMemberCount = calculateTotalPTMembersCountBy(gymMemberCountMap);

        List<GymResponse> contents = mappingGymAndMemberCount(gymMemberCountMap, gyms);

        return CountOfMembersAndGymsResponse.from(
            totalMemberCount, currentDateTime.toLocalDate(),
            new SliceImpl<>(contents, pageable, gymTrainersByPageable.hasNext())
        );
    }

    @Transactional
    public PersonalTrainingMemberResponse registerPersonalTraining(Long gymId, Long memberId, Long trainerId, LocalDateTime ptRegistrationRequestDate) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        GymTrainer gymTrainer = getGymTrainerBy(gymId, trainerId);

        if (personalTrainingRepository.existsByMemberAndGymTrainer(member, gymTrainer)) {
            throw PTException.AlREADY_REGISTERED_PT_MEMBER;
        }

        personalTrainingRepository.save(PersonalTraining.registerNewPersonalTraining(member, gymTrainer, ptRegistrationRequestDate));

        // TODO : PUSH 알림 전송
        return PersonalTrainingMemberResponse.from(member.getName(), gymTrainer.getTrainer().getName(), gymTrainer.getGym().getName());
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

    public EachGymMemberListResponse listOfPtMembersByRegistrationAllowedStatus(Long gymId, Long trainerId, PtRegistrationAllowedStatus allowedStatus, LocalDateTime allowedDate, Pageable pageable) {
        GymTrainer gymTrainer = getGymTrainerBy(gymId, trainerId);
        return personalTrainingRepository.findAllPtMembersByRegistrationAllowedStatusAndDate(gymTrainer, allowedStatus, allowedDate, pageable);
    }

    @Transactional
    public void deletePersonalTrainingMembers(List<Long> ptIds, PtRegistrationAllowedStatus status) {

        if (status == PtRegistrationAllowedStatus.ALLOWED) {
            ptIds.forEach(this::checkRemainingPtCount);
        }

        personalTrainingRepository.deleteAllByIdInBatch(ptIds);
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

        personalTraining.saveFirstPtDetailInfo(request.getPtCount(), request.getcenterFirstRegistrationMonth(), request.getNote());

        trainingInfoRepository.save(
            PersonalTrainingInfo.createPTInfo(request.getPtCount(), request.getcenterFirstRegistrationMonth(), PtRegistrationStatus.NEW_REGISTRATION, personalTraining)
        );

        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(request.getPtCount(), request.getPtCount(), request.getcenterFirstRegistrationMonth(), PtRegistrationStatus.NEW_REGISTRATION, personalTraining
            )
        );
    }

    public MemberDetailInfoResponse getPtMemberDetailInfo(Long ptId) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return personalTrainingRepository.findPtMemberDetailInfo(personalTraining);
    }

    @Transactional
    public void extendPtCount(Long ptId, ExtendPtRequest request) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        if (request.getReRegistrationDate().isBefore(personalTraining.getCenterFirstRegistrationMonth())
            || request.getReRegistrationDate().isEqual(personalTraining.getCenterFirstRegistrationMonth())) {
            throw PTException.INVALID_RE_REGISTRATION_DATE;
        }

        personalTraining.extendPtCount(personalTraining, request.getPtCount(), request.getPtCount(),
            request.getReRegistrationDate());

        trainingInfoRepository.save(
            PersonalTrainingInfo.createPTInfo(request.getPtCount(), request.getReRegistrationDate(), PtRegistrationStatus.RE_REGISTRATION, personalTraining)
        );

        // 로그 기록
        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                request.getPtCount(),
                request.getPtCount(),
                request.getReRegistrationDate(),
                PtRegistrationStatus.RE_REGISTRATION,
                personalTraining
            )
        );
    }

    public TotalPtsCountResponse countOfAllPtMembers(Long trainerId) {
        return TotalPtsCountResponse.from(
            personalTrainingRepository.countOfAllPtMembers(trainerId)
        );
    }

    public GymMemberCountDto getGymAndNumberOfMembers(Long trainerId, Long gymId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);

        return GymMemberCountDto.builder()
            .gymName(gym.getName())
            .memberCount(personalTrainingRepository.countByGymAndTrainer(gym, trainer))
            .build();
    }

    @Transactional
    public void updatePtMemberDetailInfo(Long ptId, UpdatePtMemberDetailInfoRequest request) {

        if (request.getRemainingPtCount() > request.getTotalPtCount()) {
            throw PTException.REMAINING_PT_CANNOT_EXCEED_THE_TOTAL_PT_NUMBER;
        }

        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        personalTraining.updatePtDetailInfo(personalTraining, request.getTotalPtCount(), request.getRemainingPtCount(),
            request.getNote());

        int beforeTotalPtCount = personalTraining.getTotalPtCount();
        int beforeRemainingPtCount = personalTraining.getRemainingPtCount();

        // 로그 기록
        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                request.getTotalPtCount() - beforeTotalPtCount,
                request.getRemainingPtCount() - beforeRemainingPtCount,
                LocalDateTime.now(),
                PtRegistrationStatus.PT_COUNT_UPDATE,
                personalTraining
            )
        );
    }

    public Slice<ReRegistrationHistoryResponse> getReRegistrationHistory(Long ptId, Pageable pageable) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return personalTrainingRepository.findRegistrationHistory(personalTraining, pageable);
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

    public List<AssignedPTInfoResponse> getPtAssignedTrainerInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        return personalTrainingRepository.findPtAssignedTrainerInformation(member);
    }

    public List<MemberDetailInfoResponse> getPtAssignedMembersInformation(Long trainerId, Long gymId, String name) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Gym gym = gymRepository.findById(gymId)
            .orElse(null);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllTrainerAndOptionalGym(trainer, gym);

        return personalTrainingRepository.findAllPTMembersInfoBy(gymTrainers, name);
    }

    public PtStatisticResponse getPtStatistics(Long trainerId, LocalDate current, int size) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Map<YearMonth, Long> monthlyCountsMap = personalTrainingRepository.calculatePTStatistic(trainer, current)
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
                    personalTrainingRepository.getExistingMemberCount(trainer, current)
                        .orElse(0L)
                )
                .reEnrolledMemberCount(
                    personalTrainingRepository.getMemberCountThisMonthByRegistrationStatus(trainer, current, PtRegistrationStatus.RE_REGISTRATION)
                        .orElse(0L)
                )
                .newMemberCount(
                    personalTrainingRepository.getMemberCountThisMonthByRegistrationStatus(trainer, current, PtRegistrationStatus.NEW_REGISTRATION)
                        .orElse(0L)
                )
                .build(),
            statistic
        );
    }

    private Map<String, Long> createGymMemberCountMapBy(Slice<GymTrainer> gymTrainersByPageable, LocalDateTime currentDateTime) {
        List<GymMemberCountDto> gymMemberCount = personalTrainingRepository.getGymMemberCountBy(gymTrainersByPageable.getContent(), currentDateTime);
        return gymMemberCount.stream()
            .collect(Collectors.toMap(
                GymMemberCountDto::getGymName,
                GymMemberCountDto::getMemberCount
            ));
    }

    private List<Gym> extractGymsBy(Slice<GymTrainer> gymTrainersByPageable) {
        return gymTrainersByPageable.stream()
            .map(GymTrainer::getGym)
            .collect(Collectors.toList());
    }

    private int calculateTotalPTMembersCountBy(Map<String, Long> gymMemberCountMap) {
        return gymMemberCountMap.values().stream()
            .mapToInt(Long::intValue)
            .sum();
    }

    private List<GymResponse> mappingGymAndMemberCount(Map<String, Long> gymMemberCountMap, List<Gym> gyms) {
        return gyms.stream()
            .map(gym -> {
                Long memberCount = 0L;
                if (gymMemberCountMap.containsKey(gym.getName())) {
                    memberCount = gymMemberCountMap.get(gym.getName());
                }
                return GymResponse.from(gym, memberCount);
            })
            .collect(Collectors.toList());
    }

    private GymTrainer getGymTrainerBy(Long gymId, Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Gym gym = gymRepository.findById(gymId)
            .orElseThrow(() -> GymException.GYM_NOT_FOUND);
        return gymTrainerRepository.findByTrainerAndGym(trainer, gym)
            .orElseThrow(() -> GymTrainerException.GYM_TRAINER_NOT_MAPPING);
    }

    private void checkRemainingPtCount(Long id) {
        PersonalTraining personalTraining = personalTrainingRepository.findById(id)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        if (personalTraining.getRemainingPtCount() > 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, id + "번 PT는 잔여 PT 횟수가 남아 있습니다. 정말 해제하시겠습니까?");
        }
    }
}
