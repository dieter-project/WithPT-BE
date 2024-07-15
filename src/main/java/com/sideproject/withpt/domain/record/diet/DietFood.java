package com.sideproject.withpt.domain.record.diet;

import com.sideproject.withpt.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

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

    protected void setDiets(DietInfo dietInfo) {
        this.dietInfo = dietInfo;
    }

}
