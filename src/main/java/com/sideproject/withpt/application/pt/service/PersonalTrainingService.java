package com.sideproject.withpt.application.pt.service;

import static com.sideproject.withpt.application.pt.exception.PtConstants.MAX_QUERY_MONTHS;

import com.sideproject.withpt.application.gym.exception.GymException;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.exception.GymTrainerException;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.exception.PTErrorCode;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PTCountLogRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingInfoRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.repository.model.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.repository.model.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.model.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.model.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.repository.model.MonthlyMemberCount;
import com.sideproject.withpt.application.pt.repository.model.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.service.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.service.response.GymAndMemberCountResponse;
import com.sideproject.withpt.application.pt.service.response.MonthlyStatisticsResponse;
import com.sideproject.withpt.application.pt.service.response.MonthlyStatisticsResponse.MonthStatistic;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PTCountLog;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.pt.PersonalTrainingInfo;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private final PTCountLogRepository ptCountLogRepository;

    public CountOfMembersAndGymsResponse listOfGymsAndNumberOfMembers(Long trainerId, LocalDateTime currentDateTime, Pageable pageable) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Slice<GymTrainer> gymTrainersByPageable = gymTrainerRepository.findAllPageableByTrainer(trainer, pageable);

        Map<String, Long> gymMemberCountMap = createGymMemberCountMapBy(gymTrainersByPageable, currentDateTime);

        List<Gym> gyms = extractGymsBy(gymTrainersByPageable);

        int totalMemberCount = calculateTotalPTMembersCountBy(gymMemberCountMap);

        List<GymAndMemberCountResponse> contents = mappingGymAndMemberCount(gymMemberCountMap, gyms);

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

    @Transactional
    public void updatePtMemberDetailInfo(Long ptId, UpdatePtMemberDetailInfoRequest request) {

        if (request.getRemainingPtCount() > request.getTotalPtCount()) {
            throw PTException.REMAINING_PT_CANNOT_EXCEED_THE_TOTAL_PT_NUMBER;
        }

        PersonalTraining personalTraining = personalTrainingRepository.findById(ptId)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        personalTraining.updatePtDetailInfo(personalTraining, request.getTotalPtCount(), request.getRemainingPtCount(), request.getNote());

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

    public List<AssignedPTInfoResponse> getPtAssignedTrainerInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        return personalTrainingRepository.findPtAssignedTrainerInformation(member);
    }

    public List<MemberDetailInfoResponse> searchPtMembersInformation(Long trainerId, Long gymId, String name) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Gym gym = gymRepository.findById(gymId)
            .orElse(null);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllTrainerAndGym(trainer, gym);

        return personalTrainingRepository.findAllPTMembersInfoBy(gymTrainers, name);
    }

    public MonthlyStatisticsResponse getPtStatistics(Long trainerId, LocalDate date, int size) {
        if (size > MAX_QUERY_MONTHS) {
            throw new PTException(PTErrorCode.MAX_QUERY_MONTHS);
        }

        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<GymTrainer> gymTrainers = gymTrainerRepository.findAllByTrainer(trainer);

        LocalDate startLocalDate = date.minusMonths(size - 1);
        YearMonth startDate = YearMonth.of(startLocalDate.getYear(), startLocalDate.getMonth());
        YearMonth endDate = YearMonth.of(date.getYear(), date.getMonth());

        Map<String, Long> newRegistrationPTMemberCountMap = getPtMemberCountMap(gymTrainers, startDate, endDate, PtRegistrationStatus.NEW_REGISTRATION);
        Map<String, Long> reRegistrationPTMemberCountMap = getPtMemberCountMap(gymTrainers, startDate, endDate, PtRegistrationStatus.RE_REGISTRATION);
        Map<String, Long> existingMemberCountMap = personalTrainingRepository.getExistingMemberCount(gymTrainers, startDate, endDate);

        return MonthlyStatisticsResponse.of(
            mergeMonthStatistics(startDate, endDate, newRegistrationPTMemberCountMap, reRegistrationPTMemberCountMap, existingMemberCountMap)
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

    private List<GymAndMemberCountResponse> mappingGymAndMemberCount(Map<String, Long> gymMemberCountMap, List<Gym> gyms) {
        return gyms.stream()
            .map(gym -> {
                Long memberCount = 0L;
                if (gymMemberCountMap.containsKey(gym.getName())) {
                    memberCount = gymMemberCountMap.get(gym.getName());
                }
                return GymAndMemberCountResponse.from(gym, memberCount);
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

    private Map<String, Long> getPtMemberCountMap(List<GymTrainer> gymTrainers, YearMonth startDate, YearMonth endDate, PtRegistrationStatus ptRegistrationStatus) {
        List<MonthlyMemberCount> newRegistrationPTMembers = personalTrainingRepository.getPTMemberCountByRegistrationStatus(gymTrainers, startDate, endDate, ptRegistrationStatus);
        return fillMissingMonths(newRegistrationPTMembers, startDate, endDate);
    }

    public Map<String, Long> fillMissingMonths(List<MonthlyMemberCount> memberCounts, YearMonth startDate, YearMonth endDate) {

        Map<String, Long> originalMap = memberCounts.stream()
            .collect(Collectors.toMap(
                MonthlyMemberCount::getDate,
                MonthlyMemberCount::getCount
            ));

        LinkedHashMap<String, Long> resultMap = new LinkedHashMap<>();

        YearMonth current = startDate;
        while (!current.isAfter(endDate)) {
            String yearMonthString = current.toString();
            resultMap.put(yearMonthString, originalMap.getOrDefault(yearMonthString, 0L));
            current = current.plusMonths(1);
        }

        return resultMap;
    }

    private static List<MonthStatistic> mergeMonthStatistics(YearMonth startDate, YearMonth endDate, Map<String, Long> newRegistrationPTMemberCountMap, Map<String, Long> reRegistrationPTMemberCountMap, Map<String, Long> existingMemberCountMap) {
        List<MonthStatistic> result = new ArrayList<>();
        for (YearMonth month = endDate; month.isAfter(startDate) || month.equals(startDate); month = month.minusMonths(1)) {
            String key = month.toString();

            Long existingMemberCount = existingMemberCountMap.get(key);
            Long reMemberCount = reRegistrationPTMemberCountMap.get(key);
            Long newMembersCount = newRegistrationPTMemberCountMap.get(key);

            result.add(
                MonthStatistic.builder()
                    .date(month)
                    .existingMemberCount(existingMemberCount)
                    .reEnrolledMemberCount(reMemberCount)
                    .newMemberCount(newMembersCount)
                    .build());
        }
        return result;
    }
}
