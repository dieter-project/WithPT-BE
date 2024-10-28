package com.sideproject.withpt.application.member.service;

import com.sideproject.withpt.application.member.controller.request.EditMemberDietTypeRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberExerciseFrequencyRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberInfoRequest;
import com.sideproject.withpt.application.member.controller.request.EditMemberTargetWeightRequest;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.member.service.response.MemberAndPTInfoResponse;
import com.sideproject.withpt.application.member.service.response.MemberInfoResponse;
import com.sideproject.withpt.application.member.service.response.MemberSearchResponse;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PersonalTrainingRepository personalTrainingRepository;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    public Slice<MemberSearchResponse> searchMembers(Pageable pageable, String name) {
        return memberRepository.findBySearchOption(pageable, name);
    }

    public MemberAndPTInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return MemberAndPTInfoResponse.of(
            MemberInfoResponse.of(member),
            personalTrainingRepository.findPtAssignedTrainerInformation(member)
        );
    }

    @Transactional
    public void editMemberInfo(EditMemberInfoRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        member.editMemberInfo(
            request.getName(),
            request.getBirth(),
            request.getSex(),
            request.getHeight(),
            request.getWeight()
        );
    }

    @Transactional
    public void editDietType(EditMemberDietTypeRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        member.editDietType(request.getDietType());
    }

    @Transactional
    public void editExerciseFrequency(EditMemberExerciseFrequencyRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        member.editExerciseFrequency(request.getExerciseFrequency());
    }

    @Transactional
    public void editTargetWeight(EditMemberTargetWeightRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        member.editTargetWeight(request.getTargetWeight());
    }

}
