package com.example.brave_people_backend.member.controller;

import com.example.brave_people_backend.member.dto.*;
import com.example.brave_people_backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // 프로필 페이지
    @GetMapping("/profile/{memberId}")
    public ProfileResponseDto getProfileInfo(@PathVariable("memberId") Long memberId) {
        return memberService.getProfileInfo(memberId);
    }

    //닉네임, 자기소개 수정
    @PatchMapping("/profile")
    public UpdateProfileInfoResponseDto updateProfileInfo(
            @Valid @RequestBody UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {
        return memberService.updateProfileInfo(updateProfileInfoRequestDto);
    }

    //비밀번호 인증
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/pw")
    public void reconfirmPassword(@RequestBody PwReconfirmRequestDto pwReconfirmRequestDto){
        memberService.reconfirmPassword(pwReconfirmRequestDto);
    }

    //비밀번호 재설정
    @PatchMapping("/pw")
    public void updatePassword(@RequestBody @Valid UpdatePwRequestDto updatePwRequestDto) {
        memberService.updatePassword(updatePwRequestDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public void logout() {
        memberService.logout();
    }
}
