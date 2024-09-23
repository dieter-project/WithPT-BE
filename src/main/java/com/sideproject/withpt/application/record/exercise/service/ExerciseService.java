package com.sideproject.withpt.application.record.exercise.service;

import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseEditRequest;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.controller.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseInfoResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseInfoResponse.ExerciseInformation;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.record.exercise.repository.BodyCategoryRepository;
import com.sideproject.withpt.application.record.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseInfoRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInfoRepository exerciseInfoRepository;
    private final BodyCategoryRepository bodyCategoryRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    public ExerciseResponse findExerciseAndExerciseInfos(Long memberId, LocalDate uploadDate) {
        Member member = validateMemberId(memberId);
        return exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate)
            .map(ExerciseResponse::of)
            .orElse(null);
    }

    public ExerciseInfoResponse findOneExerciseInfo(Long exerciseId, Long exerciseInfoId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);

        return exerciseRepository.findExerciseInfoById(exerciseInfoId)
            .map(exerciseInfo -> ExerciseInfoResponse.builder()
                .id(exercise.getId())
                .uploadDate(exercise.getUploadDate())
                .exerciseInfo(ExerciseInformation.of(exerciseInfo))
                .build())
            .orElseThrow(() -> ExerciseException.EXERCISE_INFO_NOT_EXIST);
    }

    public BookmarkCheckResponse checkBookmark(String title, Long memberId) {
        bookmarkRepository.findByMemberIdAndTitle(memberId, title)
            .ifPresent(exercise -> {
                throw ExerciseException.BOOKMARK_ALREADY_EXISTS;
            });

        return BookmarkCheckResponse.from(true);
    }

    @Transactional
    public void saveExercise(Long memberId, List<ExerciseRequest> request, List<MultipartFile> files, LocalDate uploadDate) {

        Member member = validateMemberId(memberId);

        exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate)
            .ifPresentOrElse(exercise -> {
                    request.forEach(e -> exercise.addExerciseInfo(e.toExerciseInfo()));
                },
                () -> {
                    Exercise exercise = Exercise.builder()
                        .member(member)
                        .exerciseInfos(request.stream()
                            .map(ExerciseRequest::toExerciseInfo)
                            .collect(Collectors.toList()))
                        .uploadDate(uploadDate)
                        .build();
                    exerciseRepository.save(exercise);
                });

        if (files != null && files.size() > 0) {
            imageUploader.uploadAndSaveImages(files, Usages.EXERCISE, uploadDate, member);
        }
    }

    @Transactional
    public void modifyExercise(Long exerciseId, Long exerciseInfoId, ExerciseEditRequest request) {
        exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);

        exerciseRepository.findExerciseInfoById(exerciseInfoId)
            .ifPresentOrElse(exerciseInfo -> {
                    ExerciseType newType = request.getExerciseType();
                    BodyCategory newBodyCategory = handleTypeChange(exerciseInfo, newType, request);

                    // 공통 업데이트 처리
                    exerciseInfo.update(
                        request.getTitle(),
                        newType,
                        newBodyCategory,
                        request.getWeight(),
                        request.getExerciseSet(),
                        request.getTimes(),
                        request.getExerciseTime()
                    );
                },
                () -> {
                    throw ExerciseException.EXERCISE_INFO_NOT_EXIST;
                }
            );
    }

    @Transactional
    public void deleteExerciseInfo(Long exerciseId, Long exerciseInfoId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);
        exercise.getExerciseInfos()
            .removeIf(info -> info.getId().equals(exerciseInfoId));

        exerciseInfoRepository.deleteExerciseInfoById(exerciseInfoId);

    }

    @Transactional
    public void deleteExerciseImage(String url) {
        imageUploader.deleteImage(url);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    /**
     * 운동 유형이 변경될 때 BodyCategory 처리
     */
    private BodyCategory handleTypeChange(ExerciseInfo exerciseInfo, ExerciseType newType, ExerciseEditRequest request) {
        // 기존 BodyCategory 삭제
        if (exerciseInfo.getBodyCategory() != null) {
            bodyCategoryRepository.delete(exerciseInfo.getBodyCategory());
        }

        if (newType == ExerciseType.AEROBIC) {
            // 유산소로 변경 시 BodyCategory가 필요 없음
            return null;
        }

        // 유산소가 아닌 경우에는 새 BodyCategory 생성
        return request.toParentBodyCategory();
    }

}
