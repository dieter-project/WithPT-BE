package com.sideproject.withpt.application.pt.service;

import com.sideproject.withpt.application.gym.repositoy.GymQueryRepository;
import com.sideproject.withpt.application.gym.service.GymService;
import com.sideproject.withpt.application.member.service.MemberService;
import com.sideproject.withpt.application.pt.controller.response.CountOfMembersAndGymsResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.pt.controller.response.TotalPtsCountResponse;
import com.sideproject.withpt.application.pt.exception.PTException;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingQueryRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
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
    private final PersonalTrainingQueryRepository trainingQueryRepository;
    private final GymQueryRepository gymQueryRepository;


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

}
