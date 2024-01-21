package com.example.brave_people_backend.controller;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    // refresh token으로 access token 재발급 받기
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }

    //이메일로 회원 찾기
    @GetMapping("/username")
    public ResponseEntity<UsernameResponseDto> email(@RequestParam("email") String email) {
        return ResponseEntity.ok(authService.findByEmail(email));
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

    // TODO signup 시 email_id 및 emailAddress를 통해 EMAIL 테이블의 authStatus = true 인지 확인로직 추가
    // TODO signup 완료 후 이메일 테이블 내용 삭제해야함

}
