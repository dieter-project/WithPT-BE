package com.sideproject.withpt.application.record.bookmark.service;

import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkEditRequest;
import com.sideproject.withpt.application.record.bookmark.exception.BookmarkException;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveRequest;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkCheckResponse;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkResponse;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.bookmark.BookmarkBodyCategory;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class BookmarkServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Nested
    @DisplayName("북마크 입력하기")
    class SaveBookmark {

        @DisplayName("유산소")
        @Test
        void AEROBIC() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            LocalDate uploadDate = LocalDate.of(2024, 10, 8);
            BookmarkSaveRequest bookmarkSaveRequest = BookmarkSaveRequest.builder()
                .uploadDate(uploadDate)
                .title("유산소")
                .exerciseType(AEROBIC)
                .exerciseTime(100)
                .build();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveRequest);

            // then
            assertThat(response)
                .extracting("uploadDate", "title", "exerciseType")
                .contains(uploadDate, "유산소", AEROBIC);
        }

        @DisplayName("무산소")
        @Test
        void ANAEROBIC() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            LocalDate uploadDate = LocalDate.of(2024, 10, 8);
            BookmarkSaveRequest bookmarkSaveRequest = BookmarkSaveRequest.builder()
                .uploadDate(uploadDate)
                .title("무산소")
                .exerciseType(ANAEROBIC)
                .bodyPart(BodyPart.UPPER_BODY.name())
                .specificBodyParts(List.of(BodyPart.CHEST.name(), BodyPart.SHOULDERS.name(), BodyPart.ARMS.name()))
                .weight(100)
                .times(10)
                .exerciseSet(5)
                .build();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveRequest);

            // then
            assertThat(response)
                .extracting("uploadDate", "title", "exerciseType", "bodyPart", "specificBodyParts", "weight", "times", "exerciseSet")
                .contains(
                    uploadDate, "무산소", ANAEROBIC, BodyPart.UPPER_BODY, List.of(BodyPart.CHEST, BodyPart.SHOULDERS, BodyPart.ARMS), 100, 10, 5
                );
        }

        @DisplayName("스트레칭")
        @Test
        void STRETCHING() {
            // given
            Member member = memberRepository.save(createMember("회원"));
            LocalDate uploadDate = LocalDate.of(2024, 9, 3);

            BookmarkSaveRequest bookmarkSaveRequest = BookmarkSaveRequest.builder()
                .uploadDate(uploadDate)
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveRequest);

            // then
            assertThat(response)
                .extracting("uploadDate", "title", "exerciseType", "bodyPart", "specificBodyParts", "exerciseTime", "weight", "times")
                .contains(
                    uploadDate, "스트레칭", STRETCHING, BodyPart.FULL_BODY, null, 60, 0, 0
                );
        }

        @DisplayName("이미 북마크가 존재하더라도 중복 title 로 저장 가능 - 중복 체크는 단순 참고용 체크")
        @Test
        void alreadyExists() {
            // given
            Member member = memberRepository.save(createMember("회원"));

            BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
            bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

            BookmarkSaveRequest bookmarkSaveRequest = BookmarkSaveRequest.builder()
                .uploadDate(LocalDate.of(2024, 10, 10))
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            final Long memberId = member.getId();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(memberId, bookmarkSaveRequest);

            // then
            assertThat(response)
                .extracting("uploadDate", "title", "exerciseType", "bodyPart", "specificBodyParts", "exerciseTime", "weight", "times")
                .contains(
                    LocalDate.of(2024, 10, 10), "스트레칭", STRETCHING, BodyPart.FULL_BODY, null, 60, 0, 0
                );

            List<Bookmark> result = bookmarkRepository.findAll();
            assertThat(result).hasSize(2)
                .extracting("uploadDate", "title", "exerciseType")
                .contains(
                    tuple(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING),
                    tuple(LocalDate.of(2024, 10, 10), "스트레칭", STRETCHING)
                );
        }
    }

    @DisplayName("북마크 리스트 조회하기")
    @Test
    void findAllBookmark() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소1", AEROBIC, 100, member));
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소2", AEROBIC, 100, member));

        BookmarkBodyCategory UPPER_BODY = createParentBodyCategory(
            BodyPart.UPPER_BODY,
            List.of(
                createChildBodyCategory(BodyPart.CHEST),
                createChildBodyCategory(BodyPart.SHOULDERS),
                createChildBodyCategory(BodyPart.ARMS)
            )
        );
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 10), "무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, member));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

        // when
        List<BookmarkResponse> responses = bookmarkService.findAllBookmark(member.getId());

        // then
        assertThat(responses).hasSize(4)
            .extracting("title", "exerciseType")
            .contains(
                tuple("무산소", ANAEROBIC),
                tuple("유산소1", AEROBIC),
                tuple("유산소2", AEROBIC),
                tuple("스트레칭", STRETCHING)
            );
    }

    @DisplayName("회원의 북마크 1건 조회")
    @Test
    void findOneBookmark() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소1", AEROBIC, 100, member));
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소2", AEROBIC, 100, member));

        BookmarkBodyCategory UPPER_BODY = createParentBodyCategory(
            BodyPart.UPPER_BODY,
            List.of(
                createChildBodyCategory(BodyPart.CHEST),
                createChildBodyCategory(BodyPart.SHOULDERS),
                createChildBodyCategory(BodyPart.ARMS)
            )
        );
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 10), "무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, member));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        Bookmark savedBookmark = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

        final Long bookmarkId = savedBookmark.getId();
        final Long memberId = member.getId();

        // when
        BookmarkResponse response = bookmarkService.findOneBookmark(memberId, bookmarkId);

        // then
        assertThat(response)
            .extracting("title", "exerciseType")
            .contains("스트레칭", STRETCHING);
    }

    @DisplayName("회원의 북마크 1건 조회 시 북마크가 존재하지 않을 때")
    @Test
    void findOneBookmarkThrowException() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        final Long bookmarkId = 1L;
        final Long memberId = member.getId();

        // when // then

        assertThatThrownBy(() -> bookmarkService.findOneBookmark(memberId, bookmarkId))
            .isInstanceOf(BookmarkException.class)
            .hasMessage("해당 북마크 데이터가 존재하지 않습니다.")
        ;
    }

    @DisplayName("회원의 북마크 n개 삭제하기")
    @Test
    void deleteBookmark() {
        // given
        Member member1 = memberRepository.save(createMember("회원1"));
        Member member2 = memberRepository.save(createMember("회원2"));

        Bookmark bookmark1 = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소1", AEROBIC, 100, member1));
        Bookmark bookmark2 = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 8), "유산소2", AEROBIC, 100, member2));

        BookmarkBodyCategory UPPER_BODY = createParentBodyCategory(
            BodyPart.UPPER_BODY,
            List.of(
                createChildBodyCategory(BodyPart.CHEST),
                createChildBodyCategory(BodyPart.SHOULDERS),
                createChildBodyCategory(BodyPart.ARMS)
            )
        );
        Bookmark bookmark3 = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 10), "무산소", ANAEROBIC, UPPER_BODY, 100, 10, 5, member1));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        Bookmark bookmark4 = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member1));

        final Long memberId = member1.getId();
        final List<Long> ids = List.of(bookmark1.getId(), bookmark2.getId(), bookmark3.getId());

        // when
        bookmarkService.deleteBookmark(memberId, ids);

        // then
        List<Bookmark> result = bookmarkRepository.findAll();
        assertThat(result).hasSize(2)
            .extracting("member.name", "title")
            .contains(
                tuple("회원1", "스트레칭"),
                tuple("회원2", "유산소2")
            );
    }

    @DisplayName("북마크 수정하기")
    @Test
    void modifyBookmark() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        Bookmark bookmark = bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

        BookmarkEditRequest request = BookmarkEditRequest.builder()
            .uploadDate(LocalDate.of(2024, 10, 9))
            .title("무산소")
            .exerciseType(ANAEROBIC)
            .bodyPart(BodyPart.UPPER_BODY.name())
            .specificBodyParts(List.of(BodyPart.CHEST.name(), BodyPart.SHOULDERS.name(), BodyPart.ARMS.name()))
            .weight(100)
            .times(10)
            .exerciseSet(5)
            .build();

        final Long memberId = member.getId();
        final Long bookmarkId = bookmark.getId();

        // when
        bookmarkService.modifyBookmark(memberId, bookmarkId, request);

        // then
        Bookmark response = bookmarkRepository.findByIdAndMember(bookmarkId, member).get();
        assertThat(response)
            .extracting("id", "uploadDate", "title", "exerciseType")
            .contains(
                bookmarkId, LocalDate.of(2024, 10, 9), "무산소", ANAEROBIC
            );
    }

    @DisplayName("북마크명과 중복되는 이름 있는지 체크")
    @Test
    void checkBookmark() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

        final String title = "스트레칭2";
        final Long memberId = member.getId();

        // when
        BookmarkCheckResponse response = bookmarkService.checkBookmarkDuplicate(title, memberId);

        // then
        assertThat(response)
            .extracting("isDuplicated", "message")
            .contains(
                false, "북마크 등록이 가능합니다."
            );
    }

    @DisplayName("북마크명과 중복되는 이름이 이미 있으면 true 응답")
    @Test
    void checkBookmarkThrowException() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
        bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

        final String title = "스트레칭";
        final Long memberId = member.getId();

        // when
        BookmarkCheckResponse response = bookmarkService.checkBookmarkDuplicate(title, memberId);

        // then
        assertThat(response)
            .extracting("isDuplicated", "message")
            .contains(
                true, "중복된 북마크명이 존재합니다."
            );
    }

    private Bookmark createBookmark(LocalDate uploadDate, String title, ExerciseType exerciseType, int exerciseTime, Member member) {
        return Bookmark.builder()
            .member(member)
            .uploadDate(uploadDate)
            .title(title)
            .exerciseType(exerciseType)
            .exerciseTime(exerciseTime)
            .build();
    }

    private Bookmark createBookmark(LocalDate uploadDate, String title, ExerciseType exerciseType, BookmarkBodyCategory bodyCategory, int weight, int times, int exerciseSet, Member member) {
        return Bookmark.builder()
            .member(member)
            .uploadDate(uploadDate)
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(bodyCategory)
            .weight(weight)
            .times(times)
            .exerciseSet(exerciseSet)
            .build();
    }

    private Bookmark createBookmark(LocalDate uploadDate, String title, ExerciseType exerciseType, BookmarkBodyCategory bodyCategory, int exerciseTime, Member member) {
        return Bookmark.builder()
            .member(member)
            .uploadDate(uploadDate)
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(bodyCategory)
            .exerciseTime(exerciseTime)
            .build();
    }

    private BookmarkBodyCategory createParentBodyCategory(BodyPart bodyPart, List<BookmarkBodyCategory> children) {
        return BookmarkBodyCategory.builder()
            .name(bodyPart)
            .children(children)
            .build();
    }

    private BookmarkBodyCategory createChildBodyCategory(BodyPart bodyPart) {
        return BookmarkBodyCategory.builder()
            .name(bodyPart)
            .build();
    }

    private Member createMember(String name) {
        return Member.builder()
            .email("test@test.com")
            .name(name)
            .dietType(DietType.DIET)
            .role(Role.MEMBER)
            .build();
    }
}