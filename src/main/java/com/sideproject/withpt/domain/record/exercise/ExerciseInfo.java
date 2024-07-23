package com.sideproject.withpt.domain.record.exercise;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

    @ElementCollection(targetClass = BodyPart.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "exercise_info_body_parts", joinColumns = @JoinColumn(name = "exercise_info_id"))
    @Column(name = "body_part", nullable = false)
    private List<BodyPart> bodyParts = new ArrayList<>();

    private int weight; // 무게(kg)
    private int exerciseSet; // 운동 set
    private int times; // 횟수

    private int exerciseTime;

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void update(ExerciseRequest request) {
//        this.title = request.getTitle();
//        this.weight = request.getWeight();
//        this.exerciseSet = request.getExerciseSet();
//        this.times = request.getTimes();
//        this.exerciseTime = request.getExerciseTime();
//        this.exerciseType = request.getExerciseType();
//        this.bodyParts = request.getBodyPart();
    }

}
