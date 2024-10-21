package com.sideproject.withpt.application.trainer.service;


import static com.sideproject.withpt.common.type.AuthProvider.KAKAO;
import static com.sideproject.withpt.common.type.Role.TRAINER;
import static com.sideproject.withpt.common.type.Sex.MAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sideproject.withpt.application.academic.repository.AcademicRepository;
import com.sideproject.withpt.application.award.repository.AwardRepository;
import com.sideproject.withpt.application.career.repository.CareerRepository;
import com.sideproject.withpt.application.certificate.repository.CertificateRepository;
import com.sideproject.withpt.application.education.repository.EducationRepository;
import com.sideproject.withpt.application.gym.repositoy.GymRepository;
import com.sideproject.withpt.application.gymtrainer.repository.GymTrainerRepository;
import com.sideproject.withpt.application.schedule.repository.WorkScheduleRepository;
import com.sideproject.withpt.application.trainer.repository.TrainerRepository;
import com.sideproject.withpt.application.trainer.service.dto.TrainerSignUpResponse;
import com.sideproject.withpt.application.trainer.service.dto.complex.GymScheduleDto;
import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import com.sideproject.withpt.application.trainer.service.dto.single.WorkScheduleDto;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.redis.RedisClient;
import com.sideproject.withpt.common.type.AcademicInstitution;
import com.sideproject.withpt.common.type.Day;
import com.sideproject.withpt.common.type.Degree;
import com.sideproject.withpt.common.type.EmploymentStatus;
import com.sideproject.withpt.domain.gym.Gym;
import com.sideproject.withpt.domain.gym.GymTrainer;
import com.sideproject.withpt.domain.user.trainer.Academic;
import com.sideproject.withpt.domain.user.trainer.Award;
import com.sideproject.withpt.domain.user.trainer.Career;
import com.sideproject.withpt.domain.user.trainer.Certificate;
import com.sideproject.withpt.domain.user.trainer.Education;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import com.sideproject.withpt.domain.gym.WorkSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class TrainerAuthenticationServiceTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private AcademicRepository academicRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private EducationRepository educationRepository;

    @MockBean
    private RedisClient redisClient;

    @Autowired
    private TrainerAuthenticationService TrainerAuthenticationService;

    @DisplayName("트레이너 회원 가입 - 모든 정보 입력했을 때")
    @Test
    void registerTrainerWithGymsAndSchedulesWithAllDetails() {
        // given
        List<CareerDto> careers = createCareerDtoList();
        List<AcademicDto> academics = createAcademicDtoList();
        List<CertificateDto> certificates = createCertificateDtoList();
        List<AwardDto> awards = createAwardDtoList();
        List<EducationDto> educations = createEducationDtoList();

        WorkScheduleDto MON = createWorkScheduleDto(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0));
        WorkScheduleDto TUE = createWorkScheduleDto(Day.TUE, LocalTime.of(12, 0), LocalTime.of(22, 0));
        WorkScheduleDto WED = createWorkScheduleDto(Day.WED, LocalTime.of(10, 0), LocalTime.of(18, 0));
        WorkScheduleDto THU = createWorkScheduleDto(Day.THU, LocalTime.of(10, 0), LocalTime.of(18, 0));
        WorkScheduleDto FRI = createWorkScheduleDto(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0));
        List<WorkScheduleDto> workSchedules = List.of(MON, TUE, WED, THU, FRI);

        GymScheduleDto gymSchedule = createTrainerGymDto("아자아자 피트니스 센터", workSchedules);

        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email("test@test.com")
            .name("test")
            .birth(LocalDate.of(1994, 7, 19))
            .sex(MAN)
            .authProvider(KAKAO)
            .careers(careers)
            .academics(academics)
            .certificates(certificates)
            .awards(awards)
            .educations(educations)
            .gyms(List.of(gymSchedule))
            .build();

        // when
        TrainerSignUpResponse response = TrainerAuthenticationService.registerTrainerWithGymsAndSchedules(trainerSignUpDto);

        // then
        assertThat(response)
            .extracting("email", "name", "oAuthProvider", "role")
            .contains("test@test.com", "test", KAKAO, TRAINER);

        List<WorkSchedule> workScheduleList = workScheduleRepository.findAll();
        assertThat(workScheduleList).hasSize(5);

        List<GymTrainer> gymTrainerList = gymTrainerRepository.findAll();
        assertThat(gymTrainerList).hasSize(1);

        GymTrainer gymTrainer = gymTrainerList.get(0);
        assertThat(gymTrainer.getTrainer()).isNotNull();
        assertThat(gymTrainer.getGym()).isNotNull();

        List<Gym> gymList = gymRepository.findAll();
        assertThat(gymList).isNotEmpty();

        List<Career> careerList = careerRepository.findAll();
        assertThat(careerList).isNotEmpty();

        List<Academic> academicList = academicRepository.findAll();
        assertThat(academicList).isNotEmpty();

        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).isNotEmpty();

        List<Award> awardList = awardRepository.findAll();
        assertThat(awardList).isNotEmpty();

        List<Education> educationList = educationRepository.findAll();
        assertThat(educationList).isNotEmpty();
    }

    @DisplayName("트레이너 회원 가입 시 (경력, 자격증, 수상, 교육, 학력사항) 모두 입력하지 않아도 가입이 가능하다.")
    @Test
    void registerTrainerWithGymsAndSchedulesWithoutAllDetails() {
        // given
        List<CareerDto> careers = Collections.emptyList();
        List<AcademicDto> academics = Collections.emptyList();
        List<AwardDto> awards = Collections.emptyList();
        List<CertificateDto> certificates = Collections.emptyList();
        List<EducationDto> educations = Collections.emptyList();

        WorkScheduleDto MON = createWorkScheduleDto(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0));
        WorkScheduleDto FRI = createWorkScheduleDto(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0));
        List<WorkScheduleDto> workSchedules = List.of(MON, FRI);

        GymScheduleDto gymSchedule = createTrainerGymDto("아자아자 피트니스 센터", workSchedules);

        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email("test@test.com")
            .name("test")
            .birth(LocalDate.of(1994, 7, 19))
            .sex(MAN)
            .authProvider(KAKAO)
            .careers(careers)
            .academics(academics)
            .certificates(certificates)
            .awards(awards)
            .educations(educations)
            .gyms(List.of(gymSchedule))
            .build();

        // when
        TrainerSignUpResponse response = TrainerAuthenticationService.registerTrainerWithGymsAndSchedules(trainerSignUpDto);

        // then
        assertThat(response)
            .extracting("email", "name", "oAuthProvider", "role")
            .contains("test@test.com", "test", KAKAO, TRAINER);

        List<WorkSchedule> workScheduleList = workScheduleRepository.findAll();
        assertThat(workScheduleList).hasSize(2);

        List<GymTrainer> gymTrainerList = gymTrainerRepository.findAll();
        assertThat(gymTrainerList).hasSize(1);

        GymTrainer gymTrainer = gymTrainerList.get(0);
        assertThat(gymTrainer.getTrainer()).isNotNull();
        assertThat(gymTrainer.getGym()).isNotNull();

        List<Gym> gymList = gymRepository.findAll();
        assertThat(gymList).isNotEmpty();

        List<Career> careerList = careerRepository.findAll();
        assertThat(careerList).isEmpty();

        List<Academic> academicList = academicRepository.findAll();
        assertThat(academicList).isEmpty();

        List<Award> awardList = awardRepository.findAll();
        assertThat(awardList).isEmpty();

        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).isEmpty();

        List<Education> educationList = educationRepository.findAll();
        assertThat(educationList).isEmpty();
    }

    /**
     * (경력,자격증,수상,교육,학력사항) 모든 조합이 32가지 이므로 1가지 경우로 테스트
     */
    @DisplayName("트레이너 회원 가입 시 (경력,자격증,수상,교육,학력사항) 중 하나라도 입력하지 않아도 가입이 가능하다.")
    @Test
    void registerTrainerWithGymsAndSchedulesWithOptionalDetails() {
        // given
        List<CareerDto> careers = createCareerDtoList();
        List<AcademicDto> academics = createAcademicDtoList();
        List<AwardDto> awards = createAwardDtoList();
        List<CertificateDto> certificates = Collections.emptyList();
        List<EducationDto> educations = Collections.emptyList();

        WorkScheduleDto MON = createWorkScheduleDto(Day.MON, LocalTime.of(10, 0), LocalTime.of(18, 0));
        WorkScheduleDto FRI = createWorkScheduleDto(Day.FRI, LocalTime.of(12, 0), LocalTime.of(22, 0));
        List<WorkScheduleDto> workSchedules = List.of(MON, FRI);

        GymScheduleDto gymSchedule = createTrainerGymDto("아자아자 피트니스 센터", workSchedules);

        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email("test@test.com")
            .name("test")
            .birth(LocalDate.of(1994, 7, 19))
            .sex(MAN)
            .authProvider(KAKAO)
            .careers(careers)
            .academics(academics)
            .certificates(certificates)
            .awards(awards)
            .educations(educations)
            .gyms(List.of(gymSchedule))
            .build();

        // when
        TrainerSignUpResponse response = TrainerAuthenticationService.registerTrainerWithGymsAndSchedules(trainerSignUpDto);

        // then
        assertThat(response)
            .extracting("email", "name", "oAuthProvider", "role")
            .contains("test@test.com", "test", KAKAO, TRAINER);

        List<WorkSchedule> workScheduleList = workScheduleRepository.findAll();
        assertThat(workScheduleList).hasSize(2);

        List<GymTrainer> gymTrainerList = gymTrainerRepository.findAll();
        assertThat(gymTrainerList).hasSize(1);

        GymTrainer gymTrainer = gymTrainerList.get(0);
        assertThat(gymTrainer.getTrainer()).isNotNull();
        assertThat(gymTrainer.getGym()).isNotNull();

        List<Gym> gymList = gymRepository.findAll();
        assertThat(gymList).isNotEmpty();

        List<Career> careerList = careerRepository.findAll();
        assertThat(careerList).isNotEmpty();

        List<Academic> academicList = academicRepository.findAll();
        assertThat(academicList).isNotEmpty();

        List<Award> awardList = awardRepository.findAll();
        assertThat(awardList).isNotEmpty();

        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).isEmpty();

        List<Education> educationList = educationRepository.findAll();
        assertThat(educationList).isEmpty();
    }

    @DisplayName("Email 이 같을 경우 에러를 응답한다.")
    @Test
    void test() {
        // given
        String email = "test@test.com";

        Trainer trainer = createTrainer(email);
        trainerRepository.save(trainer);

        TrainerSignUpDto trainerSignUpDto = TrainerSignUpDto.builder()
            .email(email)
            .build();

        // when // then
        assertThatThrownBy(() -> TrainerAuthenticationService.registerTrainerWithGymsAndSchedules(trainerSignUpDto))
            .isInstanceOf(GlobalException.class)
            .hasMessage("이미 가입된 회원 입니다.");
    }

    private Trainer createTrainer(String email) {
        return Trainer.signUpBuilder()
            .email(email)
            .name("test")
            .build();
    }

    private GymScheduleDto createTrainerGymDto(String name, List<WorkScheduleDto> workSchedules) {
        return GymScheduleDto.builder()
            .name(name)
            .address("경기도 김포시 풍무동 231-413")
            .latitude(3.143151)
            .longitude(4.151661)
            .workSchedules(workSchedules)
            .build();
    }

    private WorkScheduleDto createWorkScheduleDto(Day day, LocalTime inTime, LocalTime outTime) {
        return WorkScheduleDto.builder()
            .day(day)
            .inTime(inTime)
            .outTime(outTime)
            .build();
    }

    private static List<EducationDto> createEducationDtoList() {
        return List.of(EducationDto.builder()
            .name("교육명")
            .institution("교육기관")
            .acquisitionYearMonth(YearMonth.of(2023, 10))
            .build());
    }

    private static List<AwardDto> createAwardDtoList() {
        return List.of(AwardDto.builder()
            .name("수상명")
            .institution("수상 기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 8))
            .build());
    }

    private static List<CertificateDto> createCertificateDtoList() {
        return List.of(CertificateDto.builder()
            .name("자격증명")
            .institution("기관명")
            .acquisitionYearMonth(YearMonth.of(2023, 3))
            .build());
    }

    private static List<AcademicDto> createAcademicDtoList() {
        return List.of(AcademicDto.builder()
            .name("학교명")
            .major("전공")
            .institution(AcademicInstitution.FOUR_YEAR_UNIVERSITY)
            .degree(Degree.BACHELOR)
            .country("한국")
            .enrollmentYearMonth(YearMonth.of(2015, 2))
            .graduationYearMonth(YearMonth.of(2020, 3))
            .build());
    }

    private static List<CareerDto> createCareerDtoList() {
        return List.of(CareerDto.builder()
            .centerName("센터명")
            .jobPosition("직책")
            .status(EmploymentStatus.EMPLOYED)
            .startOfWorkYearMonth(YearMonth.of(2022, 1))
            .endOfWorkYearMonth(YearMonth.of(2023, 12))
            .build());
    }
}