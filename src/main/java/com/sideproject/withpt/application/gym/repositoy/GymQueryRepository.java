package com.sideproject.withpt.application.gym.repositoy;

import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;
import static com.sideproject.withpt.domain.pt.QPersonalTraining.personalTraining;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.lesson.controller.response.QSearchMemberResponse;
import com.sideproject.withpt.application.lesson.controller.response.SearchMemberResponse;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.gym.QGymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<GymTrainer> findAllGymsByTrainer(Trainer trainer) {
        QGymTrainer qGymTrainer = gymTrainer;

        return jpaQueryFactory.selectFrom(qGymTrainer)
            .innerJoin(qGymTrainer.gym)
            .fetchJoin()
            .where(trainerEq(trainer))
            .fetch();
    }

    public Slice<Gym> findAllTrainerGymsByPageable(Trainer trainer, Pageable pageable) {

        List<GymTrainer> gymTrainers = jpaQueryFactory.selectFrom(gymTrainer)
            .innerJoin(gymTrainer.gym)
            .fetchJoin()
            .where(trainerEq(trainer))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<Gym> contents = gymTrainers.stream().map(GymTrainer::getGym).collect(Collectors.toList());

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    public Slice<SearchMemberResponse> findAllMembersByGymIdAndName(Trainer trainer, Gym gym, String name,
        Pageable pageable) {

        NumberExpression<Integer> remainingPtCountRank = new CaseBuilder()
            .when(personalTraining.remainingPtCount.gt(0)).then(1)
            .otherwise(2);

        List<SearchMemberResponse> content = jpaQueryFactory
            .select(
                new QSearchMemberResponse(
                    personalTraining.member.id,
                    personalTraining.member.name,
                    personalTraining.infoInputStatus,
                    personalTraining.member.authentication.birth,
                    personalTraining.member.authentication.sex,
                    personalTraining.remainingPtCount
                )
            )
            .from(personalTraining)
            .leftJoin(personalTraining.member)
            .where(
                trainerEq(trainer),
                gymEq(gym),
                personalTraining.registrationAllowedStatus.eq(PtRegistrationAllowedStatus.APPROVED),
                memberNameContains(name)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(
                remainingPtCountRank.asc(),
                personalTraining.member.name.asc()
            )
            .fetch();

        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression memberNameContains(String name) {
        return StringUtils.hasText(name) ? personalTraining.member.name.contains(name) : null;
    }

    private BooleanExpression gymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : personalTraining.gym.eq(gym);
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : gymTrainer.trainer.eq(trainer);
    }
}
