package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseCreateRequest;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;

    public List<ExerciseListResponse> findAllExerciseList(Long memberId) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        validateMemberId(memberId);

        List<ExerciseListResponse> exerciseList =
                exerciseRepository
                        .findByMemberIdAndCreatedDateBetween(memberId, startOfDay, endOfDay).stream()
                        .map(ExerciseListResponse::from)
                        .collect(Collectors.toList());

        return exerciseList;
    }

    public ExerciseListResponse findOneExercise(Long memberId, Long exerciseId) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
        return ExerciseListResponse.from(exercise);
    }

    @Transactional
    public void saveExercise(Long memberId, ExerciseCreateRequest request) {
        Member member = validateMemberId(memberId);
        exerciseRepository.save(request.toEntity(member));
    }

    @Transactional
    public void modifyExercise(Long memberId, Long exerciseId, ExerciseCreateRequest request) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
        exercise.update(request);
    }

    @Transactional
    public void deleteExercise(Long memberId, Long exerciseId) {
        validateExerciseId(exerciseId, memberId);
        exerciseRepository.deleteById(exerciseId);
    }

    private Member validateMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
        return member;
    }

    private Exercise validateExerciseId(Long exerciseId, Long memberId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);

        Member member = validateMemberId(memberId);
        if (!exercise.getMember().equals(member)) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }

        return exercise;
    }

}
