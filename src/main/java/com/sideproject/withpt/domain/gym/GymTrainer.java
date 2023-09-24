package com.sideproject.withpt.domain.gym;

import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GymTrainer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private LocalDate hireDate;

    private LocalDate retirementDate;

    public void addTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public static GymTrainer createGymTrainer(Gym gym) {
        return GymTrainer.builder()
            .gym(gym)
            .hireDate(LocalDate.now())
            .build();
    }
}
