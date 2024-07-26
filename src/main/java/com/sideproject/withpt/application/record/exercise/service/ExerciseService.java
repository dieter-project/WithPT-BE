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
import com.sideproject.withpt.application.record.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseInfoRepository;
import com.sideproject.withpt.application.record.exercise.repository.ExerciseRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Exercise;
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
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    public ExerciseResponse findExerciseAndExerciseInfos(Long memberId, LocalDate uploadDate) {
        Member member = validateMemberId(memberId);
        Exercise exercise = exerciseRepository.findFirstByMemberAndUploadDate(member, uploadDate)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);

        return new ExerciseResponse(exercise.getId(), exercise.getUploadDate(), 0,
            exercise.getExerciseInfos().stream()
                .map(ExerciseResponse.ExerciseInfoResponse::of)
                .collect(Collectors.toList())
        );
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
    public void modifyExercise(Long exerciseId, Long exerciseInfoId, ExerciseEditRequest request) {
        exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);

        exerciseRepository.findExerciseInfoById(exerciseInfoId)
            .ifPresentOrElse(exerciseInfo -> {
                    exerciseInfo.update(
                        request.getTitle(),
                        request.getExerciseType(),
                        request.getBodyParts().stream().map(BodyPart::valueOf)
                            .collect(Collectors.toList()),
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
    public void deleteExercise(Long exerciseId, Long exerciseInfoId) {
        exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> ExerciseException.EXERCISE_NOT_EXIST);
        exerciseInfoRepository.deleteById(exerciseInfoId);
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
