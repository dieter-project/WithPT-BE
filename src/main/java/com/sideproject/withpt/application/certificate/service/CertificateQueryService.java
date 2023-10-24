package com.sideproject.withpt.application.certificate.service;

import static com.sideproject.withpt.application.certificate.exception.CertificateErrorCode.*;

import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.request.CertificateEditRequest;
import com.sideproject.withpt.application.certificate.controller.request.CertificateSaveRequest;
import com.sideproject.withpt.application.certificate.exception.CertificateErrorCode;
import com.sideproject.withpt.application.certificate.exception.CertificateException;
import com.sideproject.withpt.application.certificate.repository.CertificateQueryRepository;
import com.sideproject.withpt.application.certificate.repository.CertificateRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.trainer.Certificate;
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
public class CertificateQueryService {

    private final CertificateQueryRepository certificateQueryRepository;
    private final CertificateRepository certificateRepository;
    private final TrainerService trainerService;

    public Slice<CertificateResponse> getAllCertificate(Long trainerId, Pageable pageable) {
        return certificateQueryRepository.findAllCertificatePageableByTrainerId(trainerId, pageable);
    }

    public CertificateResponse getCertificate(Long trainerId, Long certificateId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        return CertificateResponse.of(
            certificateRepository.findByIdAndTrainer(certificateId, trainer)
                .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND))
        );
    }

    @Transactional
    public CertificateResponse saveCertificate(Long trainerId, CertificateSaveRequest request) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Certificate certificate = request.toEntity();
        validateDuplicationAllColumn(certificate, trainerId);

        trainer.addCertificate(certificate);

        return CertificateResponse.of(
            certificateRepository.save(certificate)
        );
    }

    @Transactional
    public CertificateResponse editCertificate(Long trainerId, CertificateEditRequest request) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Certificate certificate = certificateRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND));

        certificate.editCertificate(
            request.getName(),
            request.getAcquisitionYearMonth()
        );

        return CertificateResponse.of(certificate);
    }

    @Transactional
    public void deleteCertificate(Long trainerId, Long certificateId) {
        Trainer trainer = trainerService.getTrainerById(trainerId);

        Certificate certificate = certificateRepository.findByIdAndTrainer(certificateId, trainer)
            .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND));

        certificateRepository.delete(certificate);
    }

    private void validateDuplicationAllColumn(Certificate certificate, Long trainerId) {
        if(certificateQueryRepository.existAllColumns(certificate, trainerId)) {
            throw new CertificateException(DUPLICATE_CERTIFICATE);
        }
    }
}
