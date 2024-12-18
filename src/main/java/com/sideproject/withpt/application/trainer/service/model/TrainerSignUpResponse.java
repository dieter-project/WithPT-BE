package com.sideproject.withpt.application.trainer.service.model;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrainerSignUpResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final AuthProvider oAuthProvider;
    private final Role role;

    @Builder
    private TrainerSignUpResponse(Long id, String email, String name, AuthProvider oAuthProvider, Role role) {
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
            .oAuthProvider(trainer.getAuthProvider())
            .role(trainer.getRole())
            .build();
    }
}
