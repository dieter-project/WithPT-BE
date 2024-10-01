package com.sideproject.withpt.application.gymtrainer.repository;

import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GymTrainerQueryRepositoryImpl implements GymTrainerQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<GymTrainer> findAllPageableByTrainer(Trainer trainer, Pageable pageable) {
        List<GymTrainer> contents = jpaQueryFactory
            .selectFrom(gymTrainer)
            .where(trainerEq(trainer))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public List<GymTrainer> findAllTrainerAndOptionalGym(Trainer trainer, Gym gym) {
        return jpaQueryFactory
            .selectFrom(gymTrainer)
            .where(
                trainerEq(trainer),
                GymEq(gym)
            )
            .fetch();
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : gymTrainer.trainer.eq(trainer);
    }

    private BooleanExpression GymEq(Gym gym) {
        return ObjectUtils.isEmpty(gym) ? null : gymTrainer.gym.eq(gym);
    }
}
