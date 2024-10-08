package com.sideproject.withpt.application.record.bookmark.repository;

import static com.sideproject.withpt.common.type.ExerciseType.AEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.ANAEROBIC;
import static com.sideproject.withpt.common.type.ExerciseType.STRETCHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.bookmark.BookmarkBodyCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class BookmarkRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @DisplayName("회원의 북마크 리스트 조회")
    @Test
    void findAllByMemberOrderByUploadDateDescTitleAsc() {
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
        List<Bookmark> result = bookmarkRepository.findAllByMemberOrderByUploadDateDescTitleAsc(member);

        // then
        assertThat(result).hasSize(4)
            .extracting("title", "exerciseType")
            .containsExactly(
                tuple("무산소", ANAEROBIC),
                tuple("유산소1", AEROBIC),
                tuple("유산소2", AEROBIC),
                tuple("스트레칭", STRETCHING)
            );
    }

    @DisplayName("회원의 북마크 1건 조회")
    @Test
    void findByIdAndMember() {
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

        // when
        Optional<Bookmark> optionalBookmark = bookmarkRepository.findByIdAndMember(bookmarkId, member);

        // then
        assertThat(optionalBookmark).isPresent();
        Bookmark result = optionalBookmark.get();

        assertThat(result)
            .extracting("title", "exerciseType")
            .contains("스트레칭", STRETCHING);
    }

    @DisplayName("회원의 북마크 n개 삭제하기")
    @Test
    void deleteAllByIdsAndMember() {
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

        List<Long> ids = List.of(bookmark1.getId(), bookmark2.getId(), bookmark3.getId());

        // when
        bookmarkRepository.deleteAllByIdsAndMember(ids, member1);

        // then
        List<Bookmark> result = bookmarkRepository.findAll();
        assertThat(result).hasSize(2)
            .extracting("member.name", "title")
            .contains(
                tuple("회원1", "스트레칭"),
                tuple("회원2", "유산소2")
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