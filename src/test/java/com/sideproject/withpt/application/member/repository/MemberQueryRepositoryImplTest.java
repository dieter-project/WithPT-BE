package com.sideproject.withpt.application.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.controller.response.MemberSearchResponse;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MemberQueryRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("이름으로 회원 검색")
    @Test
    void findBySearchOption() {
        // given
        memberRepository.saveAll(
            List.of(
                createMember("test1", "test1@test.com"),
                createMember("test2", "test2@test.com"),
                createMember("test3", "test3@test.com"),
                createMember("test4", "test4@test.com"),
                createMember("test5", "test5@test.com"),
                createMember("test6", "test6@test.com"),
                createMember("test7", "test7@test.com"),
                createMember("test8", "test8@test.com"),
                createMember("test9", "test9@test.com"),
                createMember("test10", "test10@test.com")
            )
        );

        String name = "1";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<MemberSearchResponse> result = memberRepository.findBySearchOption(pageable, name);

        // then
        assertThat(result.getContent()).hasSize(2)
            .extracting("name", "email")
            .containsExactly(
                tuple("test1", "test1@test.com"),
                tuple("test10", "test10@test.com")
            );

    }

    private Member createMember(String name, String email) {
        Authentication authentication = Authentication.builder()
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .build();

        return Member.builder()
            .name(name)
            .email(email)
            .authentication(authentication)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
            .build();
    }
}