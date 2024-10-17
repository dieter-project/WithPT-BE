package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import org.springframework.util.MultiValueMap;

public interface AuthLoginParams {

    default String email() {
        return "";
    };

    default String password() {
        return "";
    };
    Role registerRole();

    AuthProvider authProvider();

    default MultiValueMap<String, String> makeBody() {
        return null;
    };
}
