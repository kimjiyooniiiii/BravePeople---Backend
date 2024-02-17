package com.example.brave_people_backend.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

// Client로 보낼 Error Message 생성
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    public StompErrorHandler() { super(); }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        String exMessage = ex.getCause().getMessage();

        if(exMessage.equals("JwtExpiredError") || exMessage.equals("MessageError") || exMessage.equals("NotCorrectJwt")) {
            return errorMessage(exMessage);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // STOMP ErrorMessage 생성
    private Message<byte[]> errorMessage(String errorMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorMessage);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
