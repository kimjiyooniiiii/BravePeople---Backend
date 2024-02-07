package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.enumclass.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    private Long senderId;
    private String message;
    private String img;

}
