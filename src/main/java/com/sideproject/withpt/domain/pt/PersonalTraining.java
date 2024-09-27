package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.trainer.Trainer;
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
import lombok.Setter;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
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
    @JoinColumn(name = "gym_trainer_id")
    private GymTrainer gymTrainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

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

    public static PersonalTraining registerPersonalTraining(Member member, Trainer trainer, Gym gym) {
        return PersonalTraining.builder()
            .member(member)
            .trainer(trainer)
            .gym(gym)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(LocalDateTime.now())
            .infoInputStatus(PTInfoInputStatus.INFO_EMPTY)
            .registrationStatus(PtRegistrationStatus.ALLOWED_BEFORE)
            .registrationAllowedStatus(PtRegistrationAllowedStatus.WAITING)
            .build();
    }

    public static PersonalTraining registerPersonalTraining(Member member, GymTrainer gymTrainer) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(LocalDateTime.now())
            .infoInputStatus(PTInfoInputStatus.INFO_EMPTY)
            .registrationStatus(PtRegistrationStatus.ALLOWED_BEFORE)
            .registrationAllowedStatus(PtRegistrationAllowedStatus.WAITING)
            .build();
    }

    public static void allowPTRegistration(PersonalTraining personalTraining) {
        personalTraining.setRegistrationAllowedStatus(PtRegistrationAllowedStatus.ALLOWED);
        personalTraining.setRegistrationStatus(PtRegistrationStatus.ALLOWED);
    }

    public static void saveFirstPtDetailInfo(PersonalTraining pt, int ptCount, LocalDateTime firstRegistrationDate, String note) {
        pt.setTotalPtCount(ptCount);
        pt.setRemainingPtCount(ptCount);
        pt.setFirstRegistrationDate(firstRegistrationDate);
        pt.setNote(note);
        pt.setInfoInputStatus(PTInfoInputStatus.INFO_REGISTERED);
        pt.setRegistrationStatus(PtRegistrationStatus.NEW_REGISTRATION);
    }

    public static void updatePtDetailInfo(PersonalTraining pt, int totalPtCount, int remainingPtCount, String note) {
        pt.setTotalPtCount(pt.getTotalPtCount() + (totalPtCount - pt.getTotalPtCount()));
        pt.setRemainingPtCount(pt.getRemainingPtCount() + (remainingPtCount - pt.getRemainingPtCount()));
        pt.setNote(note);
    }

    public static void extendPt(PersonalTraining pt, int totalPtCount, int remainingPtCount, LocalDateTime registrationDate) {
        pt.setTotalPtCount(pt.getTotalPtCount() + totalPtCount);
        pt.setRemainingPtCount(pt.getRemainingPtCount() + remainingPtCount);
        pt.setLastRegistrationDate(registrationDate);
        pt.setRegistrationStatus(PtRegistrationStatus.RE_REGISTRATION);
    }
}
