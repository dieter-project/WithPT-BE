package com.sideproject.withpt.application.pt.repository;

import com.sideproject.withpt.application.pt.controller.response.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse.MonthlyMemberCount;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.repository.dto.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PersonalTrainingQueryRepository {


    List<GymMemberCountDto> getGymMemberCountBy(List<GymTrainer> gymTrainers, LocalDateTime currentDateTime);

    EachGymMemberListResponse findAllPtMembersByRegistrationAllowedStatusAndDate(GymTrainer gymTrainer, PtRegistrationAllowedStatus allowedStatus, LocalDateTime allowedDate, Pageable pageable);

    Long countOfAllPtMembers(Long trainerId);

    long deleteAllByMembersAndTrainerAndGym(List<Member> members, Trainer trainer, Gym gym);

    Long countByGymAndTrainer(Gym gym, Trainer trainer);

    MemberDetailInfoResponse findPtMemberDetailInfo(PersonalTraining pt);

    Slice<ReRegistrationHistoryResponse> findRegistrationHistory(PersonalTraining pt, Pageable pageable);

    List<AssignedPTInfoResponse> findPtAssignedTrainerInformation(Member member);

    List<MemberDetailInfoResponse> findAllPTMembersInfoBy(List<GymTrainer> gymTrainers, String name);

    List<MonthlyMemberCount> calculatePTStatistic(Trainer trainer, LocalDate date);

    Optional<Long> getMemberCountThisMonthByRegistrationStatus(Trainer trainer, LocalDate date, PtRegistrationStatus status);

    Optional<Long> getExistingMemberCount(Trainer trainer, LocalDate date);


}
