package com.sideproject.withpt.application.lesson.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.lesson.controller.request.LessonRegistrationRequest;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.LessonStatus;
import com.sideproject.withpt.domain.pt.Lesson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class LessonServiceTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonLockFacade lettuceLockLessonFacade;

    @AfterEach
    public void after() {
        lessonRepository.deleteAll();
    }

    @Test
    @Rollback(value = false)
    public void 동시에_수업_요청() throws InterruptedException {
        //given
        Long gymId = 1L;
        Long loginId = 1L;
        String loginRole = "TRAINER";
        LessonRegistrationRequest request = LessonRegistrationRequest.builder()
            .registrationRequestId(1L)
            .date(LocalDate.parse("2023-11-15"))
            .weekday(Day.WED)
            .time(LocalTime.parse("12:00"))
            .build();

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockLessonFacade.registrationPtLesson(gymId, loginId, loginRole, request);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //when
        List<Lesson> result = lessonRepository.findAllByDateAndTimeAndStatus(request.getDate(),
            request.getTime(),
            LessonStatus.RESERVED);
        //then
        assertThat(result.size()).isEqualTo(1);
//        assertThatThrownBy(
//            () -> lessonRepository.findByDateAndTimeAndStatus(request.getDate(), request.getTime(),
//                LessonStatus.RESERVATION)
//        ).isExactlyInstanceOf(IncorrectResultSizeDataAccessException.class);
    }
}