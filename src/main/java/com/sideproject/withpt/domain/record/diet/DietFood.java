package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.record.diet.utils.NutritionalInfo;
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
public class DietFood extends BaseEntity implements NutritionalInfo {

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
    public DietFood(String name, int capacity, String units, double calories, double carbohydrate, double protein, double fat) {
        this.name = name;
        this.capacity = capacity;
        this.units = units;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
    }

    protected void setDietInfo(DietInfo dietInfo) {
        this.dietInfo = dietInfo;
    }


}
