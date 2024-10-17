package com.sideproject.withpt.application.academic.service;

import static com.sideproject.withpt.application.academic.exception.AcademicErrorCode.ACADEMIC_NOT_FOUND;
import static com.sideproject.withpt.application.academic.exception.AcademicErrorCode.DUPLICATE_ACADEMIC;
import static com.sideproject.withpt.application.career.exception.CareerErrorCode.CAREER_NOT_FOUND;

import com.sideproject.withpt.application.academic.controller.request.AcademicEditRequest;
import com.sideproject.withpt.application.academic.exception.AcademicException;
import com.sideproject.withpt.application.academic.repository.AcademicRepository;
import com.sideproject.withpt.application.academic.service.response.AcademicResponse;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.trainer.Academic;
import com.sideproject.withpt.domain.trainer.Trainer;
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
    private final TrainerService trainerService;

    public Slice<AcademicResponse> getAllAcademics(Long trainerId, Pageable pageable) {
        return academicRepository.findAllAcademicPageableByTrainerId(trainerId, pageable);
    }

    public AcademicResponse getAcademic(Long trainerId, Long academicId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        return AcademicResponse.of(
            academicRepository.findByIdAndTrainer(academicId, trainer)
                .orElseThrow(() -> new AcademicException(ACADEMIC_NOT_FOUND))
        );
    }

    @Transactional
    public AcademicResponse saveAcademic(Long trainerId, Academic academic) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        validateDuplicationAllColumn(academic, trainerId);

        trainer.addAcademic(academic);

        return AcademicResponse.of(
            academicRepository.save(academic)
        );
    }

    @Transactional
    public AcademicResponse editAcademic(Long trainerId, AcademicEditRequest request) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

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
        Trainer trainer = trainerService.getTrainerById(trainerId);

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
