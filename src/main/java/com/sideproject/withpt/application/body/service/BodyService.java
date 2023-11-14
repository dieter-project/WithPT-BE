package com.sideproject.withpt.application.body.service;

import com.sideproject.withpt.application.body.dto.request.WeightInfoRequest;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.body.dto.request.BodyInfoRequest;
import com.sideproject.withpt.application.body.dto.response.WeightInfoResponse;
import com.sideproject.withpt.application.body.exception.BodyException;
import com.sideproject.withpt.application.body.repository.BodyRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    public WeightInfoResponse findWeightInfo(Long memberId, LocalDate dateTime) {
        validateMemberId(memberId);

        Body body = bodyRepository
                .findRecentBodyInfo(memberId, dateTime)
                .orElseThrow(() -> BodyException.BODY_NOT_EXIST);

        return WeightInfoResponse.from(body);
    }

    @Transactional
    public void saveWeight(Long memberId, WeightInfoRequest request) {
        Member member = validateMemberId(memberId);

        bodyRepository
                .findTodayBodyInfo(memberId, request.getBodyRecordDate())
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기
                            value.changeWeight(request.getWeight());
                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                            bodyRepository
                                    .findRecentBodyInfo(memberId, request.getBodyRecordDate())
                                    .ifPresentOrElse(
                                            body -> {
                                                body.changeWeight(request.getWeight());
                                                bodyRepository.save(request.toBodyEntity(member, body));
                                            },
                                            () -> {
                                                bodyRepository.save(request.toEntity(member));
                                            });
                        });

        member.changeWeight(request.getWeight());
    }

    @Transactional
    public void saveBodyInfo(Long memberId, BodyInfoRequest request) {
        Member member = validateMemberId(memberId);

        bodyRepository
                .findTodayBodyInfo(memberId, request.getBodyRecordDate())
                .ifPresentOrElse(
                        value -> {
                            // 오늘 날짜 기록이 존재한다면 기록 수정하기
                            value.updateBodyInfo(request);
                        },
                        () -> {
                            // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                            bodyRepository
                                    .findRecentBodyInfo(memberId, request.getBodyRecordDate())
                                    .ifPresentOrElse(
                                            body -> {
                                                body.updateBodyInfo(request);
                                                bodyRepository.save(request.toBodyEntity(member, body));
                                            },
                                            () -> {
                                                bodyRepository.save(request.toEntity(member));
                                            });
                        });
    }

    public void findAllBodyImage(Long memberId) {

    }

    public void findTodayBodyImage(Long memberId, String dateTime) {

    }

    @Transactional
    public void saveBodyImage(List<MultipartFile> file, String dateTime, Long memberId) {
        Member member = validateMemberId(memberId);

        if(file != null && file.size() > 0) {
            imageUploader.uploadAndSaveImages(file, LocalDate.parse(dateTime), Usages.BODY, member);
        }
    }

    @Transactional
    public void deleteBodyImage(Long imageId) {
        imageUploader.deleteImage(imageId);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

}
