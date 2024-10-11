package com.sideproject.withpt.application.auth.infra.google;

import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Getter
@NoArgsConstructor
public class GoogleLoginParams implements AuthLoginParams {

    private String authorizationCode;

    @ValidEnum(regexp = "MEMBER|TRAINER", enumClass = Role.class)
    private Role role;

    @Override
    public Role registerRole() {
        return role;
    }

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        log.info("code = {}", authorizationCode);
        body.add("code", authorizationCode);
        return body;
    }
}
