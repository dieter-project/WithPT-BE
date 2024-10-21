package com.sideproject.withpt.application.certificate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.request.CertificateEditRequest;
import com.sideproject.withpt.application.certificate.exception.CertificateException;
import com.sideproject.withpt.application.certificate.repository.CertificateRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.domain.user.trainer.Certificate;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class CertificateServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService certificateService;

    @DisplayName("트레이너 모든 자격증 조회")
    @Test
    void getAllCertificate() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        certificateRepository.saveAll(List.of(
            createCertificate("자격증1", trainer),
            createCertificate("자격증2", trainer)
        ));
        Long trainerId = trainer.getId();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<CertificateResponse> responses = certificateService.getAllCertificate(trainerId, pageable);

        // then
        assertThat(responses.getContent()).hasSize(2)
            .extracting("name")
            .contains("자격증1", "자격증2");
    }

    @DisplayName("트레이너 자격증 단건 조회")
    @Test
    void getCertificate() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        List<Certificate> certificates = certificateRepository.saveAll(List.of(
            createCertificate("자격증1", trainer),
            createCertificate("자격증2", trainer)
        ));

        Long trainerId = trainer.getId();
        Long certificateId = certificates.get(1).getId();

        // when
        CertificateResponse response = certificateService.getCertificate(trainerId, certificateId);

        // then
        assertThat(response.getName()).isEqualTo("자격증2");
    }

    @DisplayName("이미 입력된 자격증 리스트에 트레이너 자격증을 추가할 수 있다.")
    @Test
    void saveCertificate() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        certificateRepository.saveAll(List.of(
            createCertificate("자격증1", trainer),
            createCertificate("자격증2", trainer)
        ));

        Certificate certificate = Certificate.builder()
            .name("자격증3")
            .institution("기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 3))
            .build();

        Long trainerId = trainer.getId();

        // when
        CertificateResponse response = certificateService.saveCertificate(trainerId, certificate);

        // then
        assertThat(response.getName()).isEqualTo("자격증3");

        List<Certificate> certificates = certificateRepository.findAll();
        assertThat(certificates).hasSize(3);
    }

    @DisplayName("자격증 리스트가 비어있을 때 트레이너 자격증을 추가할 수 있다.")
    @Test
    void saveCertificateWhenCertificatesEmpty() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        Certificate certificate = Certificate.builder()
            .name("자격증3")
            .institution("기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 3))
            .build();

        Long trainerId = trainer.getId();

        // when
        CertificateResponse response = certificateService.saveCertificate(trainerId, certificate);

        // then
        assertThat(response.getName()).isEqualTo("자격증3");

        List<Certificate> certificates = certificateRepository.findAll();
        assertThat(certificates).hasSize(1);
    }

    @DisplayName("이미 동일한 자격증이 있으면 저장할 수 없다.")
    @Test
    void validateDuplicationAllColumn() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        certificateRepository.saveAll(List.of(
            createCertificate("자격증1", trainer),
            createCertificate("자격증2", trainer)
        ));

        Certificate certificate = Certificate.builder()
            .name("자격증2")
            .institution("기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 3))
            .build();

        Long trainerId = trainer.getId();

        // when // then
        assertThatThrownBy(() -> certificateService.saveCertificate(trainerId, certificate))
            .isInstanceOf(CertificateException.class)
            .hasMessage("이미 동일한 자격증이 존재합니다.");
    }

    @DisplayName("자격증 수정")
    @Test
    void editCertificate() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        List<Certificate> certificates = certificateRepository.saveAll(List.of(
            createCertificate("자격증1", trainer),
            createCertificate("자격증2", trainer)
        ));

        CertificateEditRequest request = CertificateEditRequest.builder()
            .id(certificates.get(1).getId())
            .name("수정된 자격증명")
            .institution("기관명")
            .acquisitionYearMonth("2023-03")
            .build();

        Long trainerId = trainer.getId();

        // when
        CertificateResponse response = certificateService.editCertificate(trainerId, request);

        // then
        assertThat(response.getName()).isEqualTo("수정된 자격증명");
    }

    @DisplayName("경력 삭제")
    @Test
    void deleteCertificate() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Certificate certificate = certificateRepository.save(createCertificate("자격증2", trainer));

        Long trainerId = trainer.getId();
        Long certificateId = certificate.getId();

        // when
        certificateService.deleteCertificate(trainerId, certificateId);

        // then
        Optional<Certificate> result = certificateRepository.findById(certificateId);
        assertThat(result).isEmpty();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private Certificate createCertificate(String name, Trainer trainer) {
        return Certificate.builder()
            .trainer(trainer)
            .name(name)
            .institution("기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 3))
            .build();
    }
}