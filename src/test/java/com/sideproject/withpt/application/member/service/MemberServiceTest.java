package com.sideproject.withpt.application.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.member.dto.request.MemberSignUpRequest;
import com.sideproject.withpt.application.member.dto.response.MemberSignUpResponse;
import com.sideproject.withpt.application.member.dto.response.NicknameCheckResponse;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;


    @Test
    @DisplayName("닉네임 중복")
    public void nicknameDuplicate() {
        //given
        Member member = Member.builder()
            .id(1L)
            .nickname("test")
            .build();
        String paramNickname = "test";

        given(memberRepository.findByNickname(any()))
            .willReturn(Optional.of(member));

        assertThatThrownBy(
            () -> memberService.checkNickname(paramNickname)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .isInstanceOf(RuntimeException.class)
            .hasMessage(GlobalException.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("사용 가능한 닉네임")
    public void nicknameNotDuplicate() {
        //given
        String paramNickname = "test";

        given(memberRepository.findByNickname(any()))
            .willReturn(Optional.empty());

        //when
        NicknameCheckResponse response = memberService.checkNickname(paramNickname);

        //then
        assertTrue(response.isDuplicateNickname());
    }

    @Test
    @DisplayName("회원 가입 성공")
    public void signUpMember() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("test@naver.com")
            .name("test")
            .build();

        given(memberRepository.findByEmail(request.getEmail()))
            .willReturn(Optional.empty());

        given(memberRepository.save(any()))
            .willReturn(request.toEntity());

        //when
        MemberSignUpResponse response = memberService.signUpMember(request);

        //then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("이미 회원 존재하여 회원 가입 실패")
    public void signUpMember_already_registered() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("test@naver.com")
            .name("test")
            .build();

        Member registeredMember = Member.builder()
            .email("test@naver.com")
            .name("test")
            .build();

        given(memberRepository.findByEmail(request.getEmail()))
            .willReturn(Optional.of(registeredMember));

        assertThatThrownBy(
            () -> memberService.signUpMember(request)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.ALREADY_REGISTERED_MEMBER.getMessage());
    }
}