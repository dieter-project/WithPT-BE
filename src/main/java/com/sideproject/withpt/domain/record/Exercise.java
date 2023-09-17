package com.sideproject.withpt.domain.record;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime exerciseDate;
    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;
    private String bookmarkYn;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    public void update(ExerciseRequest request) {
        this.exerciseDate = request.getExerciseDate();
        this.title = request.getTitle();
        this.weight = request.getWeight();
        this.set = request.getSet();
        this.times = request.getTimes();
        this.hour = request.getHour();
        this.bookmarkYn = request.getBookmarkYn();
        this.exerciseType = request.getExerciseType();
        this.bodyPart = request.getBodyPart();
    }

}
