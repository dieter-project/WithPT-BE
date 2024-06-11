package com.sideproject.withpt.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {


    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원 정보 수정")
    public void editMemberInfo() {
        //given
        EditMemberInfoRequest request = new EditMemberInfoRequest(
            "test2",
            Sex.WOMAN,
            "2024-07-19",
            173.0,
            73.5
        );

        Long memberId = 11L;

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));

        //when
        memberService.editMemberInfo(request, memberId);

        //then
        Member findMember = memberService.getMemberById(memberId);
        assertThat(findMember.getHeight()).isEqualTo(request.getHeight());
        assertThat(findMember.getName()).isEqualTo("test2");
    }

    private Member createMember() {
        Authentication authentication = Authentication.builder()
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .build();

        return Member.builder()
            .id(1L)
            .name("test")
            .authentication(authentication)
            .height(173.0)
            .height(73.5)
            .build();
    }
}