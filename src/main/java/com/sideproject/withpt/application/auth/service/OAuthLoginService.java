package com.sideproject.withpt.application.auth.service;

import com.sideproject.withpt.application.auth.controller.dto.OAuthLoginResponse;
import com.sideproject.withpt.application.auth.infra.OAuthLoginParams;
import com.sideproject.withpt.application.type.Role;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OAuthLoginService {

    private final Map<Role, OAuthLoginClient> loginClients;

    public OAuthLoginService(List<OAuthLoginClient> clients) {
        this.loginClients = clients.stream().collect(
            Collectors.toUnmodifiableMap(OAuthLoginClient::role, Function.identity())
        );
    }

    public OAuthLoginResponse login(OAuthLoginParams params) {
        OAuthLoginClient oAuthLoginClient = loginClients.get(params.registerRole());
        return oAuthLoginClient.login(params);
    }

}