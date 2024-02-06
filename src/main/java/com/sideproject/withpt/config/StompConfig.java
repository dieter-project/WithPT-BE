package com.sideproject.withpt.config;

import com.sideproject.withpt.common.stomp.StompPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompPreHandler stompPreHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url => /ws-stomp
        registry.addEndpoint("/ws-stomp") // 연결될 엔드포인트
//            .setAllowedOrigins("*")
            .setAllowedOriginPatterns("*");
//            .withSockJS(); // SocketJS 를 연결한다는 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        // 메시지를 발행하는 요청 url => 메시지 보낼 때
        //  @MessageMapping 메서드로 라우팅된다.  Client에서 SEND 요청을 처리
        registry.setApplicationDestinationPrefixes("/pub");

        // 메시지를 구독하는 요청 url => 메시지 받을 때
//        registry.enableSimpleBroker("/sub");
        registry.enableStompBrokerRelay("/topic", "/exchange");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompPreHandler);
//    }
}