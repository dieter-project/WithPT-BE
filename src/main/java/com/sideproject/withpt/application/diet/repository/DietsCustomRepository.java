package com.sideproject.withpt.application.diet.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.diet.dto.response.DietResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DietsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 동적 쿼리
    // 아점저 카테고리에 따라서 데이터를 쏴줘야 함
    public DietResponse findDietInfo() {
        return null;
    }

}
