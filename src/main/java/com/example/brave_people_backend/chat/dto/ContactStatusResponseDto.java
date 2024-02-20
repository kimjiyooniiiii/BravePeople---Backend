package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.enumclass.ContactStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContactStatusResponseDto {
    private ContactStatus status;
    private Boolean isActive; //버튼 활성화 여부

    public static ContactStatusResponseDto of(ContactStatus status, boolean isActive) {
        return ContactStatusResponseDto.builder()
                .status(status)
                .isActive(isActive)
                .build();
    }
}
