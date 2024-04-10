package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseListResponse;
import com.sideproject.withpt.application.exercise.dto.response.ExerciseResponse;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Exercise;
import com.sideproject.withpt.domain.record.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .findByMemberIdAndExerciseDate(memberId, LocalDate.parse(dateTime)).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());

        return ExerciseListResponse.from(exercise, imageUrls);
    }

    public ExerciseResponse findOneExercise(Long memberId, Long exerciseId) {
        Exercise exercise = validateExerciseId(exerciseId, memberId);
        return ExerciseResponse.from(exercise);
    }

    public BookmarkCheckResponse checkBookmark(String title, Long memberId) {
        bookmarkRepository.findByMemberIdAndTitle(memberId, title)
                .ifPresent(exercise -> {
                    throw ExerciseException.BOOKMARK_ALREADY_EXISTS;
                });

        return BookmarkCheckResponse.from(true);
    }

    @Transactional
    public void saveExercise(Long memberId, List<ExerciseRequest> requestList, List<MultipartFile> file) {
        LocalDate todayDate = requestList.get(0).getExerciseDate();
        Member member = validateMemberId(memberId);

        for (ExerciseRequest request : requestList) {
            if ("Y".equals(request.getBookmarkYn())) {
                bookmarkRepository.save(request.toBookmarkEntity(member));
            }
            exerciseRepository.save(request.toExerciseEntity(member));
        }

        if(file != null && file.size() > 0) {
            imageUploader.uploadAndSaveImages(file, todayDate, Usages.EXERCISE, member);
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

    @Transactional
    public void deleteExerciseImage(String url) {
        imageUploader.deleteImage(url);
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
