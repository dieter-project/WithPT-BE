package com.sideproject.withpt.domain;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private int weight;
    private int set;
    private int time;
    @Column(name = "bookmark_yn")
    private String yn;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    @Enumerated(EnumType.STRING)
    private BodyPart part;

    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateDate;
}
