package com.sideproject.withpt.application.record.diet.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class DietRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DietRepository dietRepository;

    @DisplayName("식단 데이터가 존재하면 업로드 날짜와 회원 정보로 식단 조회 시 True 를 반환한다.")
    @Test
    void findByMemberAndUploadDate() {
        //given
        DietType dietType = DietType.Carb_Protein_Fat;
        Member member = saveMember(dietType);
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        saveDiets(dietType, member, uploadDate);

        //when
        Optional<Diets> optionalDiets = dietRepository.findByMemberAndUploadDate(member, uploadDate);

        //then
        assertThat(optionalDiets.isPresent()).isTrue();

        Diets findDiets = optionalDiets.get();
        assertThat(findDiets)
            .extracting("uploadDate", "targetDietType")
            .contains(uploadDate, dietType);
    }

    @DisplayName("업로드 날짜에 해당하는 식단 데이터가 존재하지 않을 수 있다.")
    @Test
    void findByMemberAndUploadDateReturnNull() {
        //given
        Member member = saveMember(DietType.Carb_Protein_Fat);
        LocalDate uploadDate = LocalDate.of(2024, 9, 3);

        //when
        Optional<Diets> optionalDiets = dietRepository.findByMemberAndUploadDate(member, uploadDate);

        //then
        assertThat(optionalDiets.isEmpty()).isTrue();
    }

    private Member saveMember(final DietType dietType) {
        Member member = Member.builder()
            .email("test@test.com")
            .name("member1")
            .dietType(dietType)
            .role(Role.MEMBER)
            .build();
        return memberRepository.save(member);
    }

    private Diets saveDiets(DietType dietType, Member member, LocalDate uploadDate) {
        Diets diets = Diets.builder()
            .member(member)
            .uploadDate(uploadDate)
            .totalCalorie(1200)
            .totalProtein(200)
            .totalCarbohydrate(300)
            .totalFat(400)
            .targetDietType(dietType)
            .build();

        return dietRepository.save(diets);
    }

}