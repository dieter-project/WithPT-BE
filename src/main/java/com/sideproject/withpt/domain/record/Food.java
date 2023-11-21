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
    private int totalGram;  // 영양성분함량기준량

    private int calories;
    private int carbohydrate;    // 탄수화물
    private int protein;    // 단백질
    private int fat;    // 지방

}
