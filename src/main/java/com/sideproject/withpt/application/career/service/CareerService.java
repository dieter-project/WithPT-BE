package com.sideproject.withpt.application.career.service;

import static com.sideproject.withpt.application.career.exception.CareerErrorCode.CAREER_NOT_FOUND;
import static com.sideproject.withpt.application.career.exception.CareerErrorCode.DUPLICATE_CAREER;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.service.response.CareerResponse;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.career.repository.CareerRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.trainer.Career;
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
public class CareerService {

    private final CareerRepository careerRepository;
    private final TrainerRepository trainerRepository;

    public Slice<CareerResponse> getAllCareers(Long trainerId, Pageable pageable) {
        return careerRepository.findAllCareerPageableByTrainerId(trainerId, pageable);
    }

    public CareerResponse getCareer(Long trainerId, Long careerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return CareerResponse.of(
            careerRepository.findByIdAndTrainer(careerId, trainer)
                .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND))
        );
    }

    @Transactional
    public CareerResponse saveCareer(Long trainerId, Career career) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        validateDuplicationAllColumn(career, trainerId);

        trainer.addCareer(career);

        return CareerResponse.of(
            careerRepository.save(career)
        );
    }

    @Transactional
    public CareerResponse editCareer(Long trainerId, CareerEditRequest request) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Career career = careerRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND));

        career.editCareer(
            request.getCenterName(),
            request.getJobPosition(),
            request.getStatus(),
            request.getStartOfWorkYearMonth(),
            request.getEndOfWorkYearMonth()
        );

        return CareerResponse.of(career);
    }

    @Transactional
    public void deleteCareer(Long trainerId, Long careerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Career career = careerRepository.findByIdAndTrainer(careerId, trainer)
            .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND));

        careerRepository.delete(career);
    }

    private void validateDuplicationAllColumn(Career career, Long trainerId) {
        if (careerRepository.existAllColumns(career, trainerId)) {
            throw new CareerException(DUPLICATE_CAREER);
        }
    }
}
