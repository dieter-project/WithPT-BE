package com.sideproject.withpt.domain.record;

import com.sideproject.withpt.application.diet.dto.request.DietRequest;
import com.sideproject.withpt.application.type.MealCategory;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diets extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diets_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "diets", cascade = CascadeType.ALL)
    private List<FoodItem> foodItemList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MealCategory mealCategory;
    private LocalDateTime mealTime;

    public void addDietFood(FoodItem foodItem) {
        foodItemList.add(foodItem);
        foodItem.setDiets(this);
    }

    public void updateDiets(DietRequest request) {
        this.mealCategory = request.getMealCategory();
        this.mealTime = request.getMealTime();
    }

}
