package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.common.type.OAuthProvider;
import com.sideproject.withpt.common.type.Role;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {

    Role registerRole();
    OAuthProvider oAuthProvider();

    MultiValueMap<String, String> makeBody();
}
