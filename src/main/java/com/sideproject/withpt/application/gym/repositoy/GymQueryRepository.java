package com.sideproject.withpt.application.gym.repositoy;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.gym.QGymTrainer;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GymQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<GymTrainer> findAllGymsByTrainer(Trainer trainer) {
        QGymTrainer qGymTrainer = QGymTrainer.gymTrainer;

        return jpaQueryFactory.selectFrom(qGymTrainer)
            .innerJoin(qGymTrainer.gym)
            .fetchJoin()
            .where(qGymTrainer.trainer.eq(trainer))
            .fetch();
    }
}
