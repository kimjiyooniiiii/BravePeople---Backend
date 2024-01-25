package com.example.brave_people_backend.upload.controller;

import com.example.brave_people_backend.upload.dto.UploadResponseDto;
import com.example.brave_people_backend.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/image")
    public UploadResponseDto uploadImg(@RequestPart("file") MultipartFile file) throws IOException {
        return uploadService.uploadImg(file);
    }
}
