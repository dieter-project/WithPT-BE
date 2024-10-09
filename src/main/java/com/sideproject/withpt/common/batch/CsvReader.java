package com.sideproject.withpt.common.batch;

import com.sideproject.withpt.application.food.dto.FoodDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class CsvReader {
    @Bean
    public FlatFileItemReader<FoodDto> csvFoodReader(){
        FlatFileItemReader<FoodDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/food.csv"));
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setEncoding("EUC-KR");

        DefaultLineMapper<FoodDto> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(",");
        delimitedLineTokenizer.setNames(
                "foodCode", "foodName", "dataClassificationCode", "dataClassificationName", "energyKcal",
                "nutrientStandardAmount", "moisture", "protein", "fat", "ash", "carbohydrates", "sugars",
                "dietaryFiber", "calcium", "iron", "phosphorus", "potassium", "sodium", "vitaminA", "retinol",
                "betaCarotene", "thiamine", "riboflavin", "niacin", "vitaminC", "vitaminD", "cholesterol",
                "saturatedFattyAcids", "transFattyAcids", "disposalRate", "sourceCode", "sourceName", "foodWeight",
                "importStatus", "countryOfOriginCode", "countryOfOriginName", "companyName", "dataGenerationMethodCode",
                "dataGenerationMethodName", "dataGenerationDate", "dataStandardDate", "providingOrganizationCode",
                "providingOrganizationName"
        );
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<FoodDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(FoodDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        flatFileItemReader.setLineMapper(defaultLineMapper);

        return flatFileItemReader;

    }
}