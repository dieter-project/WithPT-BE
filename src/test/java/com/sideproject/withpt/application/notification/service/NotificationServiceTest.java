package com.sideproject.withpt.application.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.notification.repository.NotificationRepository;
import com.sideproject.withpt.application.notification.service.response.NotificationResponse;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.record.diet.repository.DietRepository;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.notification.DietNotification;
import com.sideproject.withpt.domain.notification.LessonNotification;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.notification.PersonalTrainingNotification;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private PersonalTrainingRepository personalTrainingRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @DisplayName("식단 Type 알림 엔티티 생성")
    @Test
    void createDietNotification() {
        // given
        Member member = userRepository.save(createMember("회원"));
        Trainer trainer = userRepository.save(createTrainer("트레이너"));

        Diets diets = dietRepository.save(createDiets(member));

        // when
        Notification notification = notificationService.createNotification(
            member,    // sender: 회원
            trainer,   // receiver: 트레이너
            "000 회원님으로부터 식단 피드백 요청이 도착했어요.",  // 알림 내용
            NotificationType.DIET_FEEDBACK,  // 알림 종류
            LocalDateTime.of(2024, 10, 29, 3, 22),
            diets  // 관련된 식단 ID
        );

        // then
        Optional<Notification> optionalNotification = notificationRepository.findById(notification.getId());
        assertThat(optionalNotification).isPresent();

        Notification find = optionalNotification.get();
        assertThat(find.getSender())
            .extracting("name", "role")
            .contains("회원", Role.MEMBER);

        assertThat(find.getReceiver())
            .extracting("name", "role")
            .contains("트레이너", Role.TRAINER);

        DietNotification dietNotification = (DietNotification) find;
        assertThat(dietNotification).isNotNull();
    }

    @DisplayName("수업 Type 알림 엔티티 생성")
    @Test
    void createLessonNotification() {
        // given
        Member member = userRepository.save(createMember("회원"));

        Trainer trainer = userRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        Lesson lesson = lessonRepository.save(createLesson(member, gymTrainer, member, trainer));

        // when
        Notification notification = notificationService.createNotification(
            member,    // sender: 회원
            trainer,   // receiver: 트레이너
            "000 회원 님으로부터 수업 등록 요청이 왔어요.",  // 알림 내용
            NotificationType.LESSON_REGISTRATION_REQUEST,  // 알림 종류
            LocalDateTime.of(2024, 10, 29, 3, 22),
            lesson  // 관련된 수업 ID
        );

        // then
        Optional<Notification> optionalNotification = notificationRepository.findById(notification.getId());
        assertThat(optionalNotification).isPresent();

        LessonNotification lessonNotification = (LessonNotification) optionalNotification.get();
        assertThat(lessonNotification).isNotNull();
    }

    @DisplayName("PT Type 알림 엔티티 생성")
    @Test
    void createPersonalTrainingNotification() {
        // given
        Member member = userRepository.save(createMember("회원"));

        Trainer trainer = userRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer));

        // when
        Notification notification = notificationService.createNotification(
            trainer,   // sender: 트레이너
            member,    // receiver: 회원
            "000트레이너/000 피트니스\n"
                + "등록 요청을 수락하시겠습니까?",  // 알림 내용
            NotificationType.PT_REGISTRATION_REQUEST,  // 알림 종류
            LocalDateTime.of(2024, 10, 29, 3, 22),
            personalTraining  // 관련된 수업 ID
        );

        // then
        Optional<Notification> optionalNotification = notificationRepository.findById(notification.getId());
        assertThat(optionalNotification).isPresent();

        PersonalTrainingNotification personalTrainingNotification = (PersonalTrainingNotification) optionalNotification.get();
        assertThat(personalTrainingNotification).isNotNull();
    }

    @DisplayName("트레이너를 대상으로 한 알림 목록 조회")
    @Test
    void getNotification() {
        // given
        Member member = userRepository.save(createMember("회원"));

        Trainer trainer = userRepository.save(createTrainer("트레이너"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        Lesson lesson = lessonRepository.save(createLesson(member, gymTrainer, member, trainer));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer));
        Diets diets = dietRepository.save(createDiets(member));

        notificationRepository.saveAll(List.of(
            personalTrainingNotification(member, trainer, NotificationType.PT_REGISTRATION_REQUEST, "PT등록", LocalDateTime.of(2024, 10, 29, 4, 10), personalTraining),
            lessonNotification(member, trainer, NotificationType.LESSON_REGISTRATION_REQUEST, "text", LocalDateTime.of(2024, 10, 29, 4, 55), lesson),
            dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백", LocalDateTime.of(2024, 10, 29, 5, 11), diets))
        );

        Pageable pageable = PageRequest.of(0, 10);
        Long receiverId = trainer.getId();

        // when
        NotificationResponse response = notificationService.getNotificationList(receiverId, pageable);

        // then
        assertThat(response.getSender().getName()).isEqualTo("회원");
        assertThat(response.getReceiver().getName()).isEqualTo("트레이너");
        assertThat(response.getNotifications().getContent().get(0).isRead()).isFalse();
        assertThat(response.getNotifications().getContent()).hasSize(3)
            .extracting("type", "message", "createdAt")
            .containsExactly(
                tuple(NotificationType.DIET_FEEDBACK, "식단 피드백", LocalDateTime.of(2024, 10, 29, 5, 11)),
                tuple(NotificationType.LESSON_REGISTRATION_REQUEST, "text", LocalDateTime.of(2024, 10, 29, 4, 55)),
                tuple(NotificationType.PT_REGISTRATION_REQUEST, "PT등록", LocalDateTime.of(2024, 10, 29, 4, 10))
            );

    }

    private Notification personalTrainingNotification(User sender, User receiver, NotificationType type, String text, LocalDateTime createdAt, PersonalTraining relatedEntity) {
        return PersonalTrainingNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .type(type)
            .text(text)
            .createdAt(createdAt)
            .relatedPersonalTraining(relatedEntity)
            .build();
    }

    private Notification lessonNotification(User sender, User receiver, NotificationType type, String text, LocalDateTime createdAt, Lesson relatedEntity) {
        return LessonNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .type(type)
            .text(text)
            .createdAt(createdAt)
            .relatedLesson(relatedEntity)
            .build();
    }

    private Notification dietNotification(User sender, User receiver, NotificationType type, String text, LocalDateTime createdAt, Diets relatedEntity) {
        return DietNotification.builder()
            .sender(sender)
            .receiver(receiver)
            .text(text)
            .type(type)
            .createdAt(createdAt)
            .relatedDiet(relatedEntity)
            .build();
    }

    private Member createMember(String name) {
        return Member.builder()
            .email("test@test.com")
            .role(Role.MEMBER)
            .weight(80.0)
            .name(name)
            .build();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email("test@test.com")
            .role(Role.TRAINER)
            .name(name)
            .build();
    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .build();
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .build();
    }

    public Lesson createLesson(Member member, GymTrainer gymTrainer, User requester, User receiver) {
        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .requester(requester)
            .receiver(receiver)
            .build();
    }

    private Diets createDiets(Member member) {
        return Diets.builder()
            .member(member)
            .build();
    }

}