package com.sideproject.withpt.application.academic.service;

import static com.sideproject.withpt.application.academic.exception.AcademicErrorCode.ACADEMIC_NOT_FOUND;
import static com.sideproject.withpt.application.academic.exception.AcademicErrorCode.DUPLICATE_ACADEMIC;
import static com.sideproject.withpt.application.career.exception.CareerErrorCode.CAREER_NOT_FOUND;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.exception.AcademicException;
import com.sideproject.withpt.application.academic.repository.AcademicRepository;
import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.trainer.Academic;
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
public class AcademicService {

    private final AcademicRepository academicRepository;
    private final TrainerRepository trainerRepository;

    public Slice<AcademicResponse> getAllAcademics(Long trainerId, Pageable pageable) {
        return academicRepository.findAllAcademicPageableByTrainerId(trainerId, pageable);
    }

    public AcademicResponse getAcademic(Long trainerId, Long academicId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return AcademicResponse.of(
            academicRepository.findByIdAndTrainer(academicId, trainer)
                .orElseThrow(() -> new AcademicException(ACADEMIC_NOT_FOUND))
        );
    }

    @Transactional
    public AcademicResponse saveAcademic(Long trainerId, Academic academic) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        validateDuplicationAllColumn(academic, trainerId);

        trainer.addAcademic(academic);

        return AcademicResponse.of(
            academicRepository.save(academic)
        );
    }

    @Transactional
    public AcademicResponse editAcademic(Long trainerId, AcademicEditRequest request) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Academic academic = academicRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND));

        academic.editAcademic(
            request.getName(),
            request.getInstitution(),
            request.getMajor(),
            request.getDegree(),
            request.getCountry(),
            request.getEnrollmentYearMonth(),
            request.getGraduationYearMonth()
        );

        return AcademicResponse.of(academic);
    }

    @Transactional
    public void deleteAcademic(Long trainerId, Long academicId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Academic academic = academicRepository.findByIdAndTrainer(academicId, trainer)
            .orElseThrow(() -> new AcademicException(ACADEMIC_NOT_FOUND));

        academicRepository.delete(academic);
    }


    private void validateDuplicationAllColumn(Academic academic, Long trainerId) {
        if (academicRepository.existAllColumns(academic, trainerId)) {
            throw new AcademicException(DUPLICATE_ACADEMIC);
        }
    }

}
