package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequestList;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Bookmark;
import com.sideproject.withpt.domain.record.Exercise;
import com.sideproject.withpt.domain.record.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;

    private final ImageUploader imageUploader;

    public List<ExerciseListResponse> findAllExerciseList(Long memberId) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        validateMemberId(memberId);

        return exerciseRepository
                .findByMemberIdAndExerciseDateBetween(memberId, startOfDay, endOfDay).stream()
                .map(ExerciseListResponse::from)
                .collect(Collectors.toList());
    }

    public ExerciseListResponse findOneExercise(Long memberId, Long exerciseId) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
        return ExerciseListResponse.from(exercise);
    }

    @Transactional
    public void saveExercise(Long memberId, List<ExerciseRequestList.ExerciseRequest> requestList) {
        Member member = validateMemberId(memberId);

        for (ExerciseRequestList.ExerciseRequest request : requestList) {
            if ("Y".equals(request.getBookmarkYn())) {
                bookmarkRepository.findByMemberIdAndTitle(memberId, request.getTitle())
                         // 동일한 북마크명이 존재하면 에러
                        .ifPresentOrElse(
                                existingBookmark -> {
                                    throw ExerciseException.BOOKMARK_ALREADY_EXISTS;
                                },
                                () -> {
                                    bookmarkRepository.save(request.toBookmarkEntity(member));
                                }
                        );
            }

            Exercise savedExercise = exerciseRepository.save(request.toExerciseEntity(member));

            if(request.getFile() != null && !request.getFile().isEmpty()) {
                imageUploader.uploadAndSaveImages(request.getFile(), savedExercise.getId(), Usages.EXERCISE);
            }
        }
    }

    @Transactional
    public void modifyExercise(Long memberId, Long exerciseId, ExerciseRequest request) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
        exercise.update(request);
    }

    @Transactional
    public void deleteExercise(Long memberId, Long exerciseId) {
        validateExerciseId(exerciseId, memberId);
        exerciseRepository.deleteById(exerciseId);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

    private Exercise validateExerciseId(Long exerciseId, Long memberId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);
        Member member = validateMemberId(memberId);

        if (!exercise.getMember().getId().equals(member.getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }
        return exercise;
    }

}
