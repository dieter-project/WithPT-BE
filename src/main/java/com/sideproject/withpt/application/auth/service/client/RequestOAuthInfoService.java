package com.sideproject.withpt.application.auth.service.client;

import com.sideproject.withpt.application.auth.infra.AuthApiClient;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.common.type.AuthProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestOAuthInfoService {

    private final Map<AuthProvider, AuthApiClient> clients;

    public RequestOAuthInfoService(List<AuthApiClient> clients) {
        this.clients = clients.stream().collect(
            Collectors.toUnmodifiableMap(AuthApiClient::authProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(AuthLoginParams params) {
        AuthApiClient client = clients.get(params.authProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestAuthInfo(accessToken);
    }
}
