package com.sideproject.withpt.domain.record.exercise;

import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.BaseEntity;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private String title;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BodyCategory bodyCategory;

    private int weight; // 무게(kg)
    private int exerciseSet; // 운동 set
    private int times; // 횟수

    private int exerciseTime;

    @Builder
    private ExerciseInfo(String title, ExerciseType exerciseType, BodyCategory bodyCategory, int weight, int exerciseSet, int times, int exerciseTime) {
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyCategory = bodyCategory;
        this.weight = weight;
        this.exerciseSet = exerciseSet;
        this.times = times;
        this.exerciseTime = exerciseTime;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void update(String title, ExerciseType exerciseType, BodyCategory bodyCategory, int weight, int exerciseSet, int times, int exerciseTime) {
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyCategory = bodyCategory;
        this.weight = weight;
        this.exerciseSet = exerciseSet;
        this.times = times;
        this.exerciseTime = exerciseTime;
    }

}
