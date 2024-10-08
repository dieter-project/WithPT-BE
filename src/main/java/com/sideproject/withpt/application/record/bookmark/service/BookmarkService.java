package com.sideproject.withpt.application.record.bookmark.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.bookmark.exception.BookmarkException;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveDto;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkResponse;
import com.sideproject.withpt.application.record.exercise.exception.ExerciseException;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BookmarkResponse saveBookmark(Long memberId, BookmarkSaveDto bookmarkSaveDto) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        if (bookmarkRepository.existsByMemberAndTitle(member, bookmarkSaveDto.getTitle())) {
            throw BookmarkException.BOOKMARK_ALREADY_EXISTS;
        }

        return BookmarkResponse.of(
            bookmarkRepository.save(bookmarkSaveDto.toEntity(member))
        );
    }

    public List<BookmarkResponse> findAllBookmark(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return bookmarkRepository.findAllByMemberOrderByUploadDateDescTitleAsc(member).stream()
            .map(BookmarkResponse::of)
            .collect(Collectors.toList());
    }

    public BookmarkResponse findOneBookmark(Long memberId, Long bookmarkId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return BookmarkResponse.of(
            bookmarkRepository.findByIdAndMember(bookmarkId, member)
                .orElseThrow(() -> BookmarkException.BOOKMARK_NOT_EXIST)
        );
    }

//    @Transactional
//    public void modifyBookmark(Long memberId, Long bookmarkId, BookmarkRequest request) {
//        Bookmark bookmark = validateBookmarkId(bookmarkId, memberId);
//        bookmark.update(request);
//    }
//
//    @Transactional
//    public void deleteBookmark(Long memberId, String bookmarkIds) {
//        List<Long> longBookmarkIds =
//            Arrays.stream(bookmarkIds.split(","))
//                .map(Long::parseLong)
//                .collect(Collectors.toList());
//
//        for (Long bookmarkId : longBookmarkIds) {
//            validateBookmarkId(bookmarkId, memberId);
//        }
//
//        bookmarkRepository.deleteAllByIds(longBookmarkIds);
//    }

    private Bookmark validateBookmarkId(Long bookmarkId, Long memberId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
            .orElseThrow(() -> BookmarkException.BOOKMARK_NOT_EXIST);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        if (!member.getId().equals(bookmark.getMember().getId())) {
            throw ExerciseException.EXERCISE_NOT_BELONG_TO_MEMBER;
        }

        return bookmark;
    }

}
