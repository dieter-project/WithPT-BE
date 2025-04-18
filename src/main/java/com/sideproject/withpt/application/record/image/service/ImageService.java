package com.sideproject.withpt.application.record.image.service;

import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.image.controller.request.DeleteImageRequest;
import com.sideproject.withpt.application.record.image.service.response.ImageInfoResponse;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.domain.user.member.Member;
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
public class ImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;

    public Slice<ImageInfoResponse> findAllImage(Long memberId, LocalDate uploadDate, UsageType usageType, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        return imageRepository.findAllByMemberAndUsagesAndUploadDate(member, usageType, uploadDate, pageable);
    }

    @Transactional
    public void saveImage(List<MultipartFile> files, LocalDate uploadDate, Long memberId, UsageType usageType) {

        if (files == null || files.size() == 0) {
            throw GlobalException.EMPTY_FILE;
        }
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        imageUploader.uploadAndSaveImages(files, usageType, uploadDate, member);
    }

    @Transactional
    public void deleteImage(Long memberId, DeleteImageRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        imageRepository.findAllByMemberAndIdIn(member, request.getImageIds())
            .forEach(imageUploader::deleteImage);
    }
}
