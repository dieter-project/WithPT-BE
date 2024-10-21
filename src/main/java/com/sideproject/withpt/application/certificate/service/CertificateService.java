package com.sideproject.withpt.application.certificate.service;

import static com.sideproject.withpt.application.certificate.exception.CertificateErrorCode.CERTIFICATE_NOT_FOUND;
import static com.sideproject.withpt.application.certificate.exception.CertificateErrorCode.DUPLICATE_CERTIFICATE;

import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.request.CertificateEditRequest;
import com.sideproject.withpt.application.certificate.exception.CertificateException;
import com.sideproject.withpt.application.certificate.repository.CertificateRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.user.trainer.Certificate;
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
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final TrainerRepository trainerRepository;

    public Slice<CertificateResponse> getAllCertificate(Long trainerId, Pageable pageable) {
        return certificateRepository.findAllCertificatePageableByTrainerId(trainerId, pageable);
    }

    public CertificateResponse getCertificate(Long trainerId, Long certificateId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        return CertificateResponse.of(
            certificateRepository.findByIdAndTrainer(certificateId, trainer)
                .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND))
        );
    }

    @Transactional
    public CertificateResponse saveCertificate(Long trainerId, Certificate certificate) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        validateDuplicationAllColumn(certificate, trainerId);

        trainer.addCertificate(certificate);

        return CertificateResponse.of(
            certificateRepository.save(certificate)
        );
    }

    @Transactional
    public CertificateResponse editCertificate(Long trainerId, CertificateEditRequest request) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Certificate certificate = certificateRepository.findByIdAndTrainer(request.getId(), trainer)
            .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND));

        certificate.editCertificate(
            request.getName(),
            request.getInstitution(),
            request.getAcquisitionYearMonth()
        );

        return CertificateResponse.of(certificate);
    }

    @Transactional
    public void deleteCertificate(Long trainerId, Long certificateId) {
        Trainer trainer = trainerRepository.findById(trainerId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Certificate certificate = certificateRepository.findByIdAndTrainer(certificateId, trainer)
            .orElseThrow(() -> new CertificateException(CERTIFICATE_NOT_FOUND));

        certificateRepository.delete(certificate);
    }

    private void validateDuplicationAllColumn(Certificate certificate, Long trainerId) {
        if(certificateRepository.existAllColumns(certificate, trainerId)) {
            throw new CertificateException(DUPLICATE_CERTIFICATE);
        }
    }
}
