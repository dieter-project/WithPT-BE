package com.sideproject.withpt.application.auth.infra.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sideproject.withpt.application.auth.infra.OAuthLoginParams;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public class GoogleLoginParams implements OAuthLoginParams {

    private String authorizationCode;

    @ValidEnum(regexp = "MEMBER|TRAINER", enumClass = Role.class)
    private Role role;

    @Override
    public Role registerRole() {
        return role;
    }

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
