package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.BookmarkRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkResponse;
import com.sideproject.withpt.application.exercise.exception.ExerciseException;
import com.sideproject.withpt.application.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Bookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    public List<BookmarkResponse> findAllBookmark(Long memberId) {
        validateMemberId(memberId);

        return bookmarkRepository.findByMemberId(memberId).stream()
                .map(BookmarkResponse::from)
                .collect(Collectors.toList());
    }

    public BookmarkResponse findOneBookmark(Long memberId, Long bookmarkId) {
        Bookmark bookmark = validateBookmarkId(bookmarkId, memberId);
        return BookmarkResponse.from(bookmark);
    }

    @Transactional
    public void saveBookmark(Long memberId, BookmarkRequest request) {
        Member member = validateMemberId(memberId);

        bookmarkRepository.findByMemberIdAndTitle(memberId, request.getTitle())
                .ifPresentOrElse(
                        existingBookmark -> {
                            throw ExerciseException.BOOKMARK_ALREADY_EXISTS;
                        },
                        () -> {
                            bookmarkRepository.save(request.toEntity(member));
                        });
    }

    @Transactional
    public void modifyBookmark(Long memberId, Long bookmarkId, BookmarkRequest request) {
        Bookmark bookmark = validateBookmarkId(bookmarkId, memberId);
        bookmark.update(request);
    }

    @Transactional
    public void deleteBookmark(Long memberId, String bookmarkIds) {
        List<Long> longBookmarkIds =
                Arrays.stream(bookmarkIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        for (Long bookmarkId : longBookmarkIds) {
            validateBookmarkId(bookmarkId, memberId);
        }

        bookmarkRepository.deleteAllByIds(longBookmarkIds);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

    private Bookmark validateBookmarkId(Long bookmarkId, Long memberId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> ExerciseException.BOOKMARK_NOT_EXIST);

        Member member = validateMemberId(memberId);

        if (!member.getId().equals(bookmark.getMember().getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }

        return bookmark;
    }

}
