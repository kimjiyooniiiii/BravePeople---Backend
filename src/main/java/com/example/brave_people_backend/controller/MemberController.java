package com.example.brave_people_backend.controller;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    //위치 변경
    @PatchMapping("/location")
    public ResponseEntity<LocationResponseDto> updateLocation(@RequestBody LocationRequestDto locationRequestDto) {
        return ResponseEntity.ok(memberService.updateLocation(locationRequestDto));
    }

    @GetMapping("/profile")
    public ProfileResponseDto getProfileInfo(@RequestParam("memberid") Long memberId) {
        return memberService.getProfileInfo(memberId);
    }

    //닉네임, 자기소개 수정
    @PatchMapping("/profile")
    public UpdateProfileInfoResponseDto updateProfileInfo(@RequestBody UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {
        return memberService.updateProfileInfo(updateProfileInfoRequestDto);
    }
}
