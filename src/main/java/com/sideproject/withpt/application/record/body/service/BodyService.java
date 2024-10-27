package com.sideproject.withpt.application.record.body.service;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.body.controller.request.BodyInfoRequest;
import com.sideproject.withpt.application.record.body.controller.request.WeightInfoRequest;
import com.sideproject.withpt.application.record.body.service.response.WeightInfoResponse;
import com.sideproject.withpt.application.record.body.repository.BodyRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;

    public WeightInfoResponse findWeightInfo(Long memberId, LocalDate uploadDate) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        List<Body> bodies = bodyRepository.findLatestWeightsBy(member, uploadDate);
        Optional<Body> optionalBody = bodyRepository.findLatestBodyInfoBy(member, uploadDate);

        return WeightInfoResponse.from(bodies, optionalBody);
    }

    @Transactional
    public void saveOrUpdateWeight(Long memberId, WeightInfoRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        bodyRepository.findTodayBodyInfo(member, request.getUploadDate())
            .ifPresentOrElse(
                body -> {
                    // 오늘 날짜 기록이 이미 존재한다면 체중 기록만 수정하기
                    body.changeWeight(request.getWeight());
                },
                () -> {
                    // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                    bodyRepository.save(request.toEntity(member));
                });

        member.changeCurrentWeight(request.getWeight());
    }

    @Transactional
    public void saveOrUpdateBodyInfo(Long memberId, BodyInfoRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        bodyRepository
            .findTodayBodyInfo(member, request.getUploadDate())
            .ifPresentOrElse(
                body -> {
                    // 오늘 날짜 기록이 존재한다면 기록 수정하기
                    body.updateBodyInfo(request.getSkeletalMuscle(), request.getBodyFatPercentage(), request.getBmi());
                },
                () -> {
                    // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                    bodyRepository.save(request.toEntity(member));
                });
    }
}
