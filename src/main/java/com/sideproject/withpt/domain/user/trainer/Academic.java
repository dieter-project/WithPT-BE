package com.sideproject.withpt.domain.user.trainer;

import com.sideproject.withpt.common.convertor.YearMonthToDateConverter;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.domain.BaseEntity;
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
public class Academic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private String name;

    @Enumerated(EnumType.STRING)
    private AcademicInstitution institution;

    private String major;

    @Enumerated(EnumType.STRING)
    private Degree degree;

    private String country;

    @Column(name = "EMROLLMENT_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth enrollmentYearMonth;

    @Column(name = "GRADUATION_YEAR_MONTH", columnDefinition = "date")
    @Convert(converter = YearMonthToDateConverter.class)
    private YearMonth graduationYearMonth;

    public void editAcademic(String name, AcademicInstitution institution, String major, Degree degree, String country, YearMonth enrollmentYearMonth, YearMonth graduationYearMonth) {
        this.name = name;
        this.institution = institution;
        this.major = major;
        this.degree = degree;
        this.country = country;
        this.enrollmentYearMonth = enrollmentYearMonth;
        this.graduationYearMonth = graduationYearMonth;
    }
}
