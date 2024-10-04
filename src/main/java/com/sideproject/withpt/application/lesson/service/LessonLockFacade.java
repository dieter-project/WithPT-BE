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
public class LessonLockFacade {

    private final RedisClient redisClient;

//    public void registrationPtLesson(Long gymId, Long loginId, String loginRole, LessonRegistrationRequest request) {
//
//        String key = generateKey(request);
//
//        while(!redisClient.lock(key)) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        try {
//            lessonService.registrationPtLesson(gymId, loginId, loginRole, request);
//            // TODO : 채팅 노티 및 알림 추가
//        } finally {
//            redisClient.unlock(key);
//        }
//    }

    public void lessonConcurrencyCheck(Runnable runnable, String key) {

        log.info("사용된 키 {} ", key);

        while (!redisClient.lock(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            runnable.run();
            // TODO : 채팅 노티 및 알림 추가
        } finally {
            redisClient.unlock(key);
        }
    }

    public <T> T lessonConcurrencyCheck(Callable<T> callable, String key) {

        log.info("사용된 키 {} ", key);

        while (!redisClient.lock(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

}
