package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.board.ChronoUtil;
import com.example.brave_people_backend.entity.Chat;
import com.example.brave_people_backend.entity.ChatRoom;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.ContactStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponseVo implements Comparable<ChatRoomResponseVo> {
    private Long roomId;
    private String lastChat;
    private String lastSendAt;
    private String otherProfileImg;
    private String otherNickname;
    private Long otherId;
    private Boolean isRead;
    private ContactStatus status;
    private LocalDateTime time;

    public static ChatRoomResponseVo of(ChatRoom chatRoom, Member other, Chat chat, boolean isRead, ContactStatus contactStatus) {
        return ChatRoomResponseVo.builder()
                .roomId(chatRoom.getChatRoomId())
                .lastChat(chat.getMessage())
                .lastSendAt(ChronoUtil.formatDateTime(chat.getSendAt()))
                .otherProfileImg(other.getProfileImg())
                .otherNickname(other.getNickname())
                .otherId(other.getMemberId())
                //상대방이 A면, 나는 B이므로 B의 읽음 여부 반환, 반대 경우도 마찬가지
                .isRead(isRead)
                .status(contactStatus)
                .time(chat.getSendAt())
                .build();
    }

    //최신순 정렬을 위한 비교
    @Override
    public int compareTo(ChatRoomResponseVo o) {
        return o.getTime().compareTo(this.getTime());
    }
}
