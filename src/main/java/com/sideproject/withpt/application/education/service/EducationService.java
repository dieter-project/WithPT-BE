package com.sideproject.withpt.application.education.service;

import static com.sideproject.withpt.application.education.exception.EducationErrorCode.DUPLICATE_EDUCATION;
import static com.sideproject.withpt.application.education.exception.EducationErrorCode.EDUCATION_NOT_FOUND;

import com.sideproject.withpt.application.education.service.reponse.EducationResponse;
import com.sideproject.withpt.application.education.controller.request.EducationEditRequest;
import com.sideproject.withpt.application.education.exception.EducationException;
import com.sideproject.withpt.application.education.repository.EducationRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.trainer.Education;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationService {

    private final EducationRepository educationRepository;
    private final TrainerRepository trainerRepository;

    public Slice<EducationResponse> getAllEducations(Long trainerId, Pageable pageable) {
        return educationRepository.findAllEducationPageableByTrainerId(trainerId, pageable);
    }

    public EducationResponse getEducation(Long trainerId, Long educationId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return EducationResponse.of(
            educationRepository.findByIdAndTrainer(educationId, trainer)
                .orElseThrow(() -> new EducationException(EDUCATION_NOT_FOUND))
        );
    }

    @Transactional
    public EducationResponse saveEducation(Long trainerId, Education education) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        validateDuplicationAllColumn(education, trainerId);

        trainer.addEducation(education);

        return EducationResponse.of(
            educationRepository.save(education)
        );
    }

    @Transactional
    public EducationResponse editEducation(Long trainerId, EducationEditRequest request) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Education education = educationRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new EducationException(EDUCATION_NOT_FOUND));

        education.editEducation(
            request.getName(),
            request.getInstitution(),
            request.getAcquisitionYearMonth()
        );

        return EducationResponse.of(education);
    }

    @Transactional
    public void deleteEducation(Long trainerId, Long educationId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Education education = educationRepository.findByIdAndTrainer(educationId, trainer)
            .orElseThrow(() -> new EducationException(EDUCATION_NOT_FOUND));

        educationRepository.delete(education);
    }

    private void validateDuplicationAllColumn(Education education, Long trainerId) {
        if (educationRepository.existAllColumns(education, trainerId)) {
            throw new EducationException(DUPLICATE_EDUCATION);
        }
    }
}
