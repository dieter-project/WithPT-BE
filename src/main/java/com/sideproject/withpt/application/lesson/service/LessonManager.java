package com.sideproject.withpt.application.lesson.service;

import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.event.model.LessonNotificationEvent;
import com.sideproject.withpt.application.lesson.event.model.LessonRegistrationNotificationEvent;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonManager {

    private final LessonService lessonService;
    private final LessonConcurrencyLockManager lessonConcurrencyLockManager;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final String MEMBER_REGISTRATION_REQUEST_MSG = "%s 회원 님으로부터 수업 등록 요청이 왔어요.";
    private static final String LESSON_CHANGE_REQUEST_MSG = "%s 님으로부터 수업 변경 요청이 도착했어요.";

    @Transactional
    public LessonResponse registrationPTLesson(Long gymId, LessonRegistrationRequest request) {
        log.info("[LessonDelegator.registrationPTLesson()]");
        log.info("Registering PT lesson for gym ID: {}", gymId);

        User requester = findUserById(request.getRegistrationRequestId());
        User receiver = findUserById(request.getRegistrationReceiverId());

        eventPublisher.publishEvent(
            new LessonRegistrationNotificationEvent(
                requester, receiver,
                String.format(MEMBER_REGISTRATION_REQUEST_MSG, requester.getName()),
                NotificationType.LESSON_REGISTRATION_REQUEST,
                gymId,
                request.getDate(),
                request.getTime()
            )
        );

        return registerLessonWithLock(gymId, request, requester, receiver);
    }

    @Transactional
    public LessonResponse changePTLesson(Long lessonId, Long userId, LessonChangeRequest request) {
        log.info("[LessonDelegator.changePTLesson()]");
        log.info("Changing PT lesson with lesson ID: {}", lessonId);

        User requester = findUserById(userId);
        Lesson lesson = findLessonById(lessonId);
        User receiver = getReceiver(requester, lesson);

        eventPublisher.publishEvent(
            LessonNotificationEvent.create(
                requester,
                receiver,
                String.format(LESSON_CHANGE_REQUEST_MSG, requester.getName()),
                NotificationType.LESSON_CHANGE_REQUEST,
                lesson
            )
        );

        return changeLessonWithLock(request, requester, lesson);
    }

    @Transactional
    public LessonResponse registrationOrScheduleChangeLessonAccept(Long userId, Long lessonId) {
        log.info("[LessonDelegator.registrationOrScheduleChangeLessonAccept()]");
        log.info("Accepting lesson change or registration for lesson ID: {}", lessonId);

        User requester = findUserById(userId);
        Lesson lesson = findLessonById(lessonId);
        User receiver = getReceiver(requester, lesson);

        /**
         * [수업 변경 허용] 요청자가 회원이면
         *  // 트레이너에게 알림 => 000 회원 님의 수업 변경이 완료되었어요.
         * [수업 등록 & 변경 허용] 요청자가 트레이너면
         *  // 회원에게 알림
         *  // 수업 등록 허용 : 수업 등록 요청이 완료되었어요.
         *  // 수업 변경 요청 완료 : 수업 변경이 완료되었어요.
         * 000 회원님의 0000년 00월 00일 00시 00분 수업이 예약되었습니다.
         */
        eventPublisher.publishEvent(
            LessonNotificationEvent.create(
                requester,
                receiver,
                createAcceptanceMessage(lesson),
                NotificationType.LESSON_REGISTRATION_COMPLETION,
                lesson
            )
        );

        return lessonService.registrationOrScheduleChangeLessonAccept(lesson);
    }

    private LessonResponse registerLessonWithLock(Long gymId, LessonRegistrationRequest request, User requester, User receiver) {
        return lessonConcurrencyLockManager.lessonConcurrencyCheck(() ->
                lessonService.registrationPTLesson(gymId, requester, receiver, request.getDate(), request.getWeekday(), request.getTime()),
            lessonConcurrencyLockManager.generateKey(request.getDate(), request.getTime())
        );
    }

    private LessonResponse changeLessonWithLock(LessonChangeRequest request, User requester, Lesson lesson) {
        return lessonConcurrencyLockManager.lessonConcurrencyCheck(() ->
                lessonService.changePTLesson(lesson, requester, request.getDate(), request.getTime(), request.getWeekday()),
            lessonConcurrencyLockManager.generateKey(request.getDate(), request.getTime())
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
    }

    private Lesson findLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
            .orElseThrow(() -> LessonException.LESSON_NOT_FOUND);
    }

    private static User getReceiver(User requester, Lesson lesson) {
        return lesson.getRequester().equals(requester) ? lesson.getReceiver() : lesson.getRequester();
    }

    private String createAcceptanceMessage(Lesson lesson) {
        LessonSchedule schedule = lesson.getSchedule();
        LocalDate date = schedule.getDate();
        LocalTime time = schedule.getTime();

        return String.format("%s 회원님의 %d년 %d월 %d일 %d시 %d분 수업이 예약되었습니다.",
            lesson.getMember().getName(),
            date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            time.getHour(), time.getMinute());
    }
}
