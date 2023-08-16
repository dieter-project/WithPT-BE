package com.sideproject.withpt.application.member.dto.response;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Gender;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.domain.Member;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpResponse {

    private String email;
    private String name;
    private LocalDate birth;
    private Gender gender;
    private String nickname;
    private Double height;
    private Double weight;
    private DietType dietType;
    private int targetExerciseTimes;
    private Double targetWeight;
    private OAuthProvider oauthProvider;

    public static MemberSignUpResponse from(Member member){
        return MemberSignUpResponse.builder()
            .email(member.getEmail())
            .name(member.getName())
            .birth(member.getBirth())
            .gender(member.getGender())
            .nickname(member.getNickname())
            .height(member.getHeight())
            .weight(member.getWeight())
            .dietType(member.getDietType())
            .targetExerciseTimes(member.getTargetExerciseTimes())
            .targetWeight(member.getTargetWeight())
            .oauthProvider(member.getOauthProvider())
            .build();
    }
}
