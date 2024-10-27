package com.sideproject.withpt.application.food.model;

import com.sideproject.withpt.domain.record.diet.Food;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodDto {

    private String foodCode;
    private String foodName;
    private String dataClassificationCode;
    private String dataClassificationName;
    private String energyKcal;
    private String nutrientStandardAmount;
    private String moisture;
    private String protein;
    private String fat;
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
    private String disposalRate;
    private String sourceCode;
    private String sourceName;
    private String foodWeight;
    private String importStatus;
    private String countryOfOriginCode;
    private String countryOfOriginName;
    private String companyName;
    private String dataGenerationMethodCode;
    private String dataGenerationMethodName;
    private String dataGenerationDate;
    private String dataStandardDate;
    private String providingOrganizationCode;
    private String providingOrganizationName;

    public Food toEntity() {
        return Food.builder()
                .name(foodName.replaceAll("_", " "))
                .totalGram(nutrientStandardAmount.replace("g", ""))
                .calories(energyKcal)
                .carbohydrate(carbohydrates)
                .protein(protein)
                .fat(fat)
                .sugars(sugars)
                .build();
    }
}
