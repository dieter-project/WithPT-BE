package com.sideproject.withpt.application.pt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingApproveNotificationEvent;
import com.sideproject.withpt.application.pt.event.model.PersonalTrainingRegistrationNotificationEvent;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingMemberResponse;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class PersonalTrainingManagerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private PersonalTrainingRepository personalTrainingRepository;

    @Autowired
    private PersonalTrainingManager personalTrainingManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @AfterEach
    void resetMocks() {
        Mockito.reset(eventPublisher);
    }

    @DisplayName("체육관에 신규 PT 회원을 등록할 때 이벤트 발생")
    @Test
    void registerPersonalTraining() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
        LocalDateTime ptRegistrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45);

        // when
        PersonalTrainingMemberResponse response = personalTrainingManager.registerPersonalTraining(
            gym.getId(), member.getId(), trainer.getId(), ptRegistrationRequestDate
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMember()).isEqualTo("회원");
        assertThat(response.getTrainer()).isEqualTo("test 트레이너");

        // 이벤트가 발행되었는지 확인
        verify(eventPublisher, times(1))
            .publishEvent(any(PersonalTrainingRegistrationNotificationEvent.class));
    }

    @DisplayName("회원 측에서 PT 등록을 허용할 때 이벤트 발생")
    @Test
    void approvedPersonalTrainingRegistration() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Trainer trainer = trainerRepository.save(createTrainer("test 트레이너"));
        Gym gym = gymRepository.save(createGym("체육관1"));

        GymTrainer gymTrainer = gymTrainerRepository.save(createGymTrainer(gym, trainer, LocalDate.of(2024, 9, 27)));
        PersonalTraining personalTraining = personalTrainingRepository.save(createPersonalTraining(member, gymTrainer, LocalDateTime.of(2024, 9, 27, 12, 45), PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING));

        final LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 27, 9, 51);

        // when
        personalTrainingManager.approvedPersonalTrainingRegistration(personalTraining.getId(), member.getId(), registrationAllowedDate);

        // then
        PersonalTraining savedPersonalTraining = personalTrainingRepository.findAll().get(0);
        assertThat(savedPersonalTraining)
            .extracting("registrationAllowedStatus", "registrationStatus", "registrationAllowedDate")
            .contains(PtRegistrationAllowedStatus.ALLOWED, PtRegistrationStatus.ALLOWED, registrationAllowedDate);

        verify(eventPublisher, times(1))
            .publishEvent(any(PersonalTrainingApproveNotificationEvent.class));
    }

    /**
     * 공식 문서에서는 @TestConfiguration을 아래와 같이 설명하고 있다.
     * <p>
     * 1. 테스트에 대한 추가적인 빈이나 커스텀을 정의하는 데 사용할 수 있는 @Configuration이다.
     * <p>
     * 2. @Configuration 클래스와 달리 @TestConfiguration을 사용해도 @SpringBootConfiguration의 자동 감지를 방지하지 않는다.
     */
    @TestConfiguration
    static class MockitoPublisherConfiguration {

        @Bean
        @Primary
        ApplicationEventPublisher publisher() {
            return mock(ApplicationEventPublisher.class);
        }
    }

    public PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, String note, int totalPtCount, int remainingPtCount, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus, LocalDateTime centerFirstRegistrationMonth, LocalDateTime centerLastReRegistrationMonth, LocalDateTime registrationAllowedDate) {
        return PersonalTraining.builder()
            .member(member)
            .gymTrainer(gymTrainer)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .note(note)
            .centerFirstRegistrationMonth(centerFirstRegistrationMonth)
            .centerLastReRegistrationMonth(centerLastReRegistrationMonth)
            .registrationRequestDate(registrationRequestDate)
            .registrationAllowedDate(registrationAllowedDate)
            .registrationStatus(registrationStatus)
            .infoInputStatus(infoInputStatus)
            .registrationAllowedStatus(registrationAllowedStatus)
            .build();
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return createPersonalTraining(member, gymTrainer, null, 0, 0, registrationRequestDate, infoInputStatus, registrationStatus, registrationAllowedStatus, null, null, null);
    }

    private Member createMember(String name) {
        return Member.builder()
            .name(name)
            .birth(LocalDate.parse("1994-07-19"))
            .sex(Sex.MAN)
            .height(173.0)
            .weight(73.5)
            .dietType(DietType.Carb_Protein_Fat)
            .exerciseFrequency(ExerciseFrequency.EVERYDAY)
            .targetWeight(65.0)
            .build();
    }

    private Gym createGym(String name) {
        return Gym.builder()
            .name(name)
            .address("주소 123-123")
            .build();
    }

    private Trainer createTrainer(String name) {
        return Trainer.signUpBuilder()
            .email(name + "@test.com")
            .name(name)
            .build();
    }

    private GymTrainer createGymTrainer(Gym gym, Trainer trainer, LocalDate hireDate) {
        return GymTrainer.builder()
            .gym(gym)
            .trainer(trainer)
            .hireDate(hireDate)
            .build();
    }

}