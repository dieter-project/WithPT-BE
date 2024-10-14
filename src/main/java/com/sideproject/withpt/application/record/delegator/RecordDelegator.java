package com.sideproject.withpt.application.record.delegator;

import com.sideproject.withpt.application.record.bookmark.service.BookmarkService;
import com.sideproject.withpt.application.record.exercise.controller.request.ExerciseRequest;
import com.sideproject.withpt.application.record.exercise.service.ExerciseService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RecordDelegator {

    private final ExerciseService exerciseService;
    private final BookmarkService bookmarkService;

    public void saveExerciseAndBookmark(Long memberId, List<ExerciseRequest> request, List<MultipartFile> files, LocalDate uploadDate) {
        exerciseService.saveExercise(memberId, request, files, uploadDate);
        request.forEach(
            exerciseRequest -> {
                if (exerciseRequest.getBookmarkYn()) {
                    bookmarkService.saveBookmark(memberId, exerciseRequest.toBookmarkSaveDto());
                }
            }
        );
    }
}
