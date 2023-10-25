package com.sideproject.withpt.application.certificate.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.request.CertificateEditRequest;
import com.sideproject.withpt.application.certificate.controller.request.CertificateSaveRequest;
import com.sideproject.withpt.application.certificate.repository.CertificateQueryRepository;
import com.sideproject.withpt.application.certificate.repository.CertificateRepository;
import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.domain.trainer.Certificate;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class CertificateQueryServiceTest {

    @Mock
    CertificateQueryRepository certificateQueryRepository;

    @Mock
    CertificateRepository certificateRepository;

    @Mock
    TrainerService trainerService;

    @InjectMocks
    CertificateQueryService certificateQueryService;

    @Test
    public void getAll() {
        //given
        Long trainerId = 1L;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);

        List<CertificateResponse> content = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            content.add(createResponse((long) i, 2020 + i, 1 + i));
        }

        SliceImpl<CertificateResponse> testContent = new SliceImpl<>(content, pageRequest, false);
        given(certificateQueryRepository.findAllCertificatePageableByTrainerId(trainerId, pageRequest))
            .willReturn(testContent);

        //when
        Slice<CertificateResponse> result = certificateQueryService.getAllCertificate(trainerId, pageRequest);

        //then
        assertThat(result.getSize()).isEqualTo(size);
        assertThat(result.getContent()).isEqualTo(content);
    }

    @Test
    public void getCertificate() {
        //given
        Long trainerId = 1L;
        Long certificateId = 10L;

        Trainer trainer = createTrainer(trainerId);
        Certificate certificate = createCertificate(certificateId, 2023, 10);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(certificateRepository.findByIdAndTrainer(certificateId, trainer))
            .willReturn(Optional.of(certificate));

        //when
        CertificateResponse response = certificateQueryService.getCertificate(trainerId, certificateId);

        //then
        assertThat(response.getName()).isEqualTo(certificate.getName());
        assertThat(response.getAcquisitionYearMonth()).isEqualTo(certificate.getAcquisitionYearMonth());
    }

    @Test
    public void save() {
        //given
        Long trainerId = 1L;
        Long certificateId = 10L;

        Certificate certificate = createCertificate(certificateId, 2023, 11);

        CertificateSaveRequest saveRequest = CertificateSaveRequest.builder()
            .name(certificate.getName())
            .acquisitionYearMonth("2023-11")
            .build();

        Trainer trainer = createTrainer(trainerId);

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(certificateQueryRepository.existAllColumns(any(Certificate.class), eq(trainerId))).willReturn(false);
        given(certificateRepository.save(any(Certificate.class))).willReturn(certificate);

        //when
        CertificateResponse response = certificateQueryService.saveCertificate(trainerId, saveRequest);

        //then
        assertThat(response.getName()).isEqualTo(saveRequest.getName());
        assertThat(response.getAcquisitionYearMonth()).isEqualTo(saveRequest.getAcquisitionYearMonth());
    }

    @Test
    public void editCertificate() {
        //given
        Long trainerId = 1L;
        Long certificateId = 10L;

        Certificate certificate = createCertificate(certificateId, 2023, 11);
        Trainer trainer = createTrainer(trainerId);

        CertificateEditRequest editRequest = CertificateEditRequest.builder()
            .id(certificateId)
            .name(certificate.getName())
            .acquisitionYearMonth("2023-11")
            .build();

        given(trainerService.getTrainerById(trainerId)).willReturn(trainer);
        given(certificateRepository.findByIdAndTrainer(certificateId, trainer)).willReturn(Optional.of(certificate));

        //when
        CertificateResponse response = certificateQueryService.editCertificate(trainerId, editRequest);

        //then
        assertThat(response.getName()).isEqualTo(editRequest.getName());
    }

    public CertificateResponse createResponse(Long certificateId, int year, int month) {
        return CertificateResponse.builder()
            .id(certificateId)
            .name("test" + certificateId)
            .acquisitionYearMonth(YearMonth.of(year, month))
            .build();
    }

    private Trainer createTrainer(Long trainerId) {
        return Trainer.builder()
            .id(trainerId)
            .name("Trainer" + trainerId)
            .certificates(new ArrayList<>())
            .build();
    }

    private Certificate createCertificate(Long certificateId, int year, int month) {
        return Certificate.builder()
            .id(certificateId)
            .name("test" + certificateId)
            .acquisitionYearMonth(YearMonth.of(year, month))
            .build();
    }
}
