package com.sideproject.withpt.domain.notification;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
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
@DiscriminatorValue("DIET")
public class DietNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diets relatedDiet;

    @Builder
    private DietNotification(NotificationType type, String text, User sender, User receiver, Diets relatedDiet) {
        super(type, text, sender, receiver);
        this.relatedDiet = relatedDiet;
    }
}
