package com.example.brave_people_backend.controller;

import com.example.brave_people_backend.dto.MemberResponseDto;
import com.example.brave_people_backend.service.MemberService;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/my")
    public ResponseEntity<MemberResponseDto> findMemberInfoByMemberId() {
        // SecurityContext에 있는 유저 꺼내기
        return ResponseEntity.ok(memberService.findMemberInfoByMemberId(SecurityUtil.getCurrentId()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<MemberResponseDto> findMemberInfoByUsername(@PathVariable String username) {
        return ResponseEntity.ok(memberService.findMemberInfoByUsername(username));
    }
}
