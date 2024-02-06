package com.sideproject.withpt.application.chat.contoller;

import com.sideproject.withpt.application.chat.contoller.request.CreateRoomRequest;
import com.sideproject.withpt.application.chat.contoller.request.MessageRequest;
import com.sideproject.withpt.application.chat.contoller.request.ReadMessageRequest;
import com.sideproject.withpt.application.chat.contoller.response.CreateRoomResponse;
import com.sideproject.withpt.application.chat.contoller.response.MessageResponse;
import com.sideproject.withpt.application.chat.contoller.response.RoomListResponse;
import com.sideproject.withpt.application.chat.facade.ChatFacade;
import com.sideproject.withpt.application.chat.service.ChatService;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;
    private final ChatFacade chatFacade;

    @Operation(summary = "채팅방 생성")
    @PostMapping("/chat/room/create")
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

    @Operation(summary = "채팅방 리스트 조회")
    @GetMapping("/chat/rooms")
    public ApiSuccessResponse<RoomListResponse> getRoomList(@Parameter(hidden = true) @AuthenticationPrincipal Long loginId) {

        return ApiSuccessResponse.from(
            chatService.getRoomList(loginId, getLoginRole())
        );
    }

    @Operation(summary = "채팅 내역 조회")
    @GetMapping("/chat/rooms/{roomId}")
    public ApiSuccessResponse<List<MessageResponse>> getChattingList(@PathVariable Long roomId,
        @RequestParam(required = false, defaultValue = "9999999999") Long cursor) {
        log.info("room {}, cursor {}", roomId, cursor);

        return ApiSuccessResponse.from(
            chatService.getChattingList(roomId, cursor)
        );
    }

    @Operation(summary = "메세지 전송")
    @MessageMapping("chat/sendMessage")
    public void sendMessage(@Payload MessageRequest request) {
        log.info("CHAT {}", request);
        chatFacade.sendMessage(request);
    }

    @Operation(summary = "메세지 읽기")
    @MessageMapping("chat/readMessage")
    public void readMessage(@Payload ReadMessageRequest request) {
        chatFacade.readMessage(request);
    }

    private static final String CHAT_QUEUE_NAME = "chat.queue";
    //receive()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그용도)
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public <T> void receive(T t){
        System.out.println("[x] Received : " + t);
        log.info("[x] Received : {}", t);
    }

    private Role getLoginRole() {
        return Role.valueOf(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(s -> s.contains("TRAINER") || s.contains("MEMBER"))
            .collect(Collectors.joining())
            .split("_")[1]);
    }
}
