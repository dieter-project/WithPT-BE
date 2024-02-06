package com.sideproject.withpt.application.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageComponent {

    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY_PREFIX = "room.";

    private final RabbitTemplate rabbitTemplate;

    public <T> void sendMessage(T t, Long roomId) {
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, ROUTING_KEY_PREFIX + roomId, t);
    }

}
