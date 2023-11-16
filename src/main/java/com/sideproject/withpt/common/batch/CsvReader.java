package com.sideproject.withpt.common.batch;

import com.sideproject.withpt.application.Food.dto.FoodDto;
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
    public FlatFileItemReader<FoodDto> csvFoodReader() {
        /* 파일읽기 */
        FlatFileItemReader<FoodDto> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/csv/food.csv")); // 읽을 파일 경로 지정
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setEncoding("EUC-KR");

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<FoodDto> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : csv 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(","); // csv 파일에서 구분자
        delimitedLineTokenizer.setNames(
                "식품코드", "식품명", "데이터구분코드", "데이터구분명", "식품기원코드", "식품기원명",
                "식품대분류코드", "식품대분류명", "대표식품코드", "대표식품명", "식품중분류코드", "식품중분류명",
                "식품소분류코드", "식품소분류명", "식품세분류코드", "식품세분류명", "영양성분함량기준량", "에너지(kcal)",
                "수분(g)", "단백질(g)", "지방(g)", "회분(g)", "탄수화물(g)", "당류(g)", "식이섬유(g)", "칼슘(mg)",
                "철(mg)", "인(mg)", "칼륨(mg)", "나트륨(mg)", "비타민 A(μg RAE)", "레티놀(μg)", "베타카로틴(μg)",
                "티아민(mg)", "리보플라빈(mg)", "니아신(mg)", "비타민 C(mg)", "비타민 D(μg)", "콜레스테롤(mg)",
                "포화지방산(g)", "트랜스지방산(g)", "출처코드", "출처명", "식품중량", "업체명", "데이터생성방법코드",
                "데이터생성방법명", "데이터생성일자", "데이터기준일자", "제공기관코드", "제공기관명"
        );  // 행으로 읽은 데이터 매칭할 데이터 각 이름
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); // lineTokenizer 설정

        /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
        BeanWrapperFieldSetMapper<FoodDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(FoodDto.class);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); // fieldSetMapper 지정

        flatFileItemReader.setLineMapper(defaultLineMapper); // lineMapper 지정

        return flatFileItemReader;
    }
}
