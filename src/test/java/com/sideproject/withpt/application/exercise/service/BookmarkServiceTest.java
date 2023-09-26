package com.sideproject.withpt.application.exercise.service;

import com.sideproject.withpt.application.exercise.dto.request.BookmarkRequest;
import com.sideproject.withpt.application.exercise.dto.response.BookmarkResponse;
import com.sideproject.withpt.application.exercise.repository.BookmarkRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Bookmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    @DisplayName("특정 회원 북마크 리스트 전체 조회하기")
    void findBookmarkList() {
        // given
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(createMember()));
        given(bookmarkRepository.findByMemberId(any(Long.class))).willReturn(List.of(createAddBookmarkRequest().toEntity(createMember())));

        // when
        List<BookmarkResponse> allBookmark = bookmarkService.findAllBookmark(1L);

        // then
        then(bookmarkRepository).should(times(1)).findByMemberId(any(Long.class));
        assertThat(allBookmark.size()).isEqualTo(1);
        assertThat(allBookmark.get(0).getTitle()).isEqualTo("북마크명");
    }

    @Test
    @DisplayName("해당하는 북마크 조회하기")
    void findBookmark() {
        // given
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(createMember()));
        given(bookmarkRepository.findById(any(Long.class))).willReturn(Optional.of(createAddBookmarkRequest().toEntity(createMember())));

        // when
        BookmarkResponse bookmark = bookmarkService.findOneBookmark(1L, 1L);

        // then
        then(bookmarkRepository).should(times(1)).findById(any(Long.class));
        assertThat(bookmark.getTitle()).isEqualTo("북마크명");
    }

    @Test
    @DisplayName("북마크 입력 받아서 저장하기")
    void saveBookmark() {
        // given
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(createMember()));
        given(bookmarkRepository.save(any(Bookmark.class))).willReturn(createAddBookmarkRequest().toEntity(createMember()));

        // when
        bookmarkService.saveBookmark(1L, createAddBookmarkRequest());

        // then
        then(bookmarkRepository).should(times(1)).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("북마크 수정하기")
    void modifyBookmark() {
        // given
        BookmarkRequest bookmarkRequest = BookmarkRequest.builder().title("수정 북마크명").build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bookmarkRepository.findById(anyLong())).willReturn(Optional.of(createAddBookmarkRequest().toEntity(createMember())));

        // when
        bookmarkService.modifyBookmark(1L, 1L, bookmarkRequest);
        BookmarkResponse oneBookmark = bookmarkService.findOneBookmark(1L, 1L);

        // then
        assertThat("수정 북마크명").isEqualTo(oneBookmark.getTitle());
    }

    @Test
    @DisplayName("북마크 삭제하기")
    void deleteBookmark() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(bookmarkRepository.findById(anyLong())).willReturn(Optional.of(createAddBookmarkRequest().toEntity(createMember())));

        // when
        bookmarkService.deleteBookmark(1L, 1L);

        // then
        then(bookmarkRepository).should(times(1)).deleteById(anyLong());
    }

    private BookmarkRequest createAddBookmarkRequest() {
        return BookmarkRequest.builder()
                .title("북마크명")
                .weight(300)
                .set(3)
                .hour(3)
                .bodyPart(BodyPart.LOWER_BODY)
                .exerciseType(ExerciseType.ANAEROBIC)
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .nickname("test")
                .build();
    }

}
