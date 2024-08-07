package com.sideproject.withpt.application.pt.repository;

import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.personalTraining;
import static com.sideproject.withpt.domain.pt.QPersonalTrainingInfo.personalTrainingInfo;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.pt.controller.response.AssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.PtStatisticResponse.MonthlyMemberCount;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_GymInfo;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_PtInfo;
import com.sideproject.withpt.application.pt.controller.response.QAssignedPTInfoResponse_TrainerInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_GymInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_MemberInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_PtInfo;
import com.sideproject.withpt.application.pt.controller.response.QPtStatisticResponse_MonthlyMemberCount;
import com.sideproject.withpt.application.pt.controller.response.QReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QGymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto_MemberInfo;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto_PtInfo;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PersonalTrainingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<GymMemberCountDto> findAllPTsPageableByGymAndTrainer(Slice<Gym> gyms, Trainer trainer) {

        return jpaQueryFactory
            .select(
                new QGymMemberCountDto(
                    personalTraining.gym.name,
                    personalTraining.count())
            )
            .from(personalTraining)
            .leftJoin(personalTraining.gym)
            .where(trainerEq(trainer), gymsIn(gyms.getContent()),
                personalTraining.registrationAllowedStatus.eq(PtRegistrationAllowedStatus.APPROVED))
            .groupBy(personalTraining.gym)
            .fetch();
    }

    public Long countOfAllPtMembers(Long trainerId) {
        return jpaQueryFactory
            .select(personalTraining.count())
            .from(personalTraining)
            .where(
                personalTraining.gym.id.in(
                    JPAExpressions
                        .select(gymTrainer.gym.id)
                        .from(gymTrainer)
                        .where(gymTrainer.trainer.id.eq(trainerId))

                ),
                personalTraining.registrationAllowedStatus.eq(PtRegistrationAllowedStatus.APPROVED)
            ).fetchOne();
    }

    public long deleteAllByMembersAndTrainerAndGym(List<Member> members, Trainer trainer, Gym gym) {
        return jpaQueryFactory
            .delete(personalTraining)
            .where(
                membersIn(members),
                trainerEq(trainer),
                gymEq(gym)
            )
            .execute();
    }

    public Long countByGymAndTrainer(Gym gym, Trainer trainer) {
        return
            jpaQueryFactory
                .select(
                    personalTraining.count()
                )
                .from(personalTraining)
                .leftJoin(personalTraining.gym)
                .where(gymEq(gym), trainerEq(trainer))
                .fetchOne();
    }

    public EachGymMemberListResponse findAllPtMembersByRegistrationAllowedStatus(Gym gym, Trainer trainer,
        PtRegistrationAllowedStatus registrationAllowedStatus, Pageable pageable) {
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
                            personalTraining.registrationRequestDate
                        )
                    )
                )
            )
            .from(personalTraining)
            .leftJoin(personalTraining.member)
            .where(
                gymEq(gym),
                trainerEq(trainer),
                registrationAllowedStatusEq(registrationAllowedStatus)
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
                gymEq(gym),
                trainerEq(trainer),
                registrationAllowedStatusEq(registrationAllowedStatus)
            ).fetchOne();

        return EachGymMemberListResponse.builder()
            .totalMembers(totalMembers)
            .memberList(new SliceImpl<>(content, pageable, hasNext))
            .build();
    }

    public MemberDetailInfoResponse findPtMemberDetailInfo(PersonalTraining pt) {
        return jpaQueryFactory
            .select(
                new QMemberDetailInfoResponse(
                    new QMemberDetailInfoResponse_MemberInfo(
                        personalTraining.member.id,
                        personalTraining.member.name,
                        personalTraining.member.imageUrl,
                        personalTraining.member.authentication.birth,
                        personalTraining.member.authentication.sex,
                        personalTraining.member.height,
                        personalTraining.member.weight,
                        personalTraining.member.dietType
                    ),
                    new QMemberDetailInfoResponse_GymInfo(
                        personalTraining.gym.id,
                        personalTraining.gym.name
                    ),
                    new QMemberDetailInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.registrationStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.note,
                        personalTraining.firstRegistrationDate,
                        personalTraining.lastRegistrationDate
                    )
                )
            )
            .from(personalTraining)
            .join(personalTraining.member)
            .join(personalTraining.gym)
            .where(
                personalTraining.eq(pt)
            )
            .fetchOne();
    }

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
            .leftJoin(personalTrainingInfo.personalTraining)
            .where(
                personalTrainingInfo.personalTraining.id.eq(
                    JPAExpressions
                        .select(personalTraining.id)
                        .from(personalTraining)
                        .where(
                            personalTraining.eq(pt)
                        )
                )
            )
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

    public List<AssignedPTInfoResponse> findPtAssignedTrainerInformation(Member member) {
        return jpaQueryFactory
            .select(
                new QAssignedPTInfoResponse(
                    new QAssignedPTInfoResponse_TrainerInfo(
                        personalTraining.trainer.id,
                        personalTraining.trainer.name,
                        personalTraining.trainer.imageUrl
                    ),
                    new QAssignedPTInfoResponse_GymInfo(
                        personalTraining.gym.id,
                        personalTraining.gym.name
                    ),
                    new QAssignedPTInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.registrationAllowedStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.firstRegistrationDate,
                        personalTraining.lastRegistrationDate
                    )
                )
            )
            .from(personalTraining)
            .join(personalTraining.trainer)
            .join(personalTraining.gym)
            .where(
                personalTraining.member.eq(member)
            )
            .fetch();
    }

    public List<MemberDetailInfoResponse> findPtAssignedMemberInformation(Trainer trainer) {
        return jpaQueryFactory
            .select(
                new QMemberDetailInfoResponse(
                    new QMemberDetailInfoResponse_MemberInfo(
                        personalTraining.member.id,
                        personalTraining.member.name,
                        personalTraining.member.imageUrl,
                        personalTraining.member.authentication.birth,
                        personalTraining.member.authentication.sex,
                        personalTraining.member.height,
                        personalTraining.member.weight,
                        personalTraining.member.dietType
                    ),
                    new QMemberDetailInfoResponse_GymInfo(
                        personalTraining.gym.id,
                        personalTraining.gym.name
                    ),
                    new QMemberDetailInfoResponse_PtInfo(
                        personalTraining.id,
                        personalTraining.registrationStatus,
                        personalTraining.infoInputStatus,
                        personalTraining.totalPtCount,
                        personalTraining.remainingPtCount,
                        personalTraining.note,
                        personalTraining.firstRegistrationDate,
                        personalTraining.lastRegistrationDate
                    )
                )
            )
            .from(personalTraining)
            .join(personalTraining.member)
            .join(personalTraining.gym)
            .where(
                personalTraining.trainer.eq(trainer)
            )
            .fetch();
    }

    public List<MonthlyMemberCount> calculatePTStatistic(Trainer trainer, LocalDate date) {

        DateTemplate<String> localDateDateTemplate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            personalTrainingInfo.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        DateTemplate<String> startDate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            date.minusMonths(12),
            ConstantImpl.create("%Y-%m")
        );

        DateTemplate<String> endDate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            date,
            ConstantImpl.create("%Y-%m")
        );

        return jpaQueryFactory
            .select(
                new QPtStatisticResponse_MonthlyMemberCount(
                    localDateDateTemplate,
                    personalTrainingInfo.count()
                )
            ).from(personalTrainingInfo)
            .where(
                localDateDateTemplate.between(startDate, endDate),
                personalTrainingInfo.personalTraining.id.in(
                    JPAExpressions
                        .select(personalTraining.id)
                        .from(personalTraining)
                        .where(
                            personalTraining.trainer.eq(trainer)
                        )
                )
            )
            .groupBy(
                localDateDateTemplate
            ).orderBy(
                localDateDateTemplate.desc()
            ).fetch();
    }

    public Optional<Long> getMemberCountThisMonthByRegistrationStatus(Trainer trainer, LocalDate date, PtRegistrationStatus status) {

        DateTemplate<String> localDateDateTemplate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            personalTrainingInfo.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        DateTemplate<String> endDate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            date,
            ConstantImpl.create("%Y-%m")
        );

        return Optional.ofNullable(
            jpaQueryFactory
                .select(
                    personalTrainingInfo.count()
                ).from(personalTrainingInfo)
                .where(
                    localDateDateTemplate.eq(endDate),
                    personalTrainingInfo.registrationStatus.eq(status),
                    personalTrainingInfo.personalTraining.id.in(
                        JPAExpressions
                            .select(personalTraining.id)
                            .from(personalTraining)
                            .where(
                                personalTraining.trainer.eq(trainer)
                            )
                    )
                ).fetchOne());
    }

    public Optional<Long> getExistingMemberCount(Trainer trainer, LocalDate date) {

        DateTemplate<String> localDateDateTemplate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            personalTrainingInfo.registrationDate,
            ConstantImpl.create("%Y-%m")
        );

        DateTemplate<String> endDate = Expressions.dateTemplate(
            String.class,
            "DATE_FORMAT({0}, {1})",
            date,
            ConstantImpl.create("%Y-%m")
        );

        return Optional.ofNullable(
            jpaQueryFactory
            .select(
                personalTrainingInfo.count()
            ).from(personalTrainingInfo)
            .where(
                localDateDateTemplate.lt(endDate),
                personalTrainingInfo.personalTraining.id.in(
                    JPAExpressions
                        .select(personalTraining.id)
                        .from(personalTraining)
                        .where(
                            personalTraining.trainer.eq(trainer)
                        )
                )
            ).fetchOne()
        );
    }

    private BooleanExpression membersIn(List<Member> members) {
        return CollectionUtils.isEmpty(members) ? null : personalTraining.member.in(members);
    }

    private BooleanExpression memberEq(Member member) {
        return ObjectUtils.isEmpty(member) ? null : personalTraining.member.eq(member);
    }

    private BooleanExpression gymsIn(List<Gym> gyms) {
        return CollectionUtils.isEmpty(gyms) ? null : personalTraining.gym.in(gyms);
    }

    private BooleanExpression gymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : personalTraining.gym.eq(gym);
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : personalTraining.trainer.eq(trainer);
    }

    private BooleanExpression registrationAllowedStatusEq(PtRegistrationAllowedStatus registrationAllowedStatus) {
        return ObjectUtils.isEmpty(registrationAllowedStatus) ? null
            : personalTraining.registrationAllowedStatus.eq(registrationAllowedStatus);
    }

}
