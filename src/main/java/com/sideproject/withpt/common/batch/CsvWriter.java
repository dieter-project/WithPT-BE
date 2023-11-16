package com.sideproject.withpt.common.batch;

import com.sideproject.withpt.application.Food.dto.FoodDto;
import com.sideproject.withpt.application.Food.repository.FoodRepository;
import com.sideproject.withpt.domain.record.Food;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CsvWriter implements ItemWriter<FoodDto> {

    private final FoodRepository foodRepository;

    @Override
    public void write(List<? extends FoodDto> items) throws Exception {
        List<Food> foodList = new ArrayList<>();

        items.forEach(getFoodDto -> {
            Food food = getFoodDto.toEntity();
            foodList.add(food);
        });

        foodRepository.saveAll(foodList);
    }

}
