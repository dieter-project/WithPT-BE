package com.sideproject.withpt.application.record.exercise.service;

import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.body.controller.request.DeleteBodyImageRequest;
import com.sideproject.withpt.application.record.body.controller.response.BodyImageInfoResponse;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.Usages;
import com.sideproject.withpt.domain.member.Member;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseImageService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ImageUploader imageUploader;

    public Slice<BodyImageInfoResponse> findAllBodyImage(Long memberId, LocalDate uploadDate, Pageable pageable) {
        try {
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
            return imageRepository.findAllByMemberAndUsagesAndUploadDate(pageable, member, Usages.EXERCISE, uploadDate);
        } catch (Exception e) {
            throw GlobalException.EMPTY_FILE;
        }
    }

    @Transactional
    public void deleteBodyImage(Long memberId, DeleteBodyImageRequest request) {
        for (long id : request.getImageIds()) {
            imageUploader.deleteImage(id);
        }
    }
}
