package com.sideproject.withpt.application.record.body.service;

import com.sideproject.withpt.application.record.body.controller.request.BodyInfoRequest;
import com.sideproject.withpt.application.record.body.controller.request.DeleteBodyImageRequest;
import com.sideproject.withpt.application.record.body.controller.request.WeightInfoRequest;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.application.record.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.application.record.body.repository.BodyRepository;
import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    private final ImageUploader imageUploader;

    public WeightInfoResponse findWeightInfo(Long memberId, LocalDate dateTime) {
        Member member = validateMemberId(memberId);

        return bodyRepository
            .findRecentBodyInfo(member, dateTime);
    }

    @Transactional
    public void saveWeight(Long memberId, WeightInfoRequest request) {
        Member member = validateMemberId(memberId);

        bodyRepository
            .findTodayBodyInfo(member, request.getUploadDate())
            .ifPresentOrElse(
                body -> {
                    // 오늘 날짜 기록이 이미 존재한다면 체중 기록만 수정하기
                    body.changeWeight(request.getWeight());
                },
                () -> {
                    // 오늘 날짜 기록이 없다면 새로 기록 저장하기
                    bodyRepository.save(request.toEntity(member));
                });

        member.changeWeight(request.getWeight());
    }

    @Transactional
    public void saveBodyInfo(Long memberId, BodyInfoRequest request) {
        Member member = validateMemberId(memberId);

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

    public Slice<BodyImageInfoResponse> findAllBodyImage(Long memberId, LocalDate uploadDate, Pageable pageable) {
        try {
            Member member = validateMemberId(memberId);
            return imageRepository.findAllByMemberAndUsagesAndUploadDate(pageable, member, Usages.BODY, uploadDate);
        } catch (Exception e) {
            throw GlobalException.EMPTY_FILE;
        }
    }

    @Transactional
    public void saveBodyImage(List<MultipartFile> files, LocalDate uploadDate, Long memberId) {

        if (files == null || files.size() == 0) {
            throw GlobalException.EMPTY_FILE;
        }
        Member member = validateMemberId(memberId);
        imageUploader.uploadAndSaveImages(files, Usages.BODY, uploadDate, member);
    }

    @Transactional
    public void deleteBodyImage(Long memberId, DeleteBodyImageRequest request) {
        for (long id : request.getImageIds()) {
            imageUploader.deleteImage(id);
        }
//        imageUploader.deleteImage(url);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

}
