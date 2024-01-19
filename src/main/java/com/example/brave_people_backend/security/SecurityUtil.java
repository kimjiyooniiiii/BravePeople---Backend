package com.example.brave_people_backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {}

    // SecurityContextHolder에 저장된 객체의 기본키 반환
    public static Long getCurrentId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //비로그인 사용자일 경우 null 반환
        if (authentication.getName().equals("anonymousUser")) {
            return null;
        }

        if(authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
