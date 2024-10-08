package com.sideproject.withpt.application.record.bookmark.service;

import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.bookmark.exception.BookmarkException;
import com.sideproject.withpt.application.record.bookmark.repository.BookmarkRepository;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveDto;
import com.sideproject.withpt.application.record.bookmark.service.response.BookmarkResponse;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.member.Member;
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
            BookmarkSaveDto bookmarkSaveDto = BookmarkSaveDto.builder()
                .uploadDate(uploadDate)
                .title("유산소")
                .exerciseType(AEROBIC)
                .exerciseTime(100)
                .build();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveDto);

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
            BookmarkSaveDto bookmarkSaveDto = BookmarkSaveDto.builder()
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
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveDto);

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

            BookmarkSaveDto bookmarkSaveDto = BookmarkSaveDto.builder()
                .uploadDate(uploadDate)
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            // when
            BookmarkResponse response = bookmarkService.saveBookmark(member.getId(), bookmarkSaveDto);

            // then
            assertThat(response)
                .extracting("uploadDate", "title", "exerciseType", "bodyPart", "specificBodyParts", "exerciseTime", "weight", "times")
                .contains(
                    uploadDate, "스트레칭", STRETCHING, BodyPart.FULL_BODY, null, 60, 0, 0
                );
        }

        @DisplayName("이미 북마크가 존재할 때")
        @Test
        void alreadyExists() {
            // given
            Member member = memberRepository.save(createMember("회원"));

            BookmarkBodyCategory FULL_BODY = createParentBodyCategory(BodyPart.FULL_BODY, null);
            bookmarkRepository.save(createBookmark(LocalDate.of(2024, 10, 4), "스트레칭", STRETCHING, FULL_BODY, 60, member));

            BookmarkSaveDto bookmarkSaveDto = BookmarkSaveDto.builder()
                .uploadDate(LocalDate.of(2024, 10, 10))
                .title("스트레칭")
                .exerciseType(STRETCHING)
                .bodyPart(BodyPart.FULL_BODY.name())
                .exerciseTime(60)
                .build();

            final Long memberId = member.getId();

            // when // then
            assertThatThrownBy(() ->  bookmarkService.saveBookmark(memberId, bookmarkSaveDto))
                .isInstanceOf(BookmarkException.class)
                .hasMessage("이미 존재하는 북마크명입니다.")
            ;
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

    public Bookmark createBookmark(Member member, String title, ExerciseType exerciseType, BookmarkBodyCategory bodyCategory, int weight, int exerciseSet, int times, int exerciseTime, LocalDate uploadDate) {
        return Bookmark.builder()
            .member(member)
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(bodyCategory)
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .uploadDate(uploadDate)
            .build();
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