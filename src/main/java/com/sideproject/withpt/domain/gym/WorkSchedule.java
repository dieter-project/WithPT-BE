package com.sideproject.withpt.domain.gym;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_trainer_id")
    private GymTrainer gymTrainer;

    @Enumerated(EnumType.STRING)
    private Day day;

    @Column(name = "IN_TIME", columnDefinition = "TIME")
    private LocalTime inTime;

    @Column(name = "OUT_TIME", columnDefinition = "TIME")
    private LocalTime outTime;

    public void addGymTrainer(GymTrainer gymTrainer) {
        this.gymTrainer = gymTrainer;
    }
}
