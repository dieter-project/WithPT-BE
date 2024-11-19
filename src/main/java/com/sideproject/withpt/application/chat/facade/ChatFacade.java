package com.sideproject.withpt.application.chat.facade;

import com.sideproject.withpt.application.chat.contoller.request.MessageRequest;
import com.sideproject.withpt.application.chat.contoller.request.ReadMessageRequest;
import com.sideproject.withpt.application.chat.service.ChatService;
import com.sideproject.withpt.application.chat.service.MessageComponent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final ChatService chatService;
    private final MessageComponent messageComponent;

    public void sendMessage(MessageRequest request, LocalDateTime sentAt) {
        messageComponent.sendMessage(
            chatService.saveMessage(request.toDto(), sentAt), request.getRoomId()
        );
    }

    public void readMessage(ReadMessageRequest request) {
        messageComponent.sendMessage(
            chatService.readMessage(request), request.getRoomId()
        );
    }
}
