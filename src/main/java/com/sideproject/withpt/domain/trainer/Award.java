package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.trainer.convertor.YearToShortConverter;
import java.time.Year;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter(AccessLevel.PACKAGE)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Award extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private String name;

    private String institution;

    @Convert(converter = YearToShortConverter.class)
    private Year acquisitionYear;

    public void editAward(String name, String institution, Year acquisitionYear) {
        this.name = name;
        this.institution = institution;
        this.acquisitionYear = acquisitionYear;
    }
}
