package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.GymTrainer;
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
import lombok.ToString;

@Entity
@Builder
@Setter(AccessLevel.PACKAGE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"trainer", "gym"})
public class WorkSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_trainer_id")
    private GymTrainer gymTrainer;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"DAY\"")
    private Day weekday;

    @Column(name = "IN_TIME", columnDefinition = "TIME")
    private LocalTime inTime;

    @Column(name = "OUT_TIME", columnDefinition = "TIME")
    private LocalTime outTime;

    public void editWorkScheduleTime(LocalTime inTime, LocalTime outTime) {
        this.inTime = inTime;
        this.outTime = outTime;
    }
}
