package com.example.brave_people_backend.upload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.upload.dto.UploadResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public UploadResponseDto uploadImg(MultipartFile file) throws IOException {
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

            return UploadResponseDto.builder().imgUrl(imgUrl).build();
        }

        throw new CustomException("파일 업로드 실패");
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
}
