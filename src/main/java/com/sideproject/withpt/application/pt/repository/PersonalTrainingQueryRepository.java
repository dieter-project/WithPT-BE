package com.sideproject.withpt.application.pt.repository;

import static com.sideproject.withpt.domain.pt.QPersonalTraining.personalTraining;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.pt.controller.response.EachGymMemberListResponse;
import com.sideproject.withpt.application.pt.repository.dto.GymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.PtMemberListDto;
import com.sideproject.withpt.application.pt.repository.dto.QGymMemberCountDto;
import com.sideproject.withpt.application.pt.repository.dto.QPtMemberListDto;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
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

    public List<GymMemberCountDto> findAllPTsByGymAndTrainer(List<Gym> gyms, Trainer trainer) {

        return jpaQueryFactory
            .select(
                new QGymMemberCountDto(
                    personalTraining.gym.name,
                    personalTraining.count())
            )
            .from(personalTraining)
            .leftJoin(personalTraining.gym)
            .where(gymsIn(gyms), trainerEq(trainer))
            .groupBy(personalTraining.gym)
            .fetch();
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

    public EachGymMemberListResponse findAllPtMembersByRegistrationAllowedStatus(Gym gym, Trainer trainer, PtRegistrationAllowedStatus registrationAllowedStatus, Pageable pageable) {
        List<PtMemberListDto> ptMemberListDtos = jpaQueryFactory
            .select(
                new QPtMemberListDto(
                    personalTraining.member.id,
                    personalTraining.member.name,
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

        if(content.size() > pageable.getPageSize()) {
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

    private BooleanExpression membersIn(List<Member> members) {
        return CollectionUtils.isEmpty(members) ? null : personalTraining.member.in(members);
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
        return ObjectUtils.isEmpty(registrationAllowedStatus) ? null : personalTraining.registrationAllowedStatus.eq(registrationAllowedStatus);
    }
}
