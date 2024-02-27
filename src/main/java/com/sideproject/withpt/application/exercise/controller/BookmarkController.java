package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.BookmarkRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkResponse;
import com.sideproject.withpt.application.exercise.service.BookmarkService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/exercise/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "회원의 북마크 리스트 전체 조회하기")
    @GetMapping
    public ApiSuccessResponse<List<BookmarkResponse>> findAllBookmarkList(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        List<BookmarkResponse> bookmarkList = bookmarkService.findAllBookmark(memberId);
        return ApiSuccessResponse.from(bookmarkList);
    }

    @Operation(summary = "북마크 단건 조회하기")
    @GetMapping("/{bookmarkId}")
    public ApiSuccessResponse<BookmarkResponse> findOneBookmark(@PathVariable Long bookmarkId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        BookmarkResponse bookmark = bookmarkService.findOneBookmark(memberId, bookmarkId);
        return ApiSuccessResponse.from(bookmark);
    }

    @Operation(summary = "북마크 입력하기")
    @PostMapping
    public void saveBookmark(@Valid @RequestBody BookmarkRequest request, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bookmarkService.saveBookmark(memberId, request);
    }

    @Operation(summary = "북마크 수정하기")
    @PatchMapping("/{bookmarkId}")
    public void modifyBookmark(@Valid @RequestBody BookmarkRequest request,
                                             @PathVariable Long bookmarkId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bookmarkService.modifyBookmark(memberId, bookmarkId, request);
    }

    @Operation(summary = "북마크 삭제하기")
    @DeleteMapping
    public void deleteExercise(@RequestParam String bookmarkIds, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bookmarkService.deleteBookmark(memberId, bookmarkIds);
    }

}
