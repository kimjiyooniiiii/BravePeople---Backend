package com.example.brave_people_backend.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.brave_people_backend.entity.Email;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.member.dto.*;
import com.example.brave_people_backend.repository.EmailRepository;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailRepository emailRepository;
    private final AmazonS3 amazonS3;
    private final PasswordEncoder passwordEncoder;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //위치 정보 변경
    @Transactional
    public LocationResponseDto updateLocation(LocationRequestDto locationRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("회원을 찾을 수 없습니다."));

        //위도, 경도 변경
        findMember.changeLatAndLng(new BigDecimal(locationRequestDto.getLat()), new BigDecimal(locationRequestDto.getLng()));

        return LocationResponseDto.of(findMember);
    }

    // 프로필 페이지
    public ProfileResponseDto getProfileInfo(Long memberId) {
        return ProfileResponseDto.of(memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException("유효하지 않은 멤버ID"))
        );
    }

    // 프로필 이미지 변경
    @Transactional
    public ProfileImageResponseDto updateProfileImage(MultipartFile file) throws IOException {

        // file 확장자가 올바른지 확인
        if(getFileExtension(file)) {
            // S3 버킷 안의 profile 폴더 지정
            String filName = "profile/" + createFileName(file.getOriginalFilename());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3 저장
            amazonS3.putObject(bucket, filName, file.getInputStream(), metadata);

            String imgUrl = amazonS3.getUrl(bucket, filName).toString();
            Long memberId = SecurityUtil.getCurrentId();

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(()-> new CustomException("사용자를 찾을 수 없습니다."));

            // Member DB 업데이트
            member.changeProfileImage(imgUrl);

            return ProfileImageResponseDto.builder().profileImage(imgUrl).build();
        }

        throw new CustomException("파일 업로드에 실패했습니다.");
    }

    // 파일명을 난수화하기 위해 UUID를 활용하여 난수 생성
    public String createFileName(String originalFileName){
        return UUID.randomUUID().toString().concat(originalFileName);
    }

    // file 형식 확인
    private boolean getFileExtension(MultipartFile file){
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        return extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg");
    }

    //닉네임, 자기소개 변경
    @Transactional
    public UpdateProfileInfoResponseDto updateProfileInfo(UpdateProfileInfoRequestDto updateProfileInfoRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("회원을 찾을 수 없습니다."));

        //넘어온 닉네임이 null인지 체크
        if (updateProfileInfoRequestDto.getNickname() != null) {
            //닉네임 중복 체크
            if (memberRepository.existsByNickname(updateProfileInfoRequestDto.getNickname())) {
                throw new CustomException("닉네임 중복 오류");
            }
            //중복이 아니면 닉네임 변경
            findMember.changeNickname(updateProfileInfoRequestDto.getNickname());
        }

        if(updateProfileInfoRequestDto.getIntroduction() != null) {  //자기소개가 null이 아니고
            if (updateProfileInfoRequestDto.getIntroduction().isEmpty()) {     //빈 문자열이면
                findMember.changeIntroduction(null);    //자기소개 null로 변경
            }
            else {  //값이 있으면
                findMember.changeIntroduction(updateProfileInfoRequestDto.getIntroduction());
            }
        }

        return UpdateProfileInfoResponseDto.of(findMember);
    }

    //비밀번호 인증
    @Transactional
    public void reconfirmPassword(PwReconfirmRequestDto pwReconfirmRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("회원을 찾을 수 없습니다."));

        //passwordEncoder로 현재 입력한 비밀번호와 DB에 있는 비밀번호가 같은지 검사
        if (!passwordEncoder.matches(pwReconfirmRequestDto.getNowPassword(), findMember.getPw())) {
            throw new CustomException("잘못된 비밀번호 입니다.");
        }
    }

    //비밀번호 찾기
    // TODO 비밀번호 업데이트시 로그아웃 처리해야 함
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
                    .orElseThrow(() -> new CustomException("유효하지 않은 이메일ID"));
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
                .orElseThrow(() -> new CustomException("유효하지 않은 멤버ID"));

        // oldPassword와 newPassword가 일치할 시 예외 발생
        if (passwordEncoder.matches(updatePwRequestDto.getNewPassword(), findMember.getPw())) {
            throw new CustomException("기존 비밀번호와 새 비밀번호가 일치");
        }
        //member의 pw를 인코딩한 후 저장
        findMember.changePw(passwordEncoder.encode(updatePwRequestDto.getNewPassword()));
    }

    // 로그아웃
    @Transactional
    public void logout() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        if(member.getRefreshToken().isEmpty()){
            throw new CustomException("이미 로그아웃한 사용자입니다.");
        }
        // Refresh Token 삭제
        member.changeRefreshToken(null);
    }
}
