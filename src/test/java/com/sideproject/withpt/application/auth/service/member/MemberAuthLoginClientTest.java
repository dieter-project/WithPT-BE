package com.sideproject.withpt.application.auth.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;

import com.sideproject.withpt.application.auth.infra.password.PasswordLoginParams;
import com.sideproject.withpt.application.auth.service.dto.LoginResponse;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.pt.repository.PersonalTrainingRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MemberAuthLoginClientTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private PersonalTrainingRepository personalTrainingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthLoginClient memberOAuthLoginClient;

    @MockBean
    private RedisClient redisClient;

    @DisplayName("비밀번호 기반 인증 처리 - PT 정보 X")
    @Test
    void loginByPassword() {
        // given
        String email = "test@test.com";
        String password = "test1234";

        memberRepository.save(createMember("회원", email, password, AuthProvider.EMAIL));

        willDoNothing()
            .given(redisClient)
            .put(anyString(), anyString(), any(TimeUnit.class), anyLong());

        PasswordLoginParams request = PasswordLoginParams.builder()
            .email(email)
            .password("test1234")
            .role(Role.MEMBER)
            .build();

        // when
        LoginResponse response = memberOAuthLoginClient.login(request);

        // then
        assertThat(response.getPtInfos()).isEmpty();
        assertThat(response.getLoginInfo())
            .extracting("email", "authProvider", "role")
            .contains(email, AuthProvider.EMAIL, Role.MEMBER);
    }

    @DisplayName("로그인 시 PT 정보가 존재하면 PT 정보와 함께 응답한다.")
    @Test
    void loginWhenExistPTInfo() {
        // given
        String email = "test@test.com";
        String password = "test1234";

        Member member = memberRepository.save(createMember("회원", email, password, AuthProvider.EMAIL));

        Gym gym1 = gymRepository.save(createGym("체육관1"));
        Trainer trainer1 = trainerRepository.save(createTrainer("트레이너1"));
        GymTrainer gymTrainer1 = gymTrainerRepository.save(createGymTrainer(gym1, trainer1, LocalDate.of(2024, 9, 30)));

        Gym gym2 = gymRepository.save(createGym("체육관2"));
        Trainer trainer2 = trainerRepository.save(createTrainer("트레이너2"));
        GymTrainer gymTrainer2 = gymTrainerRepository.save(createGymTrainer(gym2, trainer2, LocalDate.of(2024, 9, 30)));

        LocalDateTime registrationRequestDate = LocalDateTime.of(2024, 9, 27, 12, 45, 1);
        LocalDateTime registrationAllowedDate = LocalDateTime.of(2024, 9, 29, 0, 0, 0);

        LocalDateTime centerFirstRegistrationMonth = LocalDateTime.of(2024, 9, 1, 0, 0, 0);
        LocalDateTime centerLastReRegistrationMonth = LocalDateTime.of(2024, 12, 1, 0, 0, 0);

        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer1, registrationRequestDate, PTInfoInputStatus.INFO_EMPTY, PtRegistrationStatus.ALLOWED_BEFORE, PtRegistrationAllowedStatus.WAITING)
        );

        personalTrainingRepository.save(
            createPersonalTraining(member, gymTrainer2, "노트", 40, 20, registrationRequestDate,
                PTInfoInputStatus.INFO_REGISTERED, PtRegistrationStatus.RE_REGISTRATION, PtRegistrationAllowedStatus.ALLOWED,
                centerFirstRegistrationMonth, centerLastReRegistrationMonth, registrationAllowedDate)
        );

        willDoNothing()
            .given(redisClient)
            .put(anyString(), anyString(), any(TimeUnit.class), anyLong());

        PasswordLoginParams request = PasswordLoginParams.builder()
            .email(email)
            .password("test1234")
            .role(Role.MEMBER)
            .build();

        // when
        LoginResponse response = memberOAuthLoginClient.login(request);

        // then
        assertThat(response.getPtInfos()).hasSize(2)
            .extracting("trainer.name", "gym.name", "pt.registrationStatus")
            .contains(
                tuple("트레이너1", "체육관1", PtRegistrationStatus.ALLOWED_BEFORE),
                tuple("트레이너2", "체육관2", PtRegistrationStatus.RE_REGISTRATION)
            );
        assertThat(response.getLoginInfo())
            .extracting("email", "authProvider", "role")
            .contains(email, AuthProvider.EMAIL, Role.MEMBER);
    }

    @DisplayName("패스워드가 유효하기 않으면 로그인 불가능하다.")
    @Test
    void loginWhenInvalidPassword() {
        // given
        String email = "test@test.com";

        Member member = Member.builder()
            .email(email)
            .password("test1234")
            .role(Role.MEMBER)
            .authProvider(AuthProvider.EMAIL)
            .build();
        memberRepository.save(member);

        willDoNothing()
            .given(redisClient)
            .put(anyString(), anyString(), any(TimeUnit.class), anyLong());

        PasswordLoginParams request = PasswordLoginParams.builder()
            .email(email)
            .password("test123")
            .role(Role.MEMBER)
            .build();

        // when // then
        assertThatThrownBy(() -> memberOAuthLoginClient.login(request))
            .isInstanceOf(GlobalException.class)
            .hasMessage("Invalid password");
    }

    private PersonalTraining createPersonalTraining(Member member, GymTrainer gymTrainer, LocalDateTime registrationRequestDate, PTInfoInputStatus infoInputStatus, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus) {
        return createPersonalTraining(member, gymTrainer, "", 0, 0, registrationRequestDate, infoInputStatus, registrationStatus, registrationAllowedStatus, null, null, null);
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

    private Member createMember(String name, String email, String password, AuthProvider authProvider) {
        return Member.builder()
            .email(email)
            .password(password)
            .role(Role.MEMBER)
            .authProvider(authProvider)
            .name(name)
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