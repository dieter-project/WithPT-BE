package com.sideproject.withpt.application.trainer.controller.response;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class TrainerInfoResponse {

    private Long id;
    private String email;
    private String name;
    private String imageUrl;
    private LocalDate birth;
    private Sex sex;
    private AuthProvider oauthProvider;
    private Role role;
    private LocalDateTime joinDate;

    @Builder
    public TrainerInfoResponse(Long id, String email, String name, String imageUrl, LocalDate birth, Sex sex, AuthProvider oauthProvider, Role role, LocalDateTime joinDate) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
        this.oauthProvider = oauthProvider;
        this.role = role;
        this.joinDate = joinDate;
    }

    public static TrainerInfoResponse of(Trainer trainer) {
        return TrainerInfoResponse.builder()
            .id(trainer.getId())
            .email(trainer.getEmail())
            .name(trainer.getName())
            .imageUrl(trainer.getImageUrl())
            .birth(trainer.getBirth())
            .sex(trainer.getSex())
            .oauthProvider(trainer.getAuthProvider())
            .role(trainer.getRole())
            .joinDate(trainer.getJoinDate())
            .build();
    }
}
