package com.sideproject.withpt.domain;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Gender;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private LocalDate birth;

    private Gender gender;

    private String nickname;

    private Double height;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private DietType dietType;

    private int targetExerciseTimes;

    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

}
