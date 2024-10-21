package com.sideproject.withpt.domain.user.trainer;

import com.sideproject.withpt.common.type.EmploymentStatus;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.common.convertor.YearMonthToDateConverter;
import java.time.YearMonth;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter(AccessLevel.PACKAGE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private String centerName;

    private String jobPosition;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    @Column(name = "START_OF_WORK_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth startOfWorkYearMonth;

    @Column(name = "END_OF_WORK_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth endOfWorkYearMonth;

    public void editCareer(String centerName, String jobPosition, EmploymentStatus status, YearMonth startOfWorkYearMonth, YearMonth endOfWorkYearMonth) {
        this.centerName = centerName;
        this.jobPosition = jobPosition;
        this.status = status;
        this.startOfWorkYearMonth = startOfWorkYearMonth;
        this.endOfWorkYearMonth = endOfWorkYearMonth;
    }

}
