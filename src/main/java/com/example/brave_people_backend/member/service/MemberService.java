package com.example.brave_people_backend.member.service;

import com.example.brave_people_backend.board.dto.PostListVo;
import com.example.brave_people_backend.entity.Email;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.member.dto.*;
import com.example.brave_people_backend.repository.BoardRepository;
import com.example.brave_people_backend.repository.EmailRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;

    //위치 정보 변경
    @Transactional
    public LocationResponseDto updateLocation(LocationRequestDto locationRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        //위도, 경도 변경
        findMember.changeLatAndLng(new BigDecimal(locationRequestDto.getLat()), new BigDecimal(locationRequestDto.getLng()));

        return LocationResponseDto.of(findMember);
    }

    // 프로필 페이지
    public ProfileResponseDto getProfileInfo(Long memberId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "postId"); //POST 테이블의 post_id 기준 내림차순 정렬 설정
        PageRequest pageRequest = PageRequest.of(0, 5, sort); //출력할 page와 amount 및 sort 기준 설정 (pageable 구현체)

        return ProfileResponseDto.of(memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException("존재하지 않는 멤버ID")),
                boardRepository.findPostListByProfilePage(memberId, pageRequest).map(PostListVo::of).toList()
        );
    }

    //닉네임, 자기소개, 프로필 이미지 변경
    @Transactional
    public UpdateProfileInfoResponseDto updateProfileInfo(UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        if (memberRepository.existsByNickname(updateProfileInfoRequestDto.getNickname())
            && !findMember.getNickname().equals(updateProfileInfoRequestDto.getNickname())) {
            throw new CustomException("닉네임 중복");
        }
        findMember.changeProfileInfo(updateProfileInfoRequestDto.getNickname(),
                                    updateProfileInfoRequestDto.getIntroduction(),
                                    updateProfileInfoRequestDto.getProfileImg());

        return UpdateProfileInfoResponseDto.of(findMember);
    }

    //비밀번호 인증
    @Transactional
    public void reconfirmPassword(PwReconfirmRequestDto pwReconfirmRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        //passwordEncoder로 현재 입력한 비밀번호와 DB에 있는 비밀번호가 같은지 검사
        if (!passwordEncoder.matches(pwReconfirmRequestDto.getNowPassword(), findMember.getPw())) {
            throw new CustomException("잘못된 비밀번호");
        }
    }

    //비밀번호 찾기
    @Transactional
    public void updatePassword(UpdatePwRequestDto updatePwRequestDto) {

        Long currentMemberId = SecurityUtil.getCurrentId();

        // 비로그인 상태에서 비밀번호 찾기로 재설정 시
        if (currentMemberId == null) {
            // RequestDto에 emailId나 authCode가 넘어오지 않은 경우 에러
            if (updatePwRequestDto.getEmailId() == null || updatePwRequestDto.getAuthCode() == 0) {
                throw new CustomException("입력 파라미터 부족");
            }

            // Email 엔티티 불러오고 인증코드 일치여부 확인
            Email emailEntity = emailRepository.findById(updatePwRequestDto.getEmailId())
                    .orElseThrow(() -> new CustomException("존재하지 않는 이메일ID"));
            if (emailEntity.getAuthCode() != updatePwRequestDto.getAuthCode()) {
                throw new CustomException("일치하지 않은 인증코드");
            }

            // 이메일 주소로 Member 엔티티를 불러오고 없을 시 예외 발생
            currentMemberId = memberRepository.findByEmail(emailEntity.getEmailAddress())
                    .orElseThrow(() -> new CustomException("이메일 주소로 회원을 찾을 수 없음"))
                    .getMemberId();

            // 인증코드 일치 여부 확인했으므로 EMAIL 테이블에서 삭제
            emailRepository.deleteByEmailAddress(emailEntity.getEmailAddress());
        }
        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        // oldPassword와 newPassword가 일치할 시 예외 발생
        if (passwordEncoder.matches(updatePwRequestDto.getNewPassword(), findMember.getPw())) {
            throw new CustomException("기존 비밀번호와 새 비밀번호가 일치");
        }
        //member의 pw를 인코딩한 후 저장
        findMember.changePw(passwordEncoder.encode(updatePwRequestDto.getNewPassword()));
        // Refresh Token 삭제, 로그아웃 처리
        findMember.changeRefreshToken(null);
    }

    // 로그아웃
    @Transactional
    public void logout() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

        if(member.getRefreshToken().isEmpty()){
            throw new CustomException("이미 로그아웃한 사용자");
        }
        // Refresh Token 삭제
        member.changeRefreshToken(null);
    }
}
