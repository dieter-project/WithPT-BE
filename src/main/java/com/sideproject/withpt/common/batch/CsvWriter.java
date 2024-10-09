package com.sideproject.withpt.common.batch;

import com.sideproject.withpt.application.food.dto.FoodDto;
import com.sideproject.withpt.application.food.repository.FoodRepository;
import com.sideproject.withpt.domain.record.diet.Food;
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
        List<Food> scheduleList = new ArrayList<>();

        items.forEach(getScheduleDto -> {
            Food food = getScheduleDto.toEntity();
            scheduleList.add(food);
        });

        foodRepository.saveAll(scheduleList);

    }

}
