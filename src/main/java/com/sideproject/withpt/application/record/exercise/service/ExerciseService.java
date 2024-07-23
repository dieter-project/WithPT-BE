package com.sideproject.withpt.application.record.exercise.service;

import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.controller.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseListResponse;
import com.sideproject.withpt.application.record.exercise.controller.response.ExerciseResponse;
import com.sideproject.withpt.application.record.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.record.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    public ExerciseListResponse findAllExerciseList(Long memberId, String dateTime) {
        validateMemberId(memberId);

        List<String> imageUrls = new ArrayList<>();

        // 현재 예외처리 되어있는지 확인하기
        Optional.ofNullable(imageRepository.findByMemberIdAndUploadDateAndUsages(memberId, LocalDate.parse(dateTime), Usages.EXERCISE))
            .ifPresent(images -> {
                imageUrls.addAll(images.stream()
                    .map(Image::getUrl)
                    .collect(Collectors.toList()));
            });

        List<ExerciseResponse> exercise = exerciseRepository
            .findByMemberIdAndUploadDate(memberId, LocalDate.parse(dateTime)).stream()
            .map(ExerciseResponse::from)
            .collect(Collectors.toList());

        return ExerciseListResponse.from(exercise, imageUrls);
    }

    public ExerciseResponse findOneExercise(Long memberId, Long exerciseId) {
        Exercise exercise1 = exerciseRepository.findById(exerciseId).get();
        log.info("운동 기록 : {}", exercise1);
        for (ExerciseInfo exerciseInfo : exercise1.getExerciseInfos()) {
            log.info("운동 기록 정보 {} {}", exerciseInfo, exerciseInfo.getBodyParts());
        }

//        Exercise exercise = validateExerciseId(exerciseId, memberId);
        return null;
    }

    public BookmarkCheckResponse checkBookmark(String title, Long memberId) {
        bookmarkRepository.findByMemberIdAndTitle(memberId, title)
            .ifPresent(exercise -> {
                throw ExerciseException.BOOKMARK_ALREADY_EXISTS;
            });

        return BookmarkCheckResponse.from(true);
    }

    @Transactional
    public void saveExercise(Long memberId, List<ExerciseRequest> requests, List<MultipartFile> files) {
        if (requests.size() == 0) {
            throw GlobalException.INVALID_PARAMETER;
        }

        LocalDate uploadDate = requests.get(0).getUploadDate();
        Member member = validateMemberId(memberId);

        exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate)
            .ifPresentOrElse(exercise -> {
                    requests.forEach(request -> exercise.addExerciseInfo(request.toExerciseInfo()));
                    exerciseRepository.save(exercise);
                },
                () -> {
                    Exercise exercise = Exercise.builder()
                        .member(member)
                        .uploadDate(uploadDate)
                        .build();

                    requests.forEach(request -> exercise.addExerciseInfo(request.toExerciseInfo()));
                    exerciseRepository.save(exercise);
                });

        if (files != null && files.size() > 0) {
            imageUploader.uploadAndSaveImages(files, Usages.EXERCISE, uploadDate, member);
        }
    }

    @Transactional
    public void modifyExercise(Long memberId, Long exerciseId, ExerciseRequest request) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
//        exercise.update(request);
    }

    @Transactional
    public void deleteExercise(Long memberId, Long exerciseId) {
        validateExerciseId(exerciseId, memberId);
        exerciseRepository.deleteById(exerciseId);
    }

    @Transactional
    public void deleteExerciseImage(String url) {
        imageUploader.deleteImage(url);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    private Exercise validateExerciseId(Long exerciseId, Long memberId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);
        Member member = validateMemberId(memberId);

        if (!exercise.getMember().getId().equals(member.getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }
        return exercise;
    }

}
