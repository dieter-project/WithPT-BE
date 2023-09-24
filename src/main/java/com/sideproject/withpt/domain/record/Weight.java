package com.sideproject.withpt.domain.record;

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
public class Weight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private double weight;
    private double skeletalMuscle;
    private double bodyFatPercentage;
    private double bmi;

    private LocalDateTime weightRecordDate;

}