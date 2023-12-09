package com.sideproject.withpt.domain.pt;

import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.Gym;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PTCountLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalPtCount;
    private int remainingPtCount;

    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private PtRegistrationStatus registrationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public static PTCountLog recordPTCountLog(
        Member member, Trainer trainer, Gym gym,
        int totalPtCount, int remainingPtCount, LocalDateTime registrationDate, PtRegistrationStatus registrationStatus
    ) {
        return PTCountLog.builder()
            .member(member)
            .trainer(trainer)
            .gym(gym)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .registrationDate(registrationDate)
            .registrationStatus(registrationStatus)
            .build();
    }
}
