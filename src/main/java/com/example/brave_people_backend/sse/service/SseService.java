package com.example.brave_people_backend.sse.service;

import com.example.brave_people_backend.enumclass.NotificationType;
import com.example.brave_people_backend.sse.dto.NotificationDto;
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
            emitter.complete();
        });
        emitter.onError((ex) -> {
            emitters.remove(clientId);
            emitter.complete();        });
        emitter.onTimeout(() -> {
            emitters.remove(clientId);
            emitter.complete();
        });

        emitters.put(clientId, emitter);
        try {
            emitter.send(SseEmitter.event().data(NotificationDto.of(NotificationType.CONNECT, null)));
        }
        catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    //서버에서 client로 event 전송
    public void sendEventToClient(NotificationType type, Long clientId, String message) {
        SseEmitter emitter = emitters.get(clientId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(NotificationDto.of(type, message)));
            }
            catch(IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
