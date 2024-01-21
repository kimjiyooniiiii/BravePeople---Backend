package com.example.brave_people_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateProfileInfoRequestDto {

    //request body에서 nickname, introduction을 String으로 받음
    String nickname;
    String introduction;
}
