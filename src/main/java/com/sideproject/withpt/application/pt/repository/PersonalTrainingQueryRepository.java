package com.sideproject.withpt.application.pt.repository;

import com.sideproject.withpt.application.pt.repository.model.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.repository.model.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.repository.model.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.repository.model.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.model.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.model.MonthlyMemberCount;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PersonalTrainingQueryRepository {


    List<GymMemberCountDto> getGymMemberCountBy(List<GymTrainer> gymTrainers, LocalDateTime currentDateTime);

    EachGymMemberListResponse findAllPtMembersByRegistrationAllowedStatusAndDate(GymTrainer gymTrainer, PtRegistrationAllowedStatus allowedStatus, LocalDateTime allowedDate, Pageable pageable);

    MemberDetailInfoResponse findPtMemberDetailInfo(PersonalTraining pt);

    Slice<ReRegistrationHistoryResponse> findRegistrationHistory(PersonalTraining pt, Pageable pageable);

    List<AssignedPTInfoResponse> findPtAssignedTrainerInformation(Member member);

    List<MemberDetailInfoResponse> findAllPTMembersInfoBy(List<GymTrainer> gymTrainers, String name);

    List<MonthlyMemberCount> getPTMemberCountByRegistrationStatus(List<GymTrainer> gymTrainers, YearMonth startDate, YearMonth endDate, PtRegistrationStatus status);

    Map<String, Long> getExistingMemberCount(List<GymTrainer> gymTrainers, YearMonth startDate, YearMonth endDate);


}
