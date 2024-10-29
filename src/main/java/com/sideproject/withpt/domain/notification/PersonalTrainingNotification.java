package com.sideproject.withpt.domain.notification;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("PERSONAL_TRAINING")
public class PersonalTrainingNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_training_id")
    private PersonalTraining relatedPersonalTraining;

    @Builder
    private PersonalTrainingNotification(NotificationType type, String text, User sender, User receiver, LocalDateTime createdAt, PersonalTraining relatedPersonalTraining) {
        super(type, text, sender, receiver, createdAt);
        this.relatedPersonalTraining = relatedPersonalTraining;
    }
}
