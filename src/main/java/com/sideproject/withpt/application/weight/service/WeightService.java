package com.sideproject.withpt.application.weight.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.weight.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.weight.dto.response.BodyInfoResponse;
import com.sideproject.withpt.application.weight.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.weight.exception.WeightException;
import com.sideproject.withpt.application.weight.repository.WeightRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Weight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeightService {

    private final WeightRepository weightRepository;
    private final MemberRepository memberRepository;

    // 체중 및 전체 신체 정보 조회하기
    public WeightInfoResponse findWeightInfo(Long memberId) {
        validateMemberId(memberId);
        Weight weight =
                weightRepository.findTop1ByMemberIdOrderByWeightRecordDate(memberId)
                .orElseThrow(() -> WeightException.WEIGHT_NOT_EXIST);

        return WeightInfoResponse.from(weight);
    }

    // 신체 정보 조회하기
    public BodyInfoResponse findBodyInfo(Long memberId) {
        validateMemberId(memberId);
        Weight weight = weightRepository.findTop1ByMemberIdOrderByWeightRecordDate(memberId)
                .orElseThrow(() -> WeightException.WEIGHT_NOT_EXIST);

        return BodyInfoResponse.from(weight);
    }

    // 체중 입력하기
    public void saveWeight(Long memberId, double weight) {
        // 오늘 날짜라면 수정하기

        // 오늘 날짜가 아니면 새로 데이터 저장하기
        // 체중 저장할 때 기존 인바디 데이터를 조회해서 같이 가져오기
    }

    // 신체 정보 입력하기
    public void saveBodyInfo(Long memberId, BodyInfoRequest request) {
        // 오늘 날짜라면 수정하기

        // 오늘 날짜가 아니면 새로 데이터 저장하기
        // 신체 정보 저장할 때 체중도 조회해서 같이 가져오기
    }


    private Member validateMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
        return member;
    }

}
