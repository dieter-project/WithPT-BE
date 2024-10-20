package com.sideproject.withpt.application.gym.repositoy;

import static com.sideproject.withpt.domain.gym.QGymTrainer.gymTrainer;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
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

@Slf4j
@Repository
@RequiredArgsConstructor
public class GymQueryRepositoryImpl implements GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Gym> findAllTrainerGymsByPageable(Trainer trainer, Pageable pageable) {

        List<GymTrainer> gymTrainers = jpaQueryFactory.selectFrom(gymTrainer)
            .innerJoin(gymTrainer.gym)
            .fetchJoin()
            .where(trainerEq(trainer))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<Gym> contents = gymTrainers.stream()
            .map(GymTrainer::getGym)
            .collect(Collectors.toList());

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression trainerEq(Trainer trainer) {
        return ObjectUtils.isEmpty(trainer) ? null : gymTrainer.trainer.eq(trainer);
    }
}
