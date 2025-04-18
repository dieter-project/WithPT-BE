package com.sideproject.withpt.application.lesson.service;

import com.sideproject.withpt.common.redis.RedisClient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LessonConcurrencyLockManager {

    private final RedisClient redisClient;

    public <T> T lessonConcurrencyCheck(Callable<T> callable, String key) {

        log.info("사용된 키 {} ", key);

        while (!redisClient.lock(key)) {
            sleep(100);
        }

        try {
            // TODO : 채팅 노티 및 알림 추가
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Callable 실행 중 오류 발생", e);
        } finally {
            redisClient.unlock(key);
        }
    }

    public String generateKey(LocalDate date, LocalTime time) {
        return date.toString() + time.toString();
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
lesson
-controller(pakage)
--request(pakage)
---LessonChangeRequest
---LessonRegistrationRequest
--LessonController
-event(pakage)
--listener(pakage)
---LessonEventListener
--model(pakage)
---LessonNotificationEvent
---LessonRegistrationNotificationEvent
-exception(pakage)
--LessonErrorCode
--LessonException
-repository(pakage)
--LessonQueryRepository
--LessonQueryRepositoryImpl
--LessonRepository
-service(pakage)
--response(pakage)
---AvailableLessonScheduleResponse
---LessonInfoResponse
---LessonResponse
---LessonScheduleOfMonthResponse
---LessonScheduleResponse
---LessonUserResponse
--LessonConcurrencyLockManager
--LessonManager
--LessonService
     */
}
