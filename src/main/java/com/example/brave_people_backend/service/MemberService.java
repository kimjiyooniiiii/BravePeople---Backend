package com.example.brave_people_backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.brave_people_backend.dto.*;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.MemberRepository;
import com.example.brave_people_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
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

    //비밀번호 인증
    @Transactional
    public void reconfirmPassword(PWReconfirmRequestDto pwReconfirmRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."));

        //passwordEncoder로 현재 입력한 비밀번호와 DB에 있는 비밀번호가 같은지 검사
        if (!passwordEncoder.matches(pwReconfirmRequestDto.getNowPassword(), findMember.getPw())) {
            throw new CustomException("잘못된 비밀번호 입니다.");
        }
    }

    //비밀번호 찾기 - 마이페이지
    @Transactional
    public void updatePwFromMypage(UpdatePwRequestDto updatePwRequestDto) {

        //토큰으로 현재 회원 검색, 없으면 예외처리
        Member findMember = memberRepository.findById(SecurityUtil.getCurrentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."));

        // 영문 + 숫자, 8자리 이상인지 확인
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$");
        Matcher matcher = pattern.matcher(updatePwRequestDto.getNewPassword());
        if (!matcher.find() || updatePwRequestDto.getNewPassword() == null) {
            throw new CustomException("비밀번호 구성 오류");
        }

        //member의 pw를 인코딩한 후 저장
        findMember.changePw(passwordEncoder.encode(updatePwRequestDto.getNewPassword()));
    }
}
