package com.example.brave_people_backend.auth.controller;

import com.example.brave_people_backend.auth.dto.*;
import com.example.brave_people_backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    //이메일로 회원 아이디 찾기
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
    @GetMapping(value = "/code-confirm", produces = MediaType.TEXT_HTML_VALUE)
    public String codeConfirm(@RequestParam("id") Long emailId, @RequestParam("code") int authCode) {
        String message =  authService.codeConfirm(emailId, authCode);
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div style=\"margin:100px;\">\n" +
                "    <h1> 안녕하세요.</h1>\n" +
                "    <h1> 용감한원정대 BravePeople 입니다.</h1>\n" +
                "    <br>\n" +
                "    <div align=\"center\" style=\"border:1px solid black; font-family:verdana;\">\n" +
                "    <h3> " + message + "</h3>\n" +
                "    </div>\n" +
                "    <br/>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    //비밀번호 찾기
    @GetMapping("/pw")
    public void findPasswordAndSendMail(@RequestParam("username") String username, @RequestParam("email") String email) {
        authService.findPasswordAndSendMail(username, email);
    }

    //AWS health check용 api
    @GetMapping("/aws")
    public void awsHealthCheck() {}
}
