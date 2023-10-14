package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.trainer.convertor.YearMonthToDateConverter;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Setter(AccessLevel.PACKAGE)
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

    @Column(name = "START_OF_WORK_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth startOfWorkYearMonth;

    @Column(name = "END_OF_WORK_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth endOfWorkYearMonth;

}
