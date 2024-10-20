package com.sideproject.withpt.application.certificate.repository;

import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.domain.trainer.Certificate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CertificateQueryRepository {

    Slice<CertificateResponse> findAllCertificatePageableByTrainerId(Long trainerId, Pageable pageable);

    boolean existAllColumns(Certificate certificateEntity, Long trainerId);

}
