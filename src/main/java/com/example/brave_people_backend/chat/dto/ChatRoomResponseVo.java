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
                //상대방이 A면, 나는 B이므로 B의 읽음 여부 반환, 반대 경우도 마찬가지
                .isRead(other == chatRoom.getMemberA() ? chatRoom.isBIsRead() : chatRoom.isAIsRead())
                .status(chatRoom.getContact().getContactStatus())
                .build();
    }
}
