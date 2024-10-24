package com.sideproject.withpt.application.auth.infra.google;

import com.sideproject.withpt.application.auth.infra.AuthApiClient;
import com.sideproject.withpt.application.auth.infra.OAuthInfoResponse;
import com.sideproject.withpt.application.auth.infra.AuthLoginParams;
import com.sideproject.withpt.application.auth.infra.google.GoogleInfoResponse.GoogleAccount;
import com.sideproject.withpt.common.type.AuthProvider;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleClient implements AuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";

    @Value("${oauth.google.url.auth}")
    private String authTokenUrl;

    @Value("${oauth.google.url.api}")
    private String apiUrl;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.secret}")
    private String clientSecret;

    @Value("${oauth.google.url.redirect}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    @Override
    public AuthProvider authProvider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public String requestAccessToken(AuthLoginParams params) {
        String url = authTokenUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        GoogleTokens response = restTemplate.postForObject(url, request, GoogleTokens.class);

        assert response != null;
        log.info("토큰 응답 : {}", response.toString());
        return response.getIdToken();
    }


    @Override
    public OAuthInfoResponse requestAuthInfo(String accessToken) {
        final String payloadJWT = accessToken.split("\\.")[1];

        Decoder urlDecoder = Base64.getUrlDecoder();
        final String payload = new String(urlDecoder.decode(payloadJWT));

        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        log.info("정보 : {}", jsonArray);

        return GoogleInfoResponse.builder()
            .googleAccount(GoogleAccount.builder().email(jsonArray.get("email").toString()).build())
            .build();
    }
}
