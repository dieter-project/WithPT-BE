package com.sideproject.withpt.application.lesson.service;

import static com.sideproject.withpt.application.lesson.exception.LessonErrorCode.LESSON_NOT_FOUND;

import com.sideproject.withpt.application.lesson.controller.request.LessonChangeRequest;
import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.exception.LessonException;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.lesson.service.response.LessonResponse;
import com.sideproject.withpt.application.notification.service.NotificationService;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.lesson.LessonSchedule;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonDelegator {

    private final LessonService lessonService;
    private final LessonLockFacade lessonLockFacade;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public LessonResponse registrationPTLesson(Long gymId, LessonRegistrationRequest request) {
        // 요청자와 수신자 조회
        User requester = userRepository.findById(request.getRegistrationRequestId())
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        User receiver = userRepository.findById(request.getRegistrationReceiverId())
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        // 수업 등록 처리
        LessonResponse response = lessonLockFacade.lessonConcurrencyCheck(() ->
                lessonService.registrationPTLesson(gymId, requester, receiver, request.getDate(), request.getWeekday(), request.getTime()),
            lessonLockFacade.generateKey(request.getDate(), request.getTime())
        );

        // 요청자가 회원인 경우 알림 생성
        if (requester.getRole() == Role.MEMBER) {
            sendLessonRegistrationNotification(requester, receiver, response.getId());
        }

        return response;
    }

    public LessonResponse changePTLesson(Long lessonId, Long userId, LessonChangeRequest request) {


        // 000 트레이너 님으로부터 수업 변경 요청이 도착했어요
        // 000 회원 님으로부터 수업 변경 요청이 도착했어요.

        // 요청자 식별
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        // 상대방 사용자 식별
        User receiver = lesson.getRequester().equals(requester) ? lesson.getReceiver() : lesson.getRequester();


        return null;
    }

    public LessonResponse registrationOrScheduleChangeLessonAccept(Long userId, Long lessonId) {
        // 요청자 식별
        User requester = userRepository.findById(userId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        // 상대방 사용자 식별
        User receiver = lessonRepository.findById(lessonId)
            .map(lesson -> lesson.getRequester().equals(requester) ? lesson.getReceiver() : lesson.getRequester())
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        // 수업 정보 조회 및 수락 처리
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> LessonException.LESSON_NOT_FOUND);

        LessonResponse response = lessonService.registrationOrScheduleChangeLessonAccept(lesson);

        // 알림 생성
        /**
         * [수업 변경 허용] 요청자가 회원이면
         *  // 트레이너에게 알림 => 000 회원 님의 수업 변경이 완료되었어요.
         * [수업 등록 & 변경 허용] 요청자가 트레이너면
         *  // 회원에게 알림
         *  // 수업 등록 허용 : 수업 등록 요청이 완료되었어요.
         *  // 수업 변경 요청 완료 : 수업 변경이 완료되었어요.
         * 000 회원님의 0000년 00월 00일 00시 00분 수업이 예약되었습니다.
         */
        createLessonCompletionNotification(requester, receiver, lesson);

        return response;
    }

    private void sendLessonRegistrationNotification(User requester, User receiver, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new LessonException(LESSON_NOT_FOUND));

        notificationService.createNotification(requester, receiver,
            requester.getName() + " 회원 님으로부터 수업 등록 요청이 왔어요.",
            NotificationType.LESSON_REGISTRATION_REQUEST,
            lesson
        );
    }

    private void createLessonCompletionNotification(User requester, User receiver, Lesson lesson) {
        String name = lesson.getMember().getName();
        LessonSchedule schedule = lesson.getSchedule();
        LocalDate scheduleDate = schedule.getDate();
        LocalTime scheduleTime = schedule.getTime();

        String message = String.format("%s 회원님의 %d년 %d월 %d일 %d시 %d분 수업이 예약되었습니다.",
            name, scheduleDate.getYear(), scheduleDate.getMonthValue(), scheduleDate.getDayOfMonth(),
            scheduleTime.getHour(), scheduleTime.getMinute());

        notificationService.createNotification(
            requester,
            receiver,
            message,
            NotificationType.LESSON_REGISTRATION_COMPLETION,
            lesson
        );
    }

}
