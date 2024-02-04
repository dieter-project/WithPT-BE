package com.sideproject.withpt.application.chat.contoller;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.service.ChatService;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/room/create")
    public ApiSuccessResponse<CreateRoomResponse> createRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal Long loginId,
        @Valid @RequestBody CreateRoomRequest request) {

        Role loginRole = getLoginRole();
        log.info("채팅을 시작하는 유저 정보 {} {}", loginId, loginRole);
        log.info("채팅을 요청 받은 유저 정보 {}", request);

        request.validationIdentifier(loginId, loginRole);

        return ApiSuccessResponse.from(
            chatService.createRoom(loginId, loginRole, request)
        );
    }

    private Role getLoginRole() {
        return Role.valueOf(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(s -> s.contains("TRAINER") || s.contains("MEMBER"))
            .collect(Collectors.joining())
            .split("_")[1]);
    }
}
