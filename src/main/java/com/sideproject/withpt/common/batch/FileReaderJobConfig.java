package com.sideproject.withpt.common.batch;

import com.sideproject.withpt.application.Food.dto.FoodDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CsvReader csvReader;
    private  final CsvWriter csvScheduleWriter;

    private static final int chunkSize = 100000; // 데이터 처리할 row size


    @Bean
    public Job csvFoodJob(){
        return jobBuilderFactory.get("csvFoodJob")
                .start(csvFoodReaderStep())
                .build();
    }


    @Bean
    public Step csvFoodReaderStep(){
        return stepBuilderFactory.get("csvFoodReaderStep")
                .<FoodDto, FoodDto>chunk(chunkSize)
                .reader(csvReader.csvFoodReader())
                .writer(csvScheduleWriter)
                .build();
    }

}
