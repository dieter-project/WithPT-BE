package com.sideproject.withpt.application.record.bookmark.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkEditRequest;
import com.sideproject.withpt.application.record.bookmark.exception.BookmarkException;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveDto;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkResponse;
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

    @Transactional
    public void deleteBookmark(Long memberId, List<Long> bookmarkIds) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        bookmarkRepository.deleteAllByIdsAndMember(bookmarkIds, member);
    }

    @Transactional
    public BookmarkResponse modifyBookmark(Long memberId, Long bookmarkId, BookmarkEditRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Bookmark bookmark = bookmarkRepository.findByIdAndMember(bookmarkId, member)
            .orElseThrow(() -> BookmarkException.BOOKMARK_NOT_EXIST);

        bookmark.update(
            request.getTitle(),
            request.getExerciseType(),
            request.toParentBodyCategory(),
            request.getWeight(),
            request.getExerciseSet(),
            request.getTimes(),
            request.getExerciseTime(),
            request.getUploadDate()
        );

        return BookmarkResponse.of(bookmark);
    }

}
