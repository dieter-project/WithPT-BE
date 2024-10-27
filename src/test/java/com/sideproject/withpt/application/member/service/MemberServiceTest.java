package com.sideproject.withpt.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.controller.request.EditMemberDietTypeRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberExerciseFrequencyRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberTargetWeightRequest;
import com.sideproject.withpt.application.member.service.response.MemberSearchResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.user.member.Member;
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
class MemberServiceTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원 정보 수정")
    public void editMemberInfo() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberInfoRequest request = new EditMemberInfoRequest(
            "test2",
            Sex.WOMAN,
            "2024-07-19",
            173.0,
            73.5
        );

        Long memberId = member.getId();

        //when
        memberService.editMemberInfo(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getHeight()).isEqualTo(request.getHeight());
        assertThat(findMember.getName()).isEqualTo("test2");
    }

    @Test
    @DisplayName("식단 수정")
    public void editDietType() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberDietTypeRequest request = new EditMemberDietTypeRequest(DietType.PROTEIN);
        Long memberId = member.getId();

        //when
        memberService.editDietType(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getDietType()).isEqualTo(DietType.PROTEIN);
    }

    @Test
    @DisplayName("운동목표 수정")
    public void editExerciseFrequency() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberExerciseFrequencyRequest request = new EditMemberExerciseFrequencyRequest(ExerciseFrequency.FIRST_TIME);
        Long memberId = member.getId();

        //when
        memberService.editExerciseFrequency(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getExerciseFrequency()).isEqualTo(ExerciseFrequency.FIRST_TIME);
    }

    @Test
    @DisplayName("목표체중 수정")
    public void editTargetWeight() {
        //given
        Member member = memberRepository.save(createMember("test2", "test2@test.com"));

        EditMemberTargetWeightRequest request = new EditMemberTargetWeightRequest(80.5);
        Long memberId = member.getId();

        //when
        memberService.editTargetWeight(request, memberId);

        //then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getTargetWeight()).isEqualTo(request.getTargetWeight());
    }

    @DisplayName("이름으로 회원 검색")
    @Test
    void searchMembers() {
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
        Slice<MemberSearchResponse> result = memberService.searchMembers(pageable, name);

        // then
        assertThat(result.getContent()).hasSize(2)
            .extracting("name", "email")
            .containsExactly(
                tuple("test1", "test1@test.com"),
                tuple("test10", "test10@test.com")
            );

    }

    private Member createMember(String name, String email) {
        return Member.builder()
            .name(name)
            .email(email)
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
            .build();
    }
}