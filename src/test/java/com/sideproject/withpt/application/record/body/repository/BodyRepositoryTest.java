package com.sideproject.withpt.application.record.body.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.List;
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
class BodyRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BodyRepository bodyRepository;

    @DisplayName("가장 최근에 저장된 체중을 최대 2개까지 조회할 수 있다.")
    @Test
    void findRecentWeightBy() {
        // given
        Member member = saveMember(75.0);

        LocalDate uploadDate1 = LocalDate.of(2024, 8, 31);
        saveWeight(member, 86.3, uploadDate1);

        LocalDate uploadDate2 = LocalDate.of(2024, 9, 7);
        saveWeight(member, 88.3, uploadDate2);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);
        saveWeight(member, 87.3, requestUploadDate);

        // when
        List<Body> bodies = bodyRepository.findLatestWeightsBy(member, requestUploadDate);

        // then
        assertThat(bodies).hasSize(2)
            .extracting("weight", "uploadDate")
            .containsExactly(
                tuple(87.3, requestUploadDate),
                tuple(88.3, uploadDate2)
            );
    }

    @DisplayName("가장 최근에 저장된 체중 데이터가 1개일 경우 조회 가능하다.")
    @Test
    void findRecentWeightBy2() {
        // given
        Member member = saveMember(75.0);

        LocalDate uploadDate1 = LocalDate.of(2024, 8, 31);
        saveWeight(member, 86.3, uploadDate1);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);

        // when
        List<Body> bodies = bodyRepository.findLatestWeightsBy(member, requestUploadDate);

        // then
        assertThat(bodies).hasSize(1)
            .extracting("weight", "uploadDate")
            .contains(
                tuple(86.3, uploadDate1)
            );
    }

    @DisplayName("가장 최근에 저장된 체중 데이터 없는 경우")
    @Test
    void findRecentWeightByWhenIsEmpty() {
        // given
        Member member = saveMember(75.0);
        LocalDate requestUploadDate = LocalDate.of(2024, 9, 8);

        // when
        List<Body> bodies = bodyRepository.findLatestWeightsBy(member, requestUploadDate);

        // then
        assertThat(bodies).isEmpty();
    }

    @DisplayName("가장 최근에 저장된 신체 데이터를 조회한다.")
    @Test
    void findLatestBodyInfoBy() {
        // given
        Member member = saveMember(75.0);

        LocalDate targetDateTime = LocalDate.of(2024, 9, 8);
        saveBodyInfo(member, 100, 200, 300, targetDateTime);

        LocalDate requestUploadDate = LocalDate.of(2024, 9, 9);
        // when
        Optional<Body> optionalBody = bodyRepository.findLatestBodyInfoBy(member, requestUploadDate);

        // then
        assertThat(optionalBody).isPresent();
        assertThat(optionalBody.get())
            .extracting("skeletalMuscle", "bodyFatPercentage", "bmi", "uploadDate")
            .contains(100.0, 200.0, 300.0, targetDateTime);
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