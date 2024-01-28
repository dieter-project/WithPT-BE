package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "personal_training_id")
//    private PersonalTraining personalTraining;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @Embedded
    private LessonSchedule schedule;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="date", column = @Column(name="BEFORE_DATE")),
        @AttributeOverride(name="time", column = @Column(name="BEFORE_TIME")),
        @AttributeOverride(name="weekday", column = @Column(name="BEFORE_DAY"))
    })
    private LessonSchedule beforeSchedule;

    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    private String registeredBy;

    private String modifiedBy;

    public void changeLessonStatus(LessonStatus status) {
        this.status = status;
    }
}
