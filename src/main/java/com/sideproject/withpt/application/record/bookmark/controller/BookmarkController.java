package com.sideproject.withpt.application.record.exercise.controller;

import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkSaveRequest;
import com.sideproject.withpt.application.record.bookmark.service.BookmarkService;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/record/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 입력하기")
    @PostMapping
    public ApiSuccessResponse<BookmarkResponse> saveBookmark(@Valid @RequestBody BookmarkSaveRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            bookmarkService.saveBookmark(memberId, request.toServiceDto())
        );
    }

    @Operation(summary = "북마크 리스트 조회하기")
    @GetMapping
    public ApiSuccessResponse<List<BookmarkResponse>> findAllBookmarkList(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            bookmarkService.findAllBookmark(memberId)
        );
    }

    @Operation(summary = "북마크 1건 조회하기")
    @GetMapping("/{bookmarkId}")
    public ApiSuccessResponse<BookmarkResponse> findOneBookmark(@PathVariable Long bookmarkId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            bookmarkService.findOneBookmark(memberId, bookmarkId)
        );
    }

//    @Operation(summary = "북마크 수정하기")
//    @PatchMapping("/{bookmarkId}")
//    public void modifyBookmark(@Valid @RequestBody BookmarkRequest request,
//                                             @PathVariable Long bookmarkId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
//        bookmarkService.modifyBookmark(memberId, bookmarkId, request);
//    }
//
//    @Operation(summary = "북마크 삭제하기")
//    @DeleteMapping
//    public void deleteExercise(@RequestParam String bookmarkIds, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
//        bookmarkService.deleteBookmark(memberId, bookmarkIds);
//    }

}
