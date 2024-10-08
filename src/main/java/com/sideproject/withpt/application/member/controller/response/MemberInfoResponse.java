package com.sideproject.withpt.application.member.controller.response;

import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.LoginType;
import com.sideproject.withpt.common.type.OAuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.member.SocialLogin;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class MemberInfoResponse {

    private Long id;
    private String email;
    private OAuthProvider oauthProvider;
    private LoginType loginType;
    private String name;
    private Double height;
    private Double weight;
    private LocalDate birth;
    private Sex sex;
    private String imageUrl;
    private DietType dietType;
    private ExerciseFrequency exerciseFrequency;
    private Double targetWeight;
    private Role role;
    private LocalDateTime joinDate;
    private LocalDateTime lastModifiedDate;

    public static MemberInfoResponse of(Member member, Authentication authentication, SocialLogin socialLogin) {
        return MemberInfoResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .height(member.getHeight())
            .weight(member.getWeight())
            .imageUrl(member.getImageUrl())
            .dietType(member.getDietType())
            .exerciseFrequency(member.getExerciseFrequency())
            .targetWeight(member.getTargetWeight())
            .role(member.getRole())
            .oauthProvider(socialLogin.getOauthProvider())
            .birth(authentication.getBirth())
            .sex(authentication.getSex())
            .loginType(authentication.getLoginType())
            .joinDate(authentication.getJoinDate())
            .lastModifiedDate(member.getLastModifiedDate())
            .build();
    }
}
