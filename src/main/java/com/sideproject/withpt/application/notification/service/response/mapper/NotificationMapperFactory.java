package com.sideproject.withpt.application.notification.service.response.mapper;

import com.sideproject.withpt.domain.notification.Notification;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NotificationMapperFactory {

    private final Map<Class<? extends Notification>, NotificationMapper<? extends Notification>> mappers = new HashMap<>();

    @Autowired
    public NotificationMapperFactory(List<NotificationMapper<? extends Notification>> mapperList) {
        for (NotificationMapper<? extends Notification> mapper : mapperList) {
            mappers.put(getNotificationClass(mapper), mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Notification> NotificationMapper<T> getMapper(Class<T> clazz) {
        return (NotificationMapper<T>) mappers.get(clazz);
    }

    private Class<? extends Notification> getNotificationClass(NotificationMapper<? extends Notification> mapper) {
        return (Class<? extends Notification>)
            ((ParameterizedType) mapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}
