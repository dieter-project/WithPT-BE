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
    @Column(name = "food_id")
    private Long id;

    private String name;
    private String foodGroup;   // 식품군
    private String totalGram;  // 영양성분함량기준량

    private String calories;
    private String carbohydrate;    // 탄수화물
    private String protein;    // 단백질
    private String province;    // 지방
    private String sugars;  // 당류

}
