package com.example.brave_people_backend.websocket;

import com.example.brave_people_backend.jwt.TokenProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            // Access Token 가져오기
            String authorization = String.valueOf(accessor.getNativeHeader("Authorization"));
            // Access Token이 빈칸일 때
            if(authorization == null) {
                log.error("Access Token이 비었습니다.");
                throw new MalformedJwtException("JwtError");
            }

            StompCommand command = accessor.getCommand();
            // Stomp 상태가 ERROR 일 때
            if(command.equals(StompCommand.ERROR)) { throw new MessageDeliveryException("MessageError"); }

            // Stomp 상태가 CONNECT 일 때, Token 유효성 검사
            else if(command.equals(StompCommand.CONNECT)) {
                String token = authorization.replace("Bearer ", "").replace("[","").replace("]","");

                // Token 유효성 검사 성공 시, UserDetails 객체 생성
                if(tokenProvider.validateToken(token)) { this.setAuthentication(accessor, token); }
            }
        } catch (JwtException e) {
            String eMessage = e.getMessage();
            if(eMessage.equals("토큰 기한 만료")) {
                log.error("Access Token 기한 만료");
                throw new MalformedJwtException("JwtExpiredError");
            } else {
                log.error("Not Correct JWT Token");
                throw new MalformedJwtException("NotCorrectJwt");
            }
        } catch (MessageDeliveryException e) {
            log.error("Stomp Message Error");
            throw new MessageDeliveryException("MessageError");
        }

        return message;
    }

    // Token 복호화하여 UserDetails 객체 생성
    private void setAuthentication(StompHeaderAccessor accessor, String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }
}
