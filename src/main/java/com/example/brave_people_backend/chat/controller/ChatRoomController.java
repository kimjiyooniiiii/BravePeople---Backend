package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.chat.dto.ChatResponseDto;
import com.example.brave_people_backend.chat.dto.ChatRoomResponseVo;
import com.example.brave_people_backend.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 리스트 불러오기
    @GetMapping
    public List<ChatRoomResponseVo> getChatRoomList() {
        return chatRoomService.getChatRoomList();
    }

    // 기존 채팅내역 불러오기
    @GetMapping("/{roomId}")
    public ChatResponseDto getChatList(@PathVariable("roomId") Long roomId) {
        return chatRoomService.getChatList(roomId);
    }

    // 채팅방 나가기
    @PatchMapping("/{roomId}")
    public void exitChatRoom(@PathVariable("roomId") Long roomId) { chatRoomService.exitChatRoom(roomId); }
}
