package com.sideproject.withpt.application.pt.repository;

import static com.sideproject.withpt.domain.gym.QGym.gym;
import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.personalTraining;
import static com.sideproject.withpt.domain.pt.QPersonalTrainingInfo.personalTrainingInfo;
import static com.sideproject.withpt.domain.user.member.QMember.member;
import static com.sideproject.withpt.domain.user.trainer.QTrainer.trainer;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.pt.controller.response.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_GymInfo;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_PtInfo;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_TrainerInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_GymInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_MemberInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_PtInfo;
import com.sideproject.withpt.application.pt.controller.response.QReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.repository.dto.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.MonthlyMemberCount;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QGymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.QMonthlyMemberCount;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto_MemberInfo;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto_PtInfo;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.pt.QPersonalTrainingInfo;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersonalTrainingQueryRepositoryImpl implements PersonalTrainingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GymMemberCountDto> getGymMemberCountBy(List<GymTrainer> gymTrainers, LocalDateTime currentDateTime) {
        return jpaQueryFactory
            .select(
                new QGymMemberCountDto(
                    gym.name,
                    personalTraining.count())
            )
            .from(personalTraining)
            .leftJoin(gymTrainer).on(personalTraining.gymTrainer.eq(gymTrainer))
            .leftJoin(gym).on(gymTrainer.gym.eq(gym))
            .where(gymTrainersIn(gymTrainers),
                personalTraining.registrationAllowedStatus.eq(PtRegistrationAllowedStatus.ALLOWED),
                personalTraining.registrationAllowedDate.loe(currentDateTime))
            .groupBy(gym.name)
            .fetch();
    }

    @Override
    public EachGymMemberListResponse findAllPtMembersByRegistrationAllowedStatusAndDate(GymTrainer gymTrainer, PtRegistrationAllowedStatus allowedStatus, LocalDateTime allowedDate, Pageable pageable) {
        List<PtMemberListDto> ptMemberListDtos = jpaQueryFactory
            .select(
                new QPtMemberListDto(
                    new QPtMemberListDto_MemberInfo(
                        personalTraining.member.id,
                        personalTraining.member.name,
                        new QPtMemberListDto_PtInfo(
                            personalTraining.id,
                            personalTraining.totalPtCount,
                            personalTraining.remainingPtCount,
                            personalTraining.infoInputStatus,
                            personalTraining.registrationAllowedStatus,
                            personalTraining.registrationAllowedDate,
                            personalTraining.registrationRequestDate
                        )
                    )
                )
            )
            .from(personalTraining)
            .leftJoin(personalTraining.member)
            .where(
                gymTrainerEq(gymTrainer),
                registrationAllowedStatusLoe(allowedDate),
                registrationAllowedStatusEq(allowedStatus)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<PtMemberListDto> content = new ArrayList<>(ptMemberListDtos);

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        Long totalMembers = jpaQueryFactory
            .select(personalTraining.count())
            .from(personalTraining)
            .where(
                gymTrainerEq(gymTrainer),
                registrationAllowedStatusLoe(allowedDate),
                registrationAllowedStatusEq(allowedStatus)
            ).fetchOne();

        return EachGymMemberListResponse.builder()
            .totalMembers(totalMembers)
            .memberList(new SliceImpl<>(content, pageable, hasNext))
            .build();
    }

    @Override
    public MemberDetailInfoResponse findPtMemberDetailInfo(PersonalTraining pt) {
        return jpaQueryFactory
            .select(
                new QMemberDetailInfoResponse(
                    new QMemberDetailInfoResponse_MemberInfo(
                        member.id,
                        member.name,
                        member.imageUrl,
                        member.birth,
                        member.sex,
                        member.height,
                        member.weight,
                        member.dietType
                    ),
                    new QMemberDetailInfoResponse_GymInfo(
                        gym.id,
                        gym.name
                    ),
                    new QMemberDetailInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.registrationStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.note,
                        personalTraining.centerFirstRegistrationMonth,
                        personalTraining.centerLastReRegistrationMonth
                    )
                )
            )
            .from(personalTraining)
            .leftJoin(member).on(member.eq(personalTraining.member))
            .leftJoin(gym).on(gym.eq(personalTraining.gymTrainer.gym))
            .where(
                personalTraining.eq(pt)
            )
            .fetchOne();

    }

    @Override
    public Slice<ReRegistrationHistoryResponse> findRegistrationHistory(PersonalTraining pt, Pageable pageable) {

        List<ReRegistrationHistoryResponse> contents = jpaQueryFactory
            .select(
                new QReRegistrationHistoryResponse(
                    personalTrainingInfo.personalTraining.id,
                    personalTrainingInfo.ptCount,
                    personalTrainingInfo.registrationDate,
                    personalTrainingInfo.registrationStatus
                )
            )
            .from(personalTrainingInfo)
            .join(personalTrainingInfo.personalTraining)
            .where(personalTrainingInfo.personalTraining.eq(pt))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(personalTrainingInfo.registrationDate.asc())
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public List<AssignedPTInfoResponse> findPtAssignedTrainerInformation(Member member) {
        return jpaQueryFactory
            .select(
                new QAssignedPTInfoResponse(
                    new QAssignedPTInfoResponse_TrainerInfo(
                        trainer.id,
                        trainer.name,
                        trainer.imageUrl
                    ),
                    new QAssignedPTInfoResponse_GymInfo(
                        gym.id,
                        gym.name
                    ),
                    new QAssignedPTInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.registrationStatus,
                        personalTraining.registrationAllowedStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.centerFirstRegistrationMonth,
                        personalTraining.centerLastReRegistrationMonth
                    )
                )
            )
            .from(personalTraining)
            .join(personalTraining.gymTrainer, gymTrainer).on(personalTraining.member.eq(member))
            .join(trainer).on(gymTrainer.trainer.eq(trainer))
            .join(gym).on(gymTrainer.gym.eq(gym))
            .fetch();
    }

    @Override
    public List<MemberDetailInfoResponse> findAllPTMembersInfoBy(List<GymTrainer> gymTrainers, String name) {
        return jpaQueryFactory
            .select(
                new QMemberDetailInfoResponse(
                    new QMemberDetailInfoResponse_MemberInfo(
                        member.id,
                        member.name,
                        member.imageUrl,
                        member.birth,
                        member.sex,
                        member.height,
                        member.weight,
                        member.dietType
                    ),
                    new QMemberDetailInfoResponse_GymInfo(
                        gym.id,
                        gym.name
                    ),
                    new QMemberDetailInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.registrationStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.note,
                        personalTraining.centerFirstRegistrationMonth,
                        personalTraining.centerLastReRegistrationMonth
                    )
                )
            )
            .from(personalTraining)
            .join(personalTraining.gymTrainer, gymTrainer)
            .join(personalTraining.member, member)
            .join(gym).on(gymTrainer.gym.eq(gym))
            .where(
                personalTraining.gymTrainer.in(gymTrainers),
                memberNameEq(name)
            )
            .orderBy(
                personalTraining.infoInputStatus.asc(),
                member.name.asc(), gym.name.asc())
            .fetch();
    }

    @Override
    public List<MonthlyMemberCount> getPTMemberCountByRegistrationStatus(List<GymTrainer> gymTrainers, YearMonth startDate, YearMonth endDate, PtRegistrationStatus status) {
        DateTemplate<String> localDateDateTemplate = Expressions.dateTemplate(
            String.class,
            "CONCAT(YEAR({0}), '-', LPAD(MONTH({0}), 2, '0'))",
            personalTrainingInfo.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        return jpaQueryFactory
            .select(
                new QMonthlyMemberCount(
                    localDateDateTemplate,
                    personalTrainingInfo.personalTraining.countDistinct()
                )
            ).from(personalTrainingInfo)
            .where(
                localDateDateTemplate.between(startDate.toString(), endDate.toString()),
                personalTrainingInfo.registrationStatus.eq(status),
                personalTrainingInfo.personalTraining.id.in(
                    JPAExpressions
                        .select(personalTraining.id)
                        .from(personalTraining)
                        .where(gymTrainersIn(gymTrainers))
                )
            )
            .groupBy(localDateDateTemplate)
            .orderBy(localDateDateTemplate.desc())
            .fetch();
    }

    @Override
    public Map<String, Long> getExistingMemberCount(List<GymTrainer> gymTrainers, YearMonth startDate, YearMonth endDate) {
        QPersonalTrainingInfo pti = new QPersonalTrainingInfo("pti");
        QPersonalTrainingInfo pti2 = new QPersonalTrainingInfo("pti2");

        DateTemplate<String> registrationDate = Expressions.dateTemplate(
            String.class,
            "CONCAT(YEAR({0}), '-', LPAD(MONTH({0}), 2, '0'))",
            pti.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        DateTemplate<String> registrationDate2 = Expressions.dateTemplate(
            String.class,
            "CONCAT(YEAR({0}), '-', LPAD(MONTH({0}), 2, '0'))",
            pti2.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        Map<String, Long> monthlyMemberCountMap = new LinkedHashMap<>();
        for (YearMonth month = startDate; month.isBefore(endDate) || month.equals(endDate); month = month.plusMonths(1)) {

            List<Long> excludedPersonalTrainingIds = jpaQueryFactory
                .selectDistinct(pti2.personalTraining.id)
                .from(pti2)
                .where(
                    registrationDate2.eq(month.toString()),
                    pti2.personalTraining.id.in(
                        JPAExpressions.select(personalTraining.id)
                            .from(personalTraining)
                            .where(gymTrainersIn(gymTrainers))
                    ))
                .fetch();

            List<Long> existingMembersCount = jpaQueryFactory
                .select(pti.personalTraining.id)
                .from(pti)
                .where(registrationDate.before(month.toString()),
                    pti.personalTraining.id.notIn(excludedPersonalTrainingIds),
                    pti.personalTraining.id.in(
                        JPAExpressions.select(personalTraining.id)
                            .from(personalTraining)
                            .where(gymTrainersIn(gymTrainers))
                    )
                )
                .groupBy(pti.personalTraining.id)
                .fetch();

            monthlyMemberCountMap.put(String.valueOf(month), (long) existingMembersCount.size());
        }

        return monthlyMemberCountMap;
    }

    private BooleanExpression membersIn(List<Member> members) {
        return CollectionUtils.isEmpty(members) ? null : personalTraining.member.in(members);
    }

    private BooleanExpression memberNameEq(String name) {
        return StringUtils.hasText(name) ? personalTraining.member.name.eq(name) : null;
    }

    private BooleanExpression memberEq(Member member) {
        return ObjectUtils.isEmpty(member) ? null : personalTraining.member.eq(member);
    }

    private BooleanExpression gymTrainersIn(List<GymTrainer> gymTrainers) {
        return CollectionUtils.isEmpty(gymTrainers) ? null : personalTraining.gymTrainer.in(gymTrainers);
    }

    private BooleanExpression gymTrainerEq(GymTrainer gymTrainer) {
        return ObjectUtils.isEmpty(gymTrainer) ? null : personalTraining.gymTrainer.eq(gymTrainer);
    }

    private BooleanExpression registrationAllowedStatusEq(PtRegistrationAllowedStatus registrationAllowedStatus) {
        return ObjectUtils.isEmpty(registrationAllowedStatus) ? null
            : personalTraining.registrationAllowedStatus.eq(registrationAllowedStatus);
    }

    private BooleanExpression registrationAllowedStatusLoe(LocalDateTime registrationAllowedDate) {
        return ObjectUtils.isEmpty(registrationAllowedDate) ? null : personalTraining.registrationAllowedDate.loe(registrationAllowedDate);
    }

}
