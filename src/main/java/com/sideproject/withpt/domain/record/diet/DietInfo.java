package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietInfo extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diets diets;

    @Default
    @OneToMany(mappedBy = "dietInfo", cascade = CascadeType.ALL)
    private List<DietFood> dietFoods = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MealCategory mealCategory;

    private LocalDateTime mealTime;

    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;

    public void addDietFood(DietFood dietFood) {
        dietFoods.add(dietFood);
        dietFood.setDiets(this);
    }

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
}