package com.sideproject.withpt.application.record.body.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.body.controller.response.WeightInfoResponse;
import com.sideproject.withpt.application.record.body.controller.response.WeightInfoResponse.BodyInfoResponse;
import com.sideproject.withpt.application.record.body.repository.BodyRepository;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class BodyServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BodyRepository bodyRepository;

    @Autowired
    private BodyService bodyService;

    @DisplayName("요청 날짜의 체중 및 신체 정보 조회")
    @Test
    void findWeightInfo() {
        // given
        Member member = saveMember(75.0);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);

        saveWeight(member, 88.3, LocalDate.of(2024, 9, 2));
        saveWeight(member, 87.3, LocalDate.of(2024, 9, 3));
        saveBodyInfo(member, 100, 200, 300, LocalDate.of(2024, 9, 7));
        saveBody(member, 90.0, 100, 200, 300, LocalDate.of(2024, 9, 8));

        // when
        WeightInfoResponse weightInfoResponse = bodyService.findWeightInfo(member.getId(), requestUploadDate);

        // then

        assertThat(weightInfoResponse.getWeights()).hasSize(2)
            .extracting("weight", "recentUploadDate")
            .containsExactly(
                tuple(90.0, LocalDate.of(2024, 9, 8)),
                tuple(87.3, LocalDate.of(2024, 9, 3))
            );

        BodyInfoResponse bodyInfo = weightInfoResponse.getBodyInfo();
        assertThat(bodyInfo.getRecentUploadDate()).isEqualTo(LocalDate.of(2024, 9, 8));
    }

    @DisplayName("요청 날짜의 신체 정보가 없을 경우 기본 값을 반환한다.")
    @Test
    void findWeightInfo2() {
        // given
        Member member = saveMember(75.0);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);

        saveWeight(member, 88.3, LocalDate.of(2024, 9, 2));
        saveWeight(member, 87.3, LocalDate.of(2024, 9, 3));

        // when
        WeightInfoResponse weightInfoResponse = bodyService.findWeightInfo(member.getId(), requestUploadDate);

        // then

        assertThat(weightInfoResponse.getWeights()).hasSize(2)
            .extracting("weight", "recentUploadDate")
            .containsExactly(
                tuple(87.3, LocalDate.of(2024, 9, 3)),
                tuple(88.3, LocalDate.of(2024, 9, 2))
            );

        assertThat(weightInfoResponse.getBodyInfo())
            .extracting("skeletalMuscle", "bodyFatPercentage", "bmi", "recentUploadDate")
            .contains(0.0, 0.0, 0.0, null);
    }

    @DisplayName("요청 날짜의 체중 정보가 없을 경우 기본 값을 반환한다.")
    @Test
    void findWeightInfo3() {
        // given
        Member member = saveMember(75.0);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);

        saveBodyInfo(member, 100, 200, 300, LocalDate.of(2024, 9, 7));

        // when
        WeightInfoResponse weightInfoResponse = bodyService.findWeightInfo(member.getId(), requestUploadDate);

        // then

        assertThat(weightInfoResponse.getWeights()).isEmpty();
        assertThat(weightInfoResponse.getBodyInfo())
            .extracting("skeletalMuscle", "bodyFatPercentage", "bmi", "recentUploadDate")
            .contains(100.0, 200.0, 300.0, LocalDate.of(2024, 9, 7));
    }

    public Body saveBody(Member member, double weight, double skeletalMuscle, double bodyFatPercentage, double bmi, LocalDate uploadDate) {
        return bodyRepository.save(
            Body.builder()
                .member(member)
                .targetWeight(member.getTargetWeight())
                .weight(weight)
                .skeletalMuscle(skeletalMuscle)
                .bodyFatPercentage(bodyFatPercentage)
                .bmi(bmi)
                .uploadDate(uploadDate)
                .build()
        );
    }

    public Body saveWeight(Member member, double weight, LocalDate uploadDate) {
        return bodyRepository.save(
            Body.builder()
                .member(member)
                .targetWeight(member.getTargetWeight())
                .weight(weight)
                .uploadDate(uploadDate)
                .build()
        );
    }

    public Body saveBodyInfo(Member member, double skeletalMuscle, double bodyFatPercentage, double bmi, LocalDate uploadDate) {
        return bodyRepository.save(
            Body.builder()
                .member(member)
                .targetWeight(member.getTargetWeight())
                .skeletalMuscle(skeletalMuscle)
                .bodyFatPercentage(bodyFatPercentage)
                .bmi(bmi)
                .uploadDate(uploadDate)
                .build()
        );
    }

    private Member saveMember(double targetWeight) {
        return memberRepository.save(
            Member.builder()
                .targetWeight(targetWeight)
                .email("test@test.com")
                .name("member1")
                .dietType(DietType.DIET)
                .role(Role.MEMBER)
                .build()
        );
    }
}