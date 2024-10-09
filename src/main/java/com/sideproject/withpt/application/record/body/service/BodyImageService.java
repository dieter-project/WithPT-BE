package com.sideproject.withpt.application.record.body.service;

import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.body.controller.request.DeleteBodyImageRequest;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.common.type.Usages;
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
public class BodyImageService {

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;

    public Slice<BodyImageInfoResponse> findAllBodyImage(Long memberId, LocalDate uploadDate, Pageable pageable) {
        try {
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
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
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        imageUploader.uploadAndSaveImages(files, Usages.BODY, uploadDate, member);
    }

    @Transactional
    public void deleteBodyImage(Long memberId, DeleteBodyImageRequest request) {
        for (long id : request.getImageIds()) {
            imageUploader.deleteImage(id);
        }
//        imageUploader.deleteImage(url);
    }
}
