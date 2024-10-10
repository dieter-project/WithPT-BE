package com.sideproject.withpt.application.record.diet.repository;

import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface DietQueryRepository {

    Slice<Diets> findAllPageableByMemberAndUploadDate(Member member, LocalDate uploadDate, Pageable pageable);

    List<DietInfoDto> findAllDietInfoAndDietFoodByDiets(Member member, Diets diets);

    Map<LocalDate, Diets> findDietsByYearMonth(Member member, int year, int month);
}
