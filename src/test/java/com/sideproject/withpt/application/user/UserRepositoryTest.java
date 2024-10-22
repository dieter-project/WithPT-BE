package com.sideproject.withpt.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.common.type.Role;
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
class UserRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원 저장 후 UserRepository 로 조회 시 MEMBER Type 확인")
    @Test
    void isMemberByUserRepository() {
        // given
        Member member = memberRepository.save(createMember("회원"));

        // when
        Optional<User> optionalUser = userRepository.findById(member.getId());

        // then
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get()).isInstanceOf(Member.class);
    }

    @DisplayName("회원 저장 후 UserRepository 로 조회 시 Trainer Type 검증하면 에러")
    @Test
    void isMemberByUserRepositoryThrowError() {
        // given
        Member member = memberRepository.save(createMember("회원"));
        Optional<User> optionalUser = userRepository.findById(member.getId());

        // when // then
        assertThat(optionalUser).isPresent();

        // 의도적으로 에러를 발생시키는 테스트
        Throwable thrown = catchThrowable(() -> {
            assertThat(optionalUser.get()).isInstanceOf(Trainer.class);
        });

        // 에러가 발생했는지 확인
        assertThat(thrown).isInstanceOf(AssertionError.class)
            .hasMessageContaining("to be an instance of")
            .hasMessageContaining("Trainer");
    }

    @DisplayName("트레이너 저장 후 UserRepository 로 조회 시 TRAINER Type 확인")
    @Test
    void isTrainerByUserRepository() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));

        // when
        Optional<User> optionalUser = userRepository.findById(trainer.getId());

        // then
        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get()).isInstanceOf(Trainer.class);
    }

    @DisplayName("트레이너 저장 후 UserRepository 로 조회 시 MEMBER Type 검증하면 에러")
    @Test
    void isTrainerByUserRepositoryThrowError() {
        // given
        Trainer trainer = trainerRepository.save(createTrainer("트레이너"));
        Optional<User> optionalUser = userRepository.findById(trainer.getId());

        // when // then
        assertThat(optionalUser).isPresent();

        // 의도적으로 에러를 발생시키는 테스트
        Throwable thrown = catchThrowable(() -> {
            assertThat(optionalUser.get()).isInstanceOf(Member.class);
        });

        // 에러가 발생했는지 확인
        assertThat(thrown).isInstanceOf(AssertionError.class)
            .hasMessageContaining("to be an instance of")
            .hasMessageContaining("Member");
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
}