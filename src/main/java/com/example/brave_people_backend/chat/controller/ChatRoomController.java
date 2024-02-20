package com.example.brave_people_backend.chat.controller;

import com.example.brave_people_backend.chat.dto.ChatResponseDto;
import com.example.brave_people_backend.chat.dto.ChatRoomResponseVo;
import com.example.brave_people_backend.chat.dto.ContactStatusResponseDto;
import com.example.brave_people_backend.chat.dto.ReviewRequestDto;
import com.example.brave_people_backend.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 리스트 불러오기
    @GetMapping("/chats")
    public List<ChatRoomResponseVo> getChatRoomList() {
        return chatRoomService.getChatRoomList();
    }

    // 기존 채팅내역 불러오기
    @GetMapping("/chats/{roomId}")
    public ChatResponseDto getChatList(@PathVariable("roomId") Long roomId) {
        return chatRoomService.getChatList(roomId);
    }

    @GetMapping("/contact/{roomId}")
    public ContactStatusResponseDto acceptContact(@PathVariable("roomId") Long roomId) {
        return chatRoomService.acceptContact(roomId);
    }

    @GetMapping("/contact/{roomId}/cancel")
    public ContactStatusResponseDto cancelContact(@PathVariable("roomId") Long roomId) {
        return chatRoomService.cancelContact(roomId);
    }

    @GetMapping("/contact/{roomId}/finish")
    public ContactStatusResponseDto finishContact(@PathVariable("roomId") Long roomId) {
        return chatRoomService.finishContact(roomId);
    }

    @PostMapping("/contact/{roomId}/review")
    public void reviewContact(@PathVariable("roomId") Long roomId,
                              @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        chatRoomService.reviewContact(roomId, reviewRequestDto);
    }

    @GetMapping("/contact/{roomId}/status")
    public ContactStatusResponseDto getContactStatus(@PathVariable("roomId") Long roomId) {
        return chatRoomService.getContactStatus(roomId);
    }
}
