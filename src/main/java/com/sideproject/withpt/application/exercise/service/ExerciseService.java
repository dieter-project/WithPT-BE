package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<ExerciseListResponse> findExerciseList(String memberId) {
        List<ExerciseListResponse> exerciseList = exerciseRepository.findByMemberIdAndCreateDate(memberId, LocalDateTime.now());
        return exerciseList;
    }

}
