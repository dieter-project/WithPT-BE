package com.sideproject.withpt.application.certificate.repository;

import static com.sideproject.withpt.domain.trainer.QCertificate.certificate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.withpt.application.certificate.controller.reponse.CertificateResponse;
import com.sideproject.withpt.application.certificate.controller.reponse.QCertificateResponse;
import com.sideproject.withpt.domain.trainer.Certificate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CertificateQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Slice<CertificateResponse> findAllCertificatePageableByTrainerId(Long trainerId, Pageable pageable) {

        List<CertificateResponse> certificateResponseList = jpaQueryFactory
            .select(
                new QCertificateResponse(
                    certificate.id,
                    certificate.name,
                    certificate.acquisitionYearMonth
                )
            )
            .from(certificate)
            .where(certificate.trainer.id.eq(trainerId))
            .orderBy(certificate.acquisitionYearMonth.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        List<CertificateResponse> content = new ArrayList<>(certificateResponseList);

        boolean hasNext = false;

        if(content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public boolean existAllColumns(Certificate certificateEntity, Long trainerId) {

        Integer fetchOne = jpaQueryFactory
            .selectOne()
            .from(certificate)
            .where(
                certificate.trainer.id.eq(trainerId),
                nameEq(certificateEntity.getName()),
                acquisitionYearMonthEq(certificateEntity.getAcquisitionYearMonth())
            ).fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? certificate.name.eq(name) : null;
    }

    private BooleanExpression acquisitionYearMonthEq(YearMonth acquisitionYearMonth) {
        return ObjectUtils.isEmpty(acquisitionYearMonth) ? null : certificate.acquisitionYearMonth.eq(acquisitionYearMonth);
    }
}
