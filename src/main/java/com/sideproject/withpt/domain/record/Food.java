package com.sideproject.withpt.domain.record;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_item_id")
    private Long id;

    private String name;
    private String foodGroup;   // 식품군
    private int totalGram;  // 총내용량

    private int calories;
    private double carbohydrate;    // 탄수화물
    private double protein;    // 단백질
    private double province;    // 지방
    private double sugars;

}
