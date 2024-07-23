package com.sideproject.withpt.application.record;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.AllDatesRecordResponse.BodyInfoResponse;
import com.sideproject.withpt.application.record.AllDatesRecordResponse.DietResponse;
import com.sideproject.withpt.application.record.AllDatesRecordResponse.ExerciseResponse;
import com.sideproject.withpt.application.record.body.repository.BodyRepository;
import com.sideproject.withpt.application.record.diet.repository.DietQueryRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseQueryRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.utils.DateUtility;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private final MemberRepository memberRepository;
    private final ExerciseQueryRepository exerciseQueryRepository;
    private final DietQueryRepository dietQueryRepository;
    private final BodyRepository bodyRepository;

    public Map<String, AllDatesRecordResponse> getAllDatesRecord(Long memberId, int year, int month) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<Exercise> exerciseList = exerciseQueryRepository.findExercisesByYearMonth(member, year, month);
        List<Diets> dietsList = dietQueryRepository.findDietsByYearMonth(member, year, month);
        List<Body> bodyList = bodyRepository.findBodyByYearMonth(member, year, month);

        // 날짜별로 AllDatesRecordResponse 객체 생성 및 맵으로 변환
        return DateUtility.getAllDates(year, month).stream()
            .collect(Collectors.toMap(
                date -> date,
                date -> {
                    LocalDate localDate = LocalDate.parse(date);

                    // 날짜별로 데이터를 찾기
                    Diets diet = dietsList.stream()
                        .filter(d -> d.getUploadDate().equals(localDate))
                        .findFirst()
                        .orElse(null);

                    Body body = bodyList.stream()
                        .filter(b -> b.getUploadDate().equals(localDate))
                        .findFirst()
                        .orElse(null);

                    Exercise exercise = exerciseList.stream()
                        .filter(e -> e.getUploadDate().equals(localDate))
                        .findFirst()
                        .orElse(null);

                    // AllDatesRecordResponse 객체 생성
                    return AllDatesRecordResponse.builder()
                        .diet(DietResponse.convertToDietResponse(diet))
                        .exercise(ExerciseResponse.convertToExerciseResponse(exercise))
                        .bodyInfo(BodyInfoResponse.convertToBodyInfoResponse(body))
                        .build();
                },
                (existing, replacement) -> existing, // 충돌 시 기존 값 유지
                TreeMap::new // TreeMap을 사용하여 날짜별로 정렬
            ));
    }
}
