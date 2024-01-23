package com.example.brave_people_backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileInfoRequestDto {

    //request body에서 nickname, introduction을 String으로 받음
    String nickname;
    String introduction;
}
