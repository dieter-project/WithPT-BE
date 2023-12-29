package com.sideproject.withpt.application.pt.repository;

import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.personalTraining;
import static com.sideproject.withpt.domain.pt.QPersonalTrainingInfo.personalTrainingInfo;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.controller.response.MemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_GymInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_MemberInfo;
import com.sideproject.withpt.application.pt.controller.response.QMemberDetailInfoResponse_PtInfo;
import com.sideproject.withpt.application.pt.controller.response.QReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.controller.response.ReRegistrationHistoryResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QGymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.ArrayList;
import java.util.List;
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

                )).fetchOne();
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
                    personalTraining.member.id,
                    personalTraining.member.name,
                    personalTraining.id,
                    personalTraining.totalPtCount,
                    personalTraining.remainingPtCount,
                    personalTraining.infoInputStatus,
                    personalTraining.registrationAllowedStatus,
                    personalTraining.registrationRequestDate
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

    public Slice<ReRegistrationHistoryResponse> findRegistrationHistory(Member member, Trainer trainer, Gym gym, Pageable pageable) {

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
                            memberEq(member),
                            trainerEq(trainer),
                            gymEq(gym)
                        )
                )
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(personalTrainingInfo.registrationDate.asc())
            .fetch();

        boolean hasNext = false;

        if(contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
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
