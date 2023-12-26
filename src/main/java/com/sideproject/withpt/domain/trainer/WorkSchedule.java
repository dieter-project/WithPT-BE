package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.Gym;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter(AccessLevel.PACKAGE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DAY\"")
    private Day weekday;

    @Column(name = "IN_TIME", columnDefinition = "TIME")
    private LocalTime inTime;

    @Column(name = "OUT_TIME", columnDefinition = "TIME")
    private LocalTime outTime;

    public static WorkSchedule createWorkSchedule(Gym gym, WorkSchedule workSchedule) {
        workSchedule.setGym(gym);
        return workSchedule;
    }
}
