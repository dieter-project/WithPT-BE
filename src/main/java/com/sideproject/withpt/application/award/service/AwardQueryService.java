package com.sideproject.withpt.application.award.service;

import static com.sideproject.withpt.application.award.exception.AwardErrorCode.*;

import com.sideproject.withpt.application.award.controller.reponse.AwardResponse;
import com.sideproject.withpt.application.award.controller.request.AwardEditRequest;
import com.sideproject.withpt.application.award.exception.AwardException;
import com.sideproject.withpt.application.award.repository.AwardQueryRepository;
import com.sideproject.withpt.application.award.repository.AwardRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.trainer.Award;
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
public class AwardQueryService {

    private final AwardQueryRepository awardQueryRepository;
    private final AwardRepository awardRepository;
    private final TrainerService trainerService;

    public Slice<AwardResponse> getAllAwards(Long trainerId, Pageable pageable) {
        return awardQueryRepository.findAllAwardPageableByTrainerId(trainerId, pageable);
    }

    public AwardResponse getAward(Long trainerId, Long awardId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        return AwardResponse.of(
            awardRepository.findByIdAndTrainer(awardId, trainer)
                .orElseThrow(() -> new AwardException(AWARD_NOT_FOUND))
        );
    }

    @Transactional
    public AwardResponse saveAward(Long trainerId, Award award) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        validateDuplicationAllColumn(award, trainerId);

        trainer.addAward(award);

        return AwardResponse.of(
            awardRepository.save(award)
        );
    }

    @Transactional
    public AwardResponse editAward(Long trainerId, AwardEditRequest request) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Award award = awardRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new AwardException(AWARD_NOT_FOUND));

        award.editAward(
            request.getName(),
            request.getInstitution(),
            request.getAcquisitionYearMonth()
        );

        return AwardResponse.of(award);
    }

    @Transactional
    public void deleteAward(Long trainerId, Long awardId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Award award = awardRepository.findByIdAndTrainer(awardId, trainer)
            .orElseThrow(() -> new AwardException(AWARD_NOT_FOUND));

        awardRepository.delete(award);
    }

    private void validateDuplicationAllColumn(Award award, Long trainerId) {
        if(awardQueryRepository.existAllColumns(award, trainerId)) {
            throw new AwardException(DUPLICATE_AWARD);
        }
    }
}
