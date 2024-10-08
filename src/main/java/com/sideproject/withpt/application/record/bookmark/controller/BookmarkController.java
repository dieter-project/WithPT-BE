package com.sideproject.withpt.application.record.exercise.controller;

import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkSaveRequest;
import com.sideproject.withpt.application.record.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public void saveBookmark(
        @Valid @RequestBody BookmarkSaveRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        bookmarkService.saveBookmark(memberId, request.toServiceDto());
    }

//    @Operation(summary = "회원의 북마크 리스트 전체 조회하기")
//    @GetMapping
//    public ApiSuccessResponse<List<BookmarkResponse>> findAllBookmarkList(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
//        List<BookmarkResponse> bookmarkList = bookmarkService.findAllBookmark(memberId);
//        return ApiSuccessResponse.from(bookmarkList);
//    }
//
//    @Operation(summary = "북마크 단건 조회하기")
//    @GetMapping("/{bookmarkId}")
//    public ApiSuccessResponse<BookmarkResponse> findOneBookmark(@PathVariable Long bookmarkId, @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
//        BookmarkResponse bookmark = bookmarkService.findOneBookmark(memberId, bookmarkId);
//        return ApiSuccessResponse.from(bookmark);
//    }
//
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
