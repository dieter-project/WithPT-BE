package com.sideproject.withpt.application.exercise.controller;

import com.sideproject.withpt.application.exercise.dto.request.BookmarkRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkResponse;
import com.sideproject.withpt.application.exercise.service.BookmarkService;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
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

    // 북마크 리스트 전체 조회하기
    @GetMapping
    public ApiSuccessResponse<List<BookmarkResponse>> findAllBookmarkList(@AuthenticationPrincipal Long memberId) {
        List<BookmarkResponse> bookmarkList = bookmarkService.findAllBookmark(memberId);
        return ApiSuccessResponse.from(bookmarkList);
    }

    // 북마크 단건 조회하기
    @GetMapping("/{bookmarkId}")
    public ApiSuccessResponse<BookmarkResponse> findOneBookmark(@PathVariable Long bookmarkId, @AuthenticationPrincipal Long memberId) {
        BookmarkResponse bookmark = bookmarkService.findOneBookmark(memberId, bookmarkId);
        return ApiSuccessResponse.from(bookmark);
    }

    // 북마크 입력하기
    @PostMapping
    public ApiSuccessResponse saveBookmark(@Valid @RequestBody BookmarkRequest request, @AuthenticationPrincipal Long memberId) {
        bookmarkService.saveBookmark(memberId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 북마크 수정하기
    @PatchMapping("/{bookmarkId}")
    public ApiSuccessResponse modifyBookmark(@Valid @RequestBody BookmarkRequest request,
                                             @PathVariable Long bookmarkId, @AuthenticationPrincipal Long memberId) {
        bookmarkService.modifyBookmark(memberId, bookmarkId, request);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

    // 북마크 삭제하기
    @DeleteMapping("/{bookmarkId}")
    public ApiSuccessResponse deleteExercise(@PathVariable Long bookmarkId, @AuthenticationPrincipal Long memberId) {
        bookmarkService.deleteBookmark(memberId, bookmarkId);
        return ApiSuccessResponse.NO_DATA_RESPONSE;
    }

}
