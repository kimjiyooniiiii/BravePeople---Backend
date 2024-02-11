package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.board.ChronoUtil;
import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.ContactStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseVo {
    private Long roomId;
    private String lastChat;
    private String lastSendAt;
    private String otherProfileImg;
    private String otherNickname;
    private Long otherId;
    private boolean isRead;
    private ContactStatus status;

    public static ChatRoomResponseVo of(ChatRoom chatRoom, Member other, Chat chat) {
        return ChatRoomResponseVo.builder()
                .roomId(chatRoom.getChatRoomId())
                .lastChat(chat.getMessage())
                .lastSendAt(ChronoUtil.formatDateTime(chat.getSendAt()))
                .otherProfileImg(other.getProfileImg())
                .otherNickname(other.getNickname())
                .otherId(other.getMemberId())
                //마지막 채팅 보낸 사람이 상대방이면 chat.isRead() 아니면(마지막 채팅이 나면) false
                .isRead(!chat.getSenderId().equals(other.getMemberId()) || chat.isRead())
                .status(chatRoom.getContact().getContactStatus())
                .build();
    }
}
