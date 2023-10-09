package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class PersonalTraining extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToMany(mappedBy = "personalTraining")
    private List<PersonalTrainingInfo> personalTrainingInfos = new ArrayList<>();

    private int totalPtCount;

    private int remainingPtCount;

    private String note;

    private LocalDateTime firstRegistrationDate;

    private LocalDateTime lastRegistrationDate;

    private LocalDateTime registrationRequestDate;

    @Enumerated(EnumType.STRING)
    private PtRegistrationStatus registrationStatus;

    @Enumerated(EnumType.STRING)
    private PTInfoInputStatus infoInputStatus;

    @Enumerated(EnumType.STRING)
    private PtRegistrationAllowedStatus registrationAllowedStatus;

    public void inputPtRegistrationInfo(PersonalTrainingInfo trainingInfo) {
        this.personalTrainingInfos.add(trainingInfo);
        trainingInfo.addTraining(this);
    }

    public static PersonalTraining registerPersonalTraining(Member member, Trainer trainer, Gym gym) {
        return PersonalTraining.builder()
            .member(member)
            .trainer(trainer)
            .gym(gym)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(LocalDateTime.now())
            .infoInputStatus(PTInfoInputStatus.INFO_EMPTY)
            .registrationAllowedStatus(PtRegistrationAllowedStatus.WAITING)
            .build();
    }
}
