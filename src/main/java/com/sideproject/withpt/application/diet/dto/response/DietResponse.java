package com.sideproject.withpt.application.diet.dto.response;

import com.sideproject.withpt.application.type.MealCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietResponse {

    private MealCategory mealCategory;
    private LocalDateTime mealTime;

    private List<MultipartFile> file;

    // 전체 식단 탄수화물
    // 전체 식단 단백질
    // 전체 식단 지방

    //=================== 리스트 형태로
    // 식단 상세 음식명
    // 식단 상세 수량
    // 식단 상세 식사량
    // 식단 상세 칼로리

}
