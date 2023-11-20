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
public class Diet extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodItem> foodItemList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MealCategory mealCategory;
    private LocalDateTime mealTime;

    public void updateDiets(DietRequest request) {
        this.mealCategory = request.getMealCategory();
        this.mealTime = request.getMealTime();
    }

}
