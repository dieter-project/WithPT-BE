package com.sideproject.withpt.application.auth.service.trainer;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.OAuthLoginParams;
import com.sideproject.withpt.application.auth.service.OAuthLoginClient;
import com.sideproject.withpt.application.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerOAuthLoginClient implements OAuthLoginClient {

    @Override
    public Role role() {
        return Role.TRAINER;
    }

    @Override
    public OAuthLoginResponse login(OAuthLoginParams params) {
        return null;
    }
}
