package com.sideproject.withpt.domain.record.bookmark;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkRequest;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import javax.persistence.CascadeType;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BookmarkBodyCategory bodyCategory;

    private int weight; // 무게(kg)
    private int exerciseSet; // 운동 set
    private int times; // 횟수

    private int exerciseTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    @Builder
    private Bookmark(Member member, String title, ExerciseType exerciseType, BookmarkBodyCategory bodyCategory, int weight, int exerciseSet, int times, int exerciseTime, LocalDate uploadDate) {
        this.member = member;
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyCategory = bodyCategory;
        this.weight = weight;
        this.exerciseSet = exerciseSet;
        this.times = times;
        this.exerciseTime = exerciseTime;
        this.uploadDate = uploadDate;
    }

    public void update(BookmarkRequest request) {
//        this.weight = request.getWeight();
//        this.exerciseSet = request.getExerciseSet();
//        this.times = request.getTimes();
//        this.exerciseTime = request.getHour();
//        this.exerciseType = request.getExerciseType();
//        this.bodyPart = request.getBodyPart();
//        this.title = request.getTitle();
    }

}
