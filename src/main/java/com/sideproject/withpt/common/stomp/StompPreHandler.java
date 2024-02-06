package com.sideproject.withpt.common.stomp;

import static com.sideproject.withpt.common.jwt.model.constants.JwtConstants.TOKEN_HEADER;

import com.sideproject.withpt.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@RequiredArgsConstructor
public class StompPreHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("StompPreHandler 실행!!!!!!!");

        if(accessor.getCommand() == StompCommand.CONNECT) {
            jwtTokenProvider.isValidationToken(accessor.getFirstNativeHeader(TOKEN_HEADER));

//            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아온다
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            // SecurityContext 에 Authentication 객체를 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            log.info(
//                String.format("[%s] -> %s", this.jwtTokenProvider.extractSubject(token),
//                    request.getRequestURI())
//            );
        }
        return message;
    }
}
