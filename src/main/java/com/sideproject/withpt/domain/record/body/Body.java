package com.sideproject.withpt.domain.record.body;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Body extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private double targetWeight;
    private double weight;
    private double skeletalMuscle;
    private double bodyFatPercentage;
    private double bmi;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    @Builder
    public Body(Member member, double targetWeight, double weight, double skeletalMuscle, double bodyFatPercentage, double bmi,
        LocalDate uploadDate) {
        this.member = member;
        this.targetWeight = targetWeight;
        this.weight = weight;
        this.skeletalMuscle = skeletalMuscle;
        this.bodyFatPercentage = bodyFatPercentage;
        this.bmi = bmi;
        this.uploadDate = uploadDate;
    }

    public void changeWeight(double weight) {
        this.weight = weight;
    }

    public void updateBodyInfo(double skeletalMuscle, double bodyFatPercentage, double bmi) {
        this.skeletalMuscle = skeletalMuscle;
        this.bodyFatPercentage = bodyFatPercentage;
        this.bmi = bmi;
    }

}