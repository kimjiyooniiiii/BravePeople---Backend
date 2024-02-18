package com.example.brave_people_backend.chat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    @NotNull
    private int score;
    @Size(max = 200)
    private String contents;
}
