package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.LocationRequestDto;
import com.example.brave_people_backend.dto.LocationResponseDto;
import com.example.brave_people_backend.dto.MemberResponseDto;
import com.example.brave_people_backend.dto.ProfileResponseDto;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

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
    @Transactional
    public LocationResponseDto updateLocation(LocationRequestDto locationRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."));

        //위도, 경도 변경
        findMember.changeLatAndLng(new BigDecimal(locationRequestDto.getLat()), new BigDecimal(locationRequestDto.getLng()));

        return LocationResponseDto.of(findMember);
    }

    public ProfileResponseDto getProfileInfo(Long memberId) {
        return ProfileResponseDto.of(memberRepository.findById(memberId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 멤버ID"))
        );
    }
}
