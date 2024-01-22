package com.example.brave_people_backend.controller;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입 controller
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public void signup(@RequestBody @Valid SignupRequestDto signupRequestDto){
        authService.signup(signupRequestDto);
    }

    // 로그인 controller
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    // refresh token으로 access token 재발급 받기
    @PostMapping("/reissue")
    public TokenDto reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return authService.reissue(tokenRequestDto);
    }

    //이메일로 회원 찾기
    @GetMapping("/username")
    public UsernameResponseDto email(@RequestParam("email") String email) {
        return authService.findByEmail(email);
    }
    
    //이메일 중복확인 및 메일 전송
    @GetMapping(value = "/email", produces = "application/json")
    public String emailCheckAndSendMail(@RequestParam("address") String emailAddress) {
        Long emailId = authService.emailCheckAndSendMail(emailAddress);

        return "{\"emailId\": " + emailId + "}";
    }

    //이메일 인증 링크 클릭시 프론트에 링크 전송 후 받는 컨트롤러 메서드
    @GetMapping("/code-confirm")
    public String codeConfirm(@RequestParam("id") Long emailId, @RequestParam("code") int authCode) {
        return authService.codeConfirm(emailId, authCode);
    }

    //비밀번호 찾기
    @GetMapping("/pw")
    public void findPasswordAndSendMail(@RequestParam("username") String username, @RequestParam("email") String email) {
        authService.findPasswordAndSendMail(username, email);
    }
}
