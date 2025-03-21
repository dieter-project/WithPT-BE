package com.sideproject.withpt.application.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
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
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@ActiveProfiles("test")
@SpringBootTest
class NotificationRepositoryTest {

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
    private EntityManager em;

    @DisplayName("Receiver 대상 알림 리스트 조회")
    @Test
    void findAllByReceiver() {
        // given
        Member member = userRepository.save(createMember("회원"));

        Trainer trainer = userRepository.save(createTrainer("트레이너"));
        Trainer trainer2 = userRepository.save(createTrainer("트레이너2"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        Lesson lesson = lessonRepository.save(createLesson(member, gymTrainer));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer));
        Diets diets = dietRepository.save(createDiets(member));

        notificationRepository.saveAll(List.of(
            personalTrainingNotification(member, trainer, NotificationType.PT_REGISTRATION_REQUEST, "PT 등록", LocalDateTime.of(2024, 10, 29, 3, 20), personalTraining),
            lessonNotification(member, trainer, NotificationType.LESSON_REGISTRATION_REQUEST, "수업 변경", LocalDateTime.of(2024, 10, 29, 3, 28), lesson),
            lessonNotification(member, trainer2, NotificationType.LESSON_REGISTRATION_REQUEST, "수업 변경", LocalDateTime.of(2024, 10, 29, 3, 28), lesson),
            dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백1", LocalDateTime.of(2024, 10, 29, 3, 28), diets),
            dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백2", LocalDateTime.of(2024, 10, 29, 3, 29), diets),
            dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백3", LocalDateTime.of(2024, 10, 29, 3, 31), diets),
            dietNotification(trainer, member, NotificationType.DIET_FEEDBACK, "식단 피드백4", LocalDateTime.of(2024, 10, 29, 3, 55), diets))
        );

        Pageable pageable = PageRequest.of(1, 3);
        User receiver = trainer;

        // when
        Slice<Notification> result = notificationRepository.findAllByReceiverOrderByCreatedAtDescIdDesc(receiver, pageable);

        // then
        assertThat(result.getContent()).hasSize(2)
            .extracting("type", "text", "receiver")
            .containsExactly(
                tuple(NotificationType.LESSON_REGISTRATION_REQUEST, "수업 변경", trainer),
                tuple(NotificationType.PT_REGISTRATION_REQUEST, "PT 등록", trainer)
            );

        assertThat(result.hasNext()).isFalse();
    }

    @DisplayName("알림 읽기 체크")
    @Test
    void markNotificationsAsRead() {
        // given
        Member member = userRepository.save(createMember("회원"));

        Trainer trainer = userRepository.save(createTrainer("트레이너"));
        Trainer trainer2 = userRepository.save(createTrainer("트레이너2"));
        Gym gym = gymRepository.save(createGym("체육관"));
        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer));

        Lesson lesson = lessonRepository.save(createLesson(member, gymTrainer));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer));
        Diets diets = dietRepository.save(createDiets(member));

        Notification notification1 = notificationRepository.save(personalTrainingNotification(member, trainer, NotificationType.PT_REGISTRATION_REQUEST, "PT 등록", LocalDateTime.of(2024, 10, 29, 3, 20), personalTraining));
        Notification notification2 = notificationRepository.save(lessonNotification(member, trainer, NotificationType.LESSON_REGISTRATION_REQUEST, "수업 변경", LocalDateTime.of(2024, 10, 29, 3, 28), lesson));
        Notification notification3 = notificationRepository.save(lessonNotification(member, trainer2, NotificationType.LESSON_REGISTRATION_REQUEST, "수업 변경", LocalDateTime.of(2024, 10, 29, 3, 28), lesson));
        Notification notification4 = notificationRepository.save(dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백1", LocalDateTime.of(2024, 10, 29, 3, 28), diets));
        Notification notification5 = notificationRepository.save(dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백2", LocalDateTime.of(2024, 10, 29, 3, 29), diets));
        Notification notification6 = notificationRepository.save(dietNotification(member, trainer, NotificationType.DIET_FEEDBACK, "식단 피드백3", LocalDateTime.of(2024, 10, 29, 3, 31), diets));
        Notification notification7 = notificationRepository.save(dietNotification(trainer, member, NotificationType.DIET_FEEDBACK, "식단 피드백4", LocalDateTime.of(2024, 10, 29, 3, 55), diets));

        User receiver = trainer;
        List<Long> notificationIds = List.of(notification1.getId(), notification2.getId(), notification4.getId());

        // when
        notificationRepository.markNotificationsAsRead(receiver, notificationIds);

        // then
        em.flush();
        em.clear();

        List<Notification> result = notificationRepository.findAllByReceiver(receiver);

        List<Notification> collect = result.stream()
            .filter(Notification::isRead)
            .collect(Collectors.toList());

        assertThat(collect).hasSize(3);
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

    public Lesson createLesson(Member member, GymTrainer gymTrainer) {
        return Lesson.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .build();
    }

    private Diets createDiets(Member member) {
        return Diets.builder()
            .member(member)
            .build();
    }
}