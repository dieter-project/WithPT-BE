package com.sideproject.withpt.application.body.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.body.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.body.exception.BodyException;
import com.sideproject.withpt.application.body.repository.BodyRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;

    public WeightInfoResponse findWeightInfo(Long memberId, LocalDateTime dateTime) {
        validateMemberId(memberId);
        Body body = bodyRepository
                .findTop1ByMemberIdAndWeightRecordDateBeforeOrderByWeightRecordDateDesc(memberId, dateTime)
                .orElseThrow(() -> BodyException.WEIGHT_NOT_EXIST);

        return WeightInfoResponse.from(body);
    }

    @Transactional
    public void saveWeight(Long memberId, double weight, LocalDateTime dateTime) {
        Member member = validateMemberId(memberId);

        bodyRepository
                .findTop1ByMemberIdAndWeightRecordDateBeforeOrderByWeightRecordDateDesc(memberId, dateTime)
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기

                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기

                        });

        member.changeWeight(weight);
    }

    @Transactional
    public void saveBodyInfo(Long memberId, BodyInfoRequest request, LocalDateTime dateTime) {
        validateMemberId(memberId);

        bodyRepository
                .findTop1ByMemberIdAndWeightRecordDateBeforeOrderByWeightRecordDateDesc(memberId, dateTime)
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기

                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기

                        });

    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

}
