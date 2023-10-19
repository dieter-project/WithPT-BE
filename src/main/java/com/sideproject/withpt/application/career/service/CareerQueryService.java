package com.sideproject.withpt.application.career.service;

import static com.sideproject.withpt.application.career.exception.CareerErrorCode.CAREER_NOT_FOUND;
import static com.sideproject.withpt.application.career.exception.CareerErrorCode.DUPLICATE_CAREER;

import com.sideproject.withpt.application.career.controller.request.CareerEditRequest;
import com.sideproject.withpt.application.career.controller.response.CareerResponse;
import com.sideproject.withpt.application.career.exception.CareerException;
import com.sideproject.withpt.application.career.repository.CareerQueryRepository;
import com.sideproject.withpt.application.career.repository.CareerRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.domain.trainer.Career;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.YearMonth;
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
public class CareerQueryService {

    private final CareerQueryRepository careerQueryRepository;
    private final CareerRepository careerRepository;
    private final TrainerService trainerService;

    public Slice<CareerResponse> getAllCareers(Long trainerId, Pageable pageable) {
        return careerQueryRepository.findAllCareerPageableByTrainerId(trainerId, pageable);
    }

    public CareerResponse getCareer(Long trainerId, Long careerId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        return CareerResponse.of(
            careerRepository.findByIdAndTrainer(careerId, trainer)
                .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND))
        );
    }

    @Transactional
    public CareerResponse saveCareer(Long trainerId, CareerDto careerDto) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Career career = careerDto.toEntity();
        validateDuplicationAllColumn(career, trainerId);

        trainer.addCareer(career);

        return CareerResponse.of(
            careerRepository.save(career)
        );
    }

    @Transactional
    public CareerResponse editCareer(Long trainerId, CareerEditRequest request) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Career career = careerRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND));

        career.editCareer(
            request.getCenterName(),
            request.getJobPosition(),
            YearMonth.parse(request.getStartOfWorkYearMonth()),
            YearMonth.parse(request.getEndOfWorkYearMonth())
        );

        return CareerResponse.of(career);
    }

    @Transactional
    public void deleteCareer(Long trainerId, Long careerId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Career career = careerRepository.findByIdAndTrainer(careerId, trainer)
            .orElseThrow(() -> new CareerException(CAREER_NOT_FOUND));

        careerRepository.delete(career);
    }

    private void validateDuplicationAllColumn(Career career, Long trainerId) {
        if (careerQueryRepository.existAllColumns(career, trainerId)) {
            throw new CareerException(DUPLICATE_CAREER);
        }
    }
}
