package com.sideproject.withpt.application.notification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.lesson.repository.LessonRepository;
import com.sideproject.withpt.application.notification.repository.NotificationRepository;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        Lesson lesson = lessonRepository.save(createLesson(member, gymTrainer));

        // when
        Notification notification = notificationService.createNotification(
            member,    // sender: 회원
            trainer,   // receiver: 트레이너
            "000 회원 님으로부터 수업 등록 요청이 왔어요.",  // 알림 내용
            NotificationType.LESSON_REGISTRATION_REQUEST,  // 알림 종류
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
            personalTraining  // 관련된 수업 ID
        );

        // then
        Optional<Notification> optionalNotification = notificationRepository.findById(notification.getId());
        assertThat(optionalNotification).isPresent();

        PersonalTrainingNotification personalTrainingNotification = (PersonalTrainingNotification) optionalNotification.get();
        assertThat(personalTrainingNotification).isNotNull();
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