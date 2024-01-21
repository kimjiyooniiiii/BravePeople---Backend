package com.example.brave_people_backend.service;

import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

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

    //닉네임, 자기소개 변경
    @Transactional
    public UpdateProfileInfoResponseDto updateProfileInfo(UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."));

        //넘어온 닉네임이 null인지 체크
        if (updateProfileInfoRequestDto.getNickname() != null) {
            //닉네임 중복 체크
            if (memberRepository.existsByNickname(updateProfileInfoRequestDto.getNickname())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 중복 오류");
            }
            //중복이 아니면 닉네임 변경
            findMember.changeNickname(updateProfileInfoRequestDto.getNickname());
        }
        //넘어온 자기소개가 null이 아니면
        if (updateProfileInfoRequestDto.getIntroduction() != null) {
            //자기소개 변경
            findMember.changeIntroduction(updateProfileInfoRequestDto.getIntroduction());
        }

        return UpdateProfileInfoResponseDto.of(findMember);
    }
}