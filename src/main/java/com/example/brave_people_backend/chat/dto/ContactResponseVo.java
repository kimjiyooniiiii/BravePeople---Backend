package com.example.brave_people_backend.chat.dto;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.enumclass.ContactStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContactResponseVo {
    private ContactStatus status;
    private Boolean isActive;

    public static ContactResponseVo of(Contact contact) {
        return ContactResponseVo.builder()
                .status(ContactStatus.대기중)
                .isActive(true)
                .build();
    }
}
