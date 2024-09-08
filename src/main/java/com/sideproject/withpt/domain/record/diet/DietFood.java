package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.domain.BaseEntity;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_food_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_info_id")
    private DietInfo dietInfo;

    private String name;

    private int capacity;

    private String units;

    private double calories;

    private double carbohydrate;    // 탄수화물

    private double protein;    // 단백질

    private double fat;    // 지방

    @Builder
    public DietFood(DietInfo dietInfo, DietFood dietFood) {
        this.dietInfo = dietInfo;
        this.name = dietFood.getName();
        this.capacity = dietFood.getCapacity();
        this.units = dietFood.getUnits();
        this.calories = dietFood.getCalories();
        this.carbohydrate = dietFood.getCarbohydrate();
        this.protein = dietFood.getProtein();
        this.fat = dietFood.getFat();
    }

    protected void setDietInfo(DietInfo dietInfo) {
        this.dietInfo = dietInfo;
    }

}
