package com.sideproject.withpt.domain.record.exercise;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "exercise")
    private List<ExerciseInfo> exerciseInfos = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    @Builder
    public Exercise(Member member, List<ExerciseInfo> exerciseInfos, LocalDate uploadDate) {
        this.member = member;
        this.uploadDate = uploadDate;
        addExerciseInfos(exerciseInfos);
    }

    public void addExerciseInfos(List<ExerciseInfo> exerciseInfos) {
        exerciseInfos.forEach(this::addExerciseInfo);
    }

    public void addExerciseInfo(ExerciseInfo exerciseInfo) {
        exerciseInfos.add(exerciseInfo);
        exerciseInfo.setExercise(this);
    }

}
