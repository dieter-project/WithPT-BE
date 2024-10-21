package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.diet.utils.DietNutritionalStatistics;
import com.sideproject.withpt.domain.record.diet.utils.NutritionalInfo;
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

    public <T extends NutritionalInfo> void addTotalNutritionalStatistics(DietNutritionalStatistics<T> statistics) {
        this.totalCalorie += statistics.getTotalCalories();
        this.totalCarbohydrate += statistics.getTotalCarbohydrate();
        this.totalProtein += statistics.getTotalProtein();
        this.totalFat += statistics.getTotalFat();
    }

    public <T extends NutritionalInfo> void subtractTotalNutritionalStatistics(DietNutritionalStatistics<T> statistics) {
        this.totalCalorie -= statistics.getTotalCalories();
        this.totalCarbohydrate -= statistics.getTotalCarbohydrate();
        this.totalProtein -= statistics.getTotalProtein();
        this.totalFat -= statistics.getTotalFat();
    }

    public void subtractTotalNutritionalStatistics(DietInfo dietInfo) {
        this.totalCalorie -= dietInfo.getTotalCalorie();
        this.totalCarbohydrate -= dietInfo.getTotalCarbohydrate();
        this.totalProtein -= dietInfo.getTotalProtein();
        this.totalFat -= dietInfo.getTotalFat();
    }
}
