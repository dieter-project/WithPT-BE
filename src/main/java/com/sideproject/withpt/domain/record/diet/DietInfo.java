package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.record.diet.utils.DietNutritionalStatistics;
import com.sideproject.withpt.domain.record.diet.utils.NutritionalInfo;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_info_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diets diets;

    @OneToMany(mappedBy = "dietInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietFood> dietFoods = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MealCategory mealCategory;

    private LocalDateTime mealTime;

    private double totalCalorie;
    private double totalProtein;
    private double totalCarbohydrate;
    private double totalFat;

    @Builder
    private DietInfo(Diets diets, MealCategory mealCategory, LocalDateTime mealTime, double totalCalorie, double totalProtein, double totalCarbohydrate, double totalFat,
        List<DietFood> dietFoods) {
        this.diets = diets;
        this.mealCategory = mealCategory;
        this.mealTime = mealTime;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalCarbohydrate = totalCarbohydrate;
        this.totalFat = totalFat;
        dietFoods.forEach(this::addDietFood);
    }

    public void addDietFood(DietFood dietFood) {
        dietFoods.add(dietFood);
        dietFood.setDietInfo(this);
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

    public void updateMealCategory(MealCategory mealCategory) {
        this.mealCategory = mealCategory;
    }

    public void updateMealTime(LocalDateTime mealTime) {
        this.mealTime = mealTime;
    }
}
