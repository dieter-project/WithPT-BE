package com.sideproject.withpt.application.trainer.service;

import com.sideproject.withpt.application.trainer.controller.request.InfoEditRequest;
import com.sideproject.withpt.application.trainer.controller.response.TrainerInfoResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.common.utils.constants.AwsS3Constants;
import com.sideproject.withpt.domain.trainer.Trainer;
import io.jsonwebtoken.lang.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final AwsS3Uploader awsS3Uploader;

    public Trainer getTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    public TrainerInfoResponse getTrainerInfo(Long trainerId) {
        return TrainerInfoResponse.of(
            this.getTrainerById(trainerId)
        );
    }

    @Transactional
    public void editTrainerInfo(Long trainerId, InfoEditRequest request, MultipartFile file) {
        Trainer trainer = getTrainerById(trainerId);

        String imageUrl = trainer.getImageUrl();

        if(isNotEmptyFile(file)) {
            imageUrl = saveProfileImage(trainerId, file);
        }

        trainer.editTrainerProfile(
            imageUrl,
            request.getName(), request.getBirth(), request.getSex()
        );
    }

    private String saveProfileImage(Long trainerId, MultipartFile file) {
        return awsS3Uploader.upload(AwsS3Constants.PROFILE_PATH, trainerId.toString(), file);
    }

    private boolean isNotEmptyFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }
}
