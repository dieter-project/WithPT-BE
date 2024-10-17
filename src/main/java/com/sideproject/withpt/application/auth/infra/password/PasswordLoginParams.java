package com.sideproject.withpt.application.auth.infra.password;

import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
public class PasswordLoginParams implements AuthLoginParams {

    private String email;

    private String password;

    @ValidEnum(regexp = "MEMBER|TRAINER", enumClass = Role.class)
    private Role role;

    @Builder
    private PasswordLoginParams(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public Role registerRole() {
        return role;
    }

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.EMAIL;
    }
}
