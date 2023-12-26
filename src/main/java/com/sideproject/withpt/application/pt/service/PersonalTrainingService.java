package com.sideproject.withpt.application.pt.service;

import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.pt.controller.request.AcceptPtRegistrationRequest;
import com.sideproject.withpt.application.pt.controller.request.ExtendPtRequest;
import com.sideproject.withpt.application.pt.controller.request.SavePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.request.UpdatePtMemberDetailInfoRequest;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalAndRemainingPtCountResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PTCountLogRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingInfoRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingQueryRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PTCountLog;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.pt.PersonalTrainingInfo;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

    private final GymService gymService;
    private final MemberService memberService;
    private final TrainerService trainerService;

    private final PersonalTrainingRepository trainingRepository;
    private final PersonalTrainingInfoRepository trainingInfoRepository;
    private final PersonalTrainingQueryRepository trainingQueryRepository;
    private final GymQueryRepository gymQueryRepository;

    private final PTCountLogRepository ptCountLogRepository;

    @Transactional
    public PersonalTrainingMemberResponse registerPersonalTrainingMember(Long gymId, Long memberId, Long trainerId) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        // TODO : 이미 등록된 회원입니다 예외 추가
        if (trainingRepository.existsByMemberAndTrainerAndGym(member, trainer, gym)) {
            throw PTException.AlREADY_REGISTERED_PT_MEMBER;
        }

        trainingRepository.save(PersonalTraining.registerPersonalTraining(member, trainer, gym));

        // TODO : PUSH 알림 전송
        return PersonalTrainingMemberResponse.from(member.getName(), trainer.getName(), gym.getName());
    }

    @Transactional
    public long deletePersonalTrainingMembers(Long gymId, Long trainerId, List<Long> memberIds) {
        Gym gym = gymService.getGymById(gymId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        List<Member> members = memberService.getAllMemberById(memberIds);

        return trainingQueryRepository.deleteAllByMembersAndTrainerAndGym(members, trainer, gym);
    }

    public Slice<CountOfMembersAndGymsResponse> listOfGymsAndNumberOfMembers(Long trainerId, Pageable pageable) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Slice<Gym> gyms = gymQueryRepository.findAllTrainerGymsByPageable(trainer, pageable);
        List<GymMemberCountDto> gymMemberCountDtos = trainingQueryRepository.findAllPTsPageableByGymAndTrainer(gyms, trainer);

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

    public TotalPtsCountResponse countOfAllPtMembers(Long trainerId) {
        return TotalPtsCountResponse.from(
             trainingQueryRepository.countOfAllPtMembers(trainerId)
        );
    }

    public GymMemberCountDto getGymAndNumberOfMembers(Long trainerId, Long gymId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        return GymMemberCountDto.builder()
            .gymName(gym.getName())
            .memberCount(trainingQueryRepository.countByGymAndTrainer(gym, trainer))
            .build();
    }

    public EachGymMemberListResponse listOfPtMembersByRegistrationAllowedStatus(Long gymId, Long trainerId,
        PtRegistrationAllowedStatus registrationAllowedStatus, Pageable pageable) {
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        return trainingQueryRepository.findAllPtMembersByRegistrationAllowedStatus(gym, trainer,
            registrationAllowedStatus, pageable);
    }

    @Transactional
    public void allowPtRegistrationNotification(AcceptPtRegistrationRequest request) {
        Member member = memberService.getMemberById(request.getMemberId());
        Trainer trainer = trainerService.getTrainerById(request.getTrainerId());
        Gym gym = gymService.getGymById(request.getGymId());

        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        // 이미 등록을 허용하면 에러
        if (personalTraining.getRegistrationAllowedStatus() == PtRegistrationAllowedStatus.APPROVED) {
            throw PTException.AlREADY_ALLOWED_PT_REGISTRATION;
        }

        PersonalTraining.allowPTRegistration(personalTraining);
    }

    public MemberDetailInfoResponse getPtMemberDetailInfo(Long memberId, Long trainerId, Long gymId) {
        return trainingQueryRepository.findPtMemberDetailInfo(memberId, trainerId, gymId);
    }

    @Transactional
    public void savePtMemberDetailInfo(Long memberId, Long trainerId, Long gymId, SavePtMemberDetailInfoRequest request) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        // 이미 초기 입력이 됐으면 에러
        if (personalTraining.getRegistrationStatus() == PtRegistrationStatus.NEW_REGISTRATION) {
            throw PTException.AlREADY_REGISTERED_FIRST_PT_INFO;
        }

        PersonalTraining.saveFirstPtDetailInfo(personalTraining, request.getPtCount(), request.getFirstRegistrationDate(), request.getNote());

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
                member, trainer, gym,
                request.getPtCount(),
                request.getPtCount(),
                request.getFirstRegistrationDate(), PtRegistrationStatus.NEW_REGISTRATION)
        );
    }

    public TotalAndRemainingPtCountResponse getPtTotalAndRemainingCount(Long memberId, Long trainerId, Long gymId) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        return TotalAndRemainingPtCountResponse.of(
            member.getId(),
            gym.getId(),
            personalTraining.getTotalPtCount(),
            personalTraining.getRemainingPtCount()
        );
    }

    @Transactional
    public void updatePtMemberDetailInfo(Long memberId, Long trainerId, Long gymId, UpdatePtMemberDetailInfoRequest request) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        int beforeTotalPtCount = personalTraining.getTotalPtCount();
        int beforeRemainingPtCount = personalTraining.getRemainingPtCount();

        PersonalTraining.updatePtDetailInfo(personalTraining, request.getTotalPtCount(), request.getRemainingPtCount(), request.getNote());

        // 로그 기록
        ptCountLogRepository.save(
            PTCountLog.recordPTCountLog(
                member, trainer, gym,
                request.getTotalPtCount() - beforeTotalPtCount,
                request.getRemainingPtCount() - beforeRemainingPtCount,
                LocalDateTime.now(),
                PtRegistrationStatus.PT_COUNT_UPDATE
            )
        );
    }

    @Transactional
    public void extendPt(Long memberId, Long trainerId, Long gymId, ExtendPtRequest request) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

        PersonalTraining personalTraining = trainingRepository.findByMemberAndTrainerAndGym(member, trainer, gym)
            .orElseThrow(() -> PTException.PT_NOT_FOUND);

        int beforeTotalPtCount = personalTraining.getTotalPtCount();
        int beforeRemainingPtCount = personalTraining.getRemainingPtCount();

        PersonalTraining.extendPt(personalTraining, request.getPtCount(), request.getPtCount(), request.getReRegistrationDate());

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
                member, trainer, gym,
                request.getPtCount(),
                request.getPtCount(),
                request.getReRegistrationDate(),
                PtRegistrationStatus.RE_REGISTRATION
            )
        );
    }

    public Slice<ReRegistrationHistoryResponse> getReRegistrationHistory(Long memberId, Long trainerId, Long gymId, Pageable pageable) {
        Member member = memberService.getMemberById(memberId);
        Trainer trainer = trainerService.getTrainerById(trainerId);
        Gym gym = gymService.getGymById(gymId);

         return trainingQueryRepository.findRegistrationHistory(member, trainer, gym, pageable);
    }
}
