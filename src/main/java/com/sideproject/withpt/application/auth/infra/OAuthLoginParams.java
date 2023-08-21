package com.sideproject.withpt.application.auth.infra;

import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {

    Role registerRole();
    OAuthProvider oAuthProvider();

    MultiValueMap<String, String> makeBody();
}
