package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diets extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate uploadDate;

    private String feedback;

    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;

    @Enumerated(EnumType.STRING)
    private DietType targetDietType;

    public void addTotalCalorie(double totalCalorie) {
        this.totalCalorie += totalCalorie;
    }

    public void addTotalProtein(double totalProtein) {
        this.totalProtein += totalProtein;
    }

    public void addTotalCarbohydrate(double totalCarbohydrate) {
        this.totalCarbohydrate += totalCarbohydrate;
    }

    public void addTotalFat(double totalFat) {
        this.totalFat += totalFat;
    }

    public void subtractTotalCalorie(double totalCalorie) {
        this.totalCalorie -= totalCalorie;
    }

    public void subtractTotalProtein(double totalProtein) {
        this.totalProtein -= totalProtein;
    }

    public void subtractTotalCarbohydrate(double totalCarbohydrate) {
        this.totalCarbohydrate -= totalCarbohydrate;
    }

    public void subtractTotalFat(double totalFat) {
        this.totalFat -= totalFat;
    }
}
