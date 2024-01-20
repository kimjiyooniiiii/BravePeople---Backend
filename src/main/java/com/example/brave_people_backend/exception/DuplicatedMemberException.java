package com.example.brave_people_backend.exception;

// 회원가입 시, 중복된 Member가 있으면 발생하는 예외
public class DuplicatedMemberException  extends RuntimeException{

    public DuplicatedMemberException() {
        super("중복된 회원입니다");
    }
}
