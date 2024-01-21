package com.sideproject.withpt.application.lesson.service;

import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.common.redis.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonLockFacade {

    private final RedisClient redisClient;
    private final LessonService lessonService;

    public void registrationPtLesson(Long gymId, Long loginId, String loginRole, LessonRegistrationRequest request) {

        String key = generateKey(request);

        while(!redisClient.lock(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            lessonService.registrationPtLesson(gymId, loginId, loginRole, request);
            // TODO : 채팅 노티 및 알림 추가
        } finally {
            redisClient.unlock(key);
        }
    }

    private String generateKey(LessonRegistrationRequest request) {
        String date = request.getDate().toString();
        String time = request.getTime().toString();
        return date + time;
    }
}
