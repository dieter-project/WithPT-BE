package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalDateTime;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalTrainingInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ptCount;

    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private PtRegistrationStatus registrationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_training_id")
    private PersonalTraining personalTraining;

    public void addTraining(PersonalTraining personalTraining) {
        this.personalTraining = personalTraining;
    }

}