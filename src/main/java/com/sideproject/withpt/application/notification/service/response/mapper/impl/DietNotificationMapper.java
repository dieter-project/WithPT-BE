
package com.sideproject.withpt.application.notification.service.response.mapper.impl;

import com.sideproject.withpt.application.notification.service.response.NotificationInfoResponse;
import com.sideproject.withpt.application.notification.service.response.mapper.NotificationMapper;
import com.sideproject.withpt.application.record.diet.service.response.DietResponse;
import com.sideproject.withpt.domain.notification.DietNotification;
import org.springframework.stereotype.Component;

@Component
public class DietNotificationMapper implements NotificationMapper<DietNotification> {

    @Override
    public NotificationInfoResponse<?> toResponse(DietNotification notification) {
        DietResponse response = DietResponse.of(notification.getRelatedDiet());
        return NotificationInfoResponse.from(notification, response);
    }
}
