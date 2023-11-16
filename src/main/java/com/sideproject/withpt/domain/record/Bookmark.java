package com.sideproject.withpt.domain.record;

import com.sideproject.withpt.application.exercise.dto.request.BookmarkRequest;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    public void update(BookmarkRequest request) {
        this.weight = request.getWeight();
        this.set = request.getSet();
        this.times = request.getTimes();
        this.hour = request.getHour();
        this.exerciseType = request.getExerciseType();
        this.bodyPart = request.getBodyPart();
        this.title = request.getTitle();
    }

}
