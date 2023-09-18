package com.sideproject.withpt.application.trainer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerGymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.application.type.Day;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.jwt.AuthTokenGenerator;
import com.sideproject.withpt.common.jwt.model.dto.TokenSetDto;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.config.TestEmbeddedRedisConfig;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    GymRepository gymRepository;

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

        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email("test@naver.com")
            .name("test")
            .birth(LocalDate.of(1994, 10, 19))
            .sex(Sex.MAN)
            .oauthProvider(OAuthProvider.KAKAO)
            .careers(createCareerDtos())
            .academics(createAcademicDtos())
            .certificates(createCertificateDtos())
            .awards(createAwardDtos())
            .educations(createEducationDtos())
            .gyms(createTrainerGymScheduleDtos())
            .build();

        Trainer trainer = Trainer.builder()
            .id(100L)
            .email("test@naver.com")
            .name("test")
            .birth(LocalDate.of(1994, 10, 19))
            .sex(Sex.MAN)
            .careers(CareerDto.toEntities(createCareerDtos()))
            .certificates(CertificateDto.toEntities(createCertificateDtos()))
            .build();

        Gym gym = Gym.builder()
            .id(1L)
            .name("체육관" + 0)
            .address("주소 : " + 0)
            .latitude(3.131515)
            .longitude(10.5452)
            .build();

        given(trainerRepository.findByEmail(trainerSignUpDto.getEmail()))
            .willReturn(Optional.empty());

        given(gymRepository.findByName(any()))
            .willReturn(Optional.ofNullable(gym));

        given(trainerRepository.save(any()))
            .willReturn(trainer);

        given(authTokenGenerator.generateTokenSet(any(), any()))
            .willReturn(
                TokenSetDto.of("access", "refresh", "Bearer ", 1800L, 604800L)
            );

        //when
        TokenSetDto tokenSetDto = trainerService.signUp(trainerSignUpDto);

        //then
        assertThat(tokenSetDto.getAccessToken()).isEqualTo("access");
        assertThat(tokenSetDto.getRefreshToken()).isEqualTo("refresh");

    }

    @Test
    @DisplayName("이미 트레이너가 존재하면 실패")
    public void signUpTrainer_already_registered() {
        //given
        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email("test@naver.com")
            .name("test")
            .birth(LocalDate.of(1994, 10, 19))
            .sex(Sex.MAN)
            .oauthProvider(OAuthProvider.KAKAO)
            .careers(createCareerDtos())
            .academics(createAcademicDtos())
            .certificates(createCertificateDtos())
            .awards(createAwardDtos())
            .educations(createEducationDtos())
            .gyms(createTrainerGymScheduleDtos())
            .build();

        Trainer registeredTrainer = Trainer.builder()
            .id(1L)
            .email("test@naver.com")
            .name("test")
            .sex(Sex.MAN)
            .build();

        given(trainerRepository.findByEmail(trainerSignUpDto.getEmail()))
            .willReturn(Optional.of(registeredTrainer));

        assertThatThrownBy(
            () -> trainerService.signUp(trainerSignUpDto)
        )
            .isExactlyInstanceOf(GlobalException.class)
            .hasMessage(GlobalException.ALREADY_REGISTERED_USER.getMessage());

    }

    private static List<CareerDto> createCareerDtos() {
        List<CareerDto> careerDtos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            careerDtos.add(
                CareerDto.builder()
                    .centerName("센터 " + i)
                    .startOfWorkYearMonth(YearMonth.of(2022, i + 1))
                    .endOfWorkYearMonth(YearMonth.of(2024, i + 1))
                    .build()
            );
        }
        return careerDtos;
    }

    private static List<AcademicDto> createAcademicDtos() {
        List<AcademicDto> academicDtos = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            academicDtos.add(
                AcademicDto.builder()
                    .name("학교 " + i)
                    .major("전공 " + i)
                    .enrollmentYear(Year.of(2018))
                    .graduationYear(Year.of(2024))
                    .build()
            );
        }
        return academicDtos;
    }

    private static List<CertificateDto> createCertificateDtos() {
        List<CertificateDto> certificateDtos = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            certificateDtos.add(
                CertificateDto.builder()
                    .name("자격증 " + i)
                    .acquisitionYearMonth(YearMonth.of(2023, i))
                    .build()
            );
        }
        return certificateDtos;
    }

    private static List<AwardDto> createAwardDtos() {
        List<AwardDto> awardDtos = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            awardDtos.add(
                AwardDto.builder()
                    .name("상 " + i)
                    .institution("기관 " + i)
                    .acquisitionYear(Year.of(202 + i))
                    .build()
            );
        }
        return awardDtos;
    }

    private static List<EducationDto> createEducationDtos() {
        List<EducationDto> educationDtos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            educationDtos.add(
                EducationDto.builder()
                    .name("교육 " + i)
                    .acquisitionYearMonth(YearMonth.of(2022, i + 1))
                    .build()
            );
        }
        return educationDtos;
    }

    private static List<TrainerGymScheduleDto> createTrainerGymScheduleDtos() {
        List<TrainerGymScheduleDto> gymScheduleDtos = new ArrayList<>();
        List<WorkScheduleDto> workScheduleDtos = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            workScheduleDtos.add(
                WorkScheduleDto.builder()
                    .day(Day.MON)
                    .inTime(LocalTime.of(9, 30))
                    .outTime(LocalTime.of(22, 0))
                    .build()
            );
        }

        for (int i = 0; i < 3; i++) {

            gymScheduleDtos.add(
                TrainerGymScheduleDto.builder()
                    .name("체육관" + i)
                    .address("주소 : " + i)
                    .latitude(3.131515)
                    .longitude(10.5452)
                    .workSchedules(workScheduleDtos)
                    .build()
            );
        }
        return gymScheduleDtos;
    }
}