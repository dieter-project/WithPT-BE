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

    private LocalDateTime firstRegistrationDate; // 센터 등록월

    private LocalDateTime lastRegistrationDate; // 센터 마지막 재등록월

    private LocalDateTime registrationRequestDate; // 등록 요청 날짜

    private LocalDateTime registrationAllowedDate; // 등록 승인 날짜

    @Enumerated(EnumType.STRING)
    private PtRegistrationStatus registrationStatus;

    @Enumerated(EnumType.STRING)
    private PTInfoInputStatus infoInputStatus;

    @Enumerated(EnumType.STRING)
    private PtRegistrationAllowedStatus registrationAllowedStatus;

    public static PersonalTraining registerNewPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime ptRegistrationRequestDate) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(0)
            .remainingPtCount(0)
            .registrationRequestDate(ptRegistrationRequestDate)
            .infoInputStatus(PTInfoInputStatus.INFO_EMPTY)
            .registrationStatus(PtRegistrationStatus.ALLOWED_BEFORE)
            .registrationAllowedStatus(PtRegistrationAllowedStatus.WAITING)
            .build();
    }

    public void approvedPersonalTrainingRegistration(LocalDateTime registrationAllowedDate) {
        this.registrationAllowedStatus = PtRegistrationAllowedStatus.ALLOWED;
        this.registrationStatus = PtRegistrationStatus.ALLOWED;
        this.registrationAllowedDate = registrationAllowedDate;
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
