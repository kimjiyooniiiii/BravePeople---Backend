package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.LocationRequestDto;
import com.example.brave_people_backend.dto.LocationResponseDto;
import com.example.brave_people_backend.dto.MemberResponseDto;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 임시 테스트 서비스 입니다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // 기본키로 Member 찾기
    public MemberResponseDto findMemberInfoByMemberId(Long memberId){
        return memberRepository.findById(memberId)
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }

    // 가입 아이디로 Member 찾기
    public MemberResponseDto findMemberInfoByUsername(String username) {
        return memberRepository.findByUsername(username)
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }

    //위치 정보 변경
    public LocationResponseDto updateLocation(LocationRequestDto locationRequestDto) {

        return LocationResponseDto.of(memberRepository.findByIdOne(SecurityUtil.getCurrentId()),
                locationRequestDto.getLat(), locationRequestDto.getLng());
    }

}
