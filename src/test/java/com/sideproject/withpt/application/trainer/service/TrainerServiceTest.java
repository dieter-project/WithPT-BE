package com.sideproject.withpt.application.trainer.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest;
import com.sideproject.withpt.application.trainer.controller.dto.TrainerSignUpRequest.CareerDto;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.trainer.Career;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

@ExtendWith(MockitoExtension.class)
@Import(TestEmbeddedRedisConfig.class)
class TrainerServiceTest {


    @Mock
    TrainerRepository trainerRepository;

    @Mock
    AuthTokenGenerator authTokenGenerator;

    @Mock
    RedisClient redisClient;

    @InjectMocks
    TrainerService trainerService;

    @Test
    @DisplayName("트레이너 가입 성공")
    public void signUpTrainer() {
        //given
        List<CareerDto> careerDtos = new ArrayList<>();
        List<Career> careerList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CareerDto careerDto = CareerDto.builder()
                .centerName("test" + i)
                .yearOfService(LocalDate.parse("2022-10-30"))
                .build();
            careerDtos.add(careerDto);
            careerList.add(careerDto.toEntity());
        }

        TrainerSignUpRequest request = TrainerSignUpRequest.builder()
            .email("test@naver.com")
            .name("test")
            .sex(Sex.MAN)
            .careers(careerDtos)
            .build();

        Trainer trainer = Trainer.builder()
            .id(1L)
            .email("test@naver.com")
            .name("test")
            .sex(Sex.MAN)
            .careers(careerList)
            .build();

        given(trainerRepository.findByEmail(request.getEmail()))
            .willReturn(Optional.empty());

        given(trainerRepository.save(any()))
            .willReturn(trainer);

        given(authTokenGenerator.generateTokenSet(any(), any()))
            .willReturn(
                TokenSetDto.of("access", "refresh", "Bearer ", 1800L, 604800L)
            );

        //when
        TokenSetDto tokenSetDto = trainerService.signUp(request);

        //then
        assertThat(tokenSetDto.getAccessToken()).isEqualTo("access");
        assertThat(tokenSetDto.getRefreshToken()).isEqualTo("refresh");

    }

    @Test
    @DisplayName("이미 트레이너가 존재하면 실패")
    public void signUpTrainer_already_registered() {
        //given
        TrainerSignUpRequest request = TrainerSignUpRequest.builder()
            .email("test@naver.com")
            .name("test")
            .sex(Sex.MAN)

            .build();

        Trainer registeredTrainer = Trainer.builder()
            .id(1L)
            .email("test@naver.com")
            .name("test")
            .sex(Sex.MAN)
            .build();

        given(trainerRepository.findByEmail(request.getEmail()))
            .willReturn(Optional.of(registeredTrainer));

        assertThatThrownBy(
            () -> trainerService.signUp(request)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.ALREADY_REGISTERED_USER.getMessage());

    }

   
}