package com.sideproject.withpt.domain.record;

import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
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
public class Body extends BaseEntity {

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

    private LocalDateTime bodyRecordDate;

    public void changeWeight(double weight) {
        this.weight = weight;
    }

    public void updateBodyInfo(BodyInfoRequest request) {
        this.skeletalMuscle = request.getSkeletalMuscle();
        this.bodyFatPercentage = request.getBodyFatPercentage();
        this.bmi = request.getBmi();
    }

}