package com.sideproject.withpt.application.notification.service.response.mapper;

import com.sideproject.withpt.application.notification.service.response.mapper.impl.DietNotificationMapper;
import com.sideproject.withpt.application.notification.service.response.mapper.impl.LessonNotificationMapper;
import com.sideproject.withpt.application.notification.service.response.mapper.impl.PersonalTrainingNotificationMapper;
import com.sideproject.withpt.domain.notification.DietNotification;
import com.sideproject.withpt.domain.notification.LessonNotification;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.notification.PersonalTrainingNotification;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class NotificationMapperFactoryTest {

    @DisplayName("리플렉션을 통해 알림 타입 확인")
    @Test
    void getNotificationClass() {

        DietNotificationMapper mapper = new DietNotificationMapper();

        // then
        System.out.println("mapper.getClass() = " + mapper.getClass());
        System.out.println("mapper.getClass().getGenericInterfaces() = " + Arrays.toString(mapper.getClass().getGenericInterfaces()));
        System.out.println("mapper.getClass().getGenericInterfaces()[0] = " + mapper.getClass().getGenericInterfaces()[0]);
        System.out.println("(ParameterizedType) mapper.getClass().getGenericInterfaces()[0] = " + (ParameterizedType) mapper.getClass().getGenericInterfaces()[0]);
        System.out.println("((ParameterizedType) mapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()= " + ((ParameterizedType) mapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        Class<? extends Notification> aClass = (Class<? extends Notification>) ((ParameterizedType) mapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        System.out.println("aClass = " + aClass);

    }

    @DisplayName("주입된 알림 map 확인")
    @Test
    void getNotificationClassPrint() {

        NotificationMapperFactory notificationMapperFactory = new NotificationMapperFactory(List.of(
            new DietNotificationMapper(),
            new LessonNotificationMapper(),
            new PersonalTrainingNotificationMapper())
        );

        Map<Class<? extends Notification>, NotificationMapper<? extends Notification>> mappers = notificationMapperFactory.getMappers();
        mappers.forEach((aClass, notificationMapper) ->
            log.info("key {} : value {}", aClass, notificationMapper)
        );
        System.out.println("======================\n");

        Notification notification = personalTrainingNotification();
        log.info("type : {}", notification.getClass());
        NotificationMapper<? extends Notification> mapper = notificationMapperFactory.getMapper(notification.getClass());
        log.info("mapper : {}", mapper);
    }


    private Notification personalTrainingNotification() {
        return PersonalTrainingNotification.builder()
            .build();
    }

    private Notification lessonNotification() {
        return LessonNotification.builder()
            .build();
    }

    private Notification dietNotification() {
        return DietNotification.builder()
            .build();
    }

    private Class<? extends Notification> getNotificationClass(NotificationMapper<? extends Notification> mapper) {
        return (Class<? extends Notification>)
            ((ParameterizedType) mapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}