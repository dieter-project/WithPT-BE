package com.sideproject.withpt.application.trainer.service.dto;

import com.sideproject.withpt.common.type.OAuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.trainer.Trainer;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrainerSignUpResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final OAuthProvider oAuthProvider;
    private final Role role;

    @Builder
    private TrainerSignUpResponse(Long id, String email, String name, OAuthProvider oAuthProvider, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.oAuthProvider = oAuthProvider;
        this.role = role;
    }

    public static TrainerSignUpResponse of(Trainer trainer) {
        return TrainerSignUpResponse.builder()
            .id(trainer.getId())
            .email(trainer.getEmail())
            .name(trainer.getName())
            .oAuthProvider(trainer.getOauthProvider())
            .role(trainer.getRole())
            .build();
    }
}
