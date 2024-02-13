package com.example.brave_people_backend.sse.service;

import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.sse.dto.NewChatRoomNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SseService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    public SseEmitter stream(Long clientId) {

        SseEmitter emitter = new SseEmitter(120L * 1000 * 60);  //SseEmitter 지속 시간을 120분으로 설정

        //emitter가 연결 완료 , 오류 , 타임아웃일 때 emitters에서 remove
        emitter.onCompletion(() -> {
            emitters.remove(clientId);
            cleanupEmitter(emitter);
        });
        emitter.onError((ex) -> {
            emitters.remove(clientId);
            cleanupEmitter(emitter);
        });
        emitter.onTimeout(() -> {
            emitters.remove(clientId);
            cleanupEmitter(emitter);
        });

        emitters.put(clientId, emitter);
        return emitter;
    }

    //서버에서 client로 event 전송
    public void sendEventToClient(Long clientId, Long roomId) {
        SseEmitter emitter = emitters.get(clientId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(NewChatRoomNotificationDto.of(roomId)));
            }
            catch(IOException e) {
                emitter.completeWithError(e);
            }
        }
    }

    private void cleanupEmitter(SseEmitter emitter) {
        try {
            emitter.complete();
        }
        catch (Exception e) {
            throw new CustomException(String.valueOf(emitters.size()), "cleanupEmitter()");
        }
    }
}
