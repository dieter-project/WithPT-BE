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
                .findRecentBodyInfo(memberId, dateTime)
                .orElseThrow(() -> BodyException.BODY_NOT_EXIST);

        return WeightInfoResponse.from(body);
    }

    @Transactional
    public void saveWeight(Long memberId, double weight, LocalDateTime dateTime) {
        Member member = validateMemberId(memberId);

        bodyRepository
                .findTodayBodyInfo(memberId, dateTime)
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기
                            value.changeWeight(weight);
                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                            Body body = bodyRepository
                                    .findRecentBodyInfo(memberId, dateTime)
                                    .orElseThrow(() -> BodyException.BODY_NOT_EXIST);

                            body.changeWeight(weight);
                            bodyRepository.save(body);
                        });

        member.changeWeight(weight);
    }

    @Transactional
    public void saveBodyInfo(Long memberId, BodyInfoRequest request) {
        validateMemberId(memberId);

        bodyRepository
                .findTodayBodyInfo(memberId, request.getBodyRecordDate())
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기
                            value.updateBodyInfo(request);
                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                            Body body = bodyRepository
                                    .findRecentBodyInfo(memberId, request.getBodyRecordDate())
                                    .orElseThrow(() -> BodyException.BODY_NOT_EXIST);

                            body.updateBodyInfo(request);
                            bodyRepository.save(body);
                        });
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

}
