package com.sideproject.withpt.application.auth.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.pt.repository.model.AssignedPTInfoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_EMPTY)
public class LoginResponse {

    private AuthLoginResponse loginInfo;
    private List<AssignedPTInfoResponse> ptInfos;

    @Builder
    private LoginResponse(AuthLoginResponse loginInfo, List<AssignedPTInfoResponse> ptInfos) {
        this.loginInfo = loginInfo;
        this.ptInfos = ptInfos;
    }

    public static LoginResponse of(AuthLoginResponse loginInfo) {
        return LoginResponse.builder()
            .loginInfo(loginInfo)
            .build();
    }

    public static LoginResponse of(AuthLoginResponse loginInfo, List<AssignedPTInfoResponse> ptInfos) {
        return LoginResponse.builder()
            .loginInfo(loginInfo)
            .ptInfos(ptInfos)
            .build();
    }
}
