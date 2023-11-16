package com.sideproject.withpt.application.Food.dto;

import com.sideproject.withpt.domain.record.Food;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {  // 49개

    private String foodCode;
    private String foodName;
    private String dataCategoryCode;
    private String dataCategoryName;
    private String originCode;
    private String originName;
    private String categoryCode;
    private String categoryName;
    private String representativeFoodCode;
    private String representativeFoodName;
    private String subCategoryCode;
    private String subCategoryName;
    private String subSubCategoryCode;
    private String subSubCategoryName;
    private String nutrientStandardAmount;
    private String energyKcal;
    private String moisture;
    private String protein;
    private String province;
    private String ash;
    private String carbohydrates;
    private String sugars;
    private String dietaryFiber;
    private String calcium;
    private String iron;
    private String phosphorus;
    private String potassium;
    private String sodium;
    private String vitaminA;
    private String retinol;
    private String betaCarotene;
    private String thiamine;
    private String riboflavin;
    private String niacin;
    private String vitaminC;
    private String vitaminD;
    private String cholesterol;
    private String saturatedFattyAcids;
    private String transFattyAcids;
    private String sourceCode;
    private String sourceName;
    private String foodWeight;
    private String companyName;
    private String dataCreationMethodCode;
    private String dataCreationMethodName;
    private String dataCreationDate;
    private String dataStandardDate;
    private String providerCode;
    private String providerName;

    public Food toEntity() {
        return Food.builder()
                .name(foodName)
                .foodGroup(categoryName)
                .totalGram(nutrientStandardAmount)
                .calories(energyKcal)
                .carbohydrate(carbohydrates)
                .protein(protein)
                .province(province)
                .sugars(sugars)
                .build();
    }

}

