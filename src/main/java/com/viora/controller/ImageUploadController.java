package com.viora.controller;

import com.viora.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ImageUploadController {

    private final S3UploadService s3UploadService;

    /**
     * 프론트엔드로부터 이미지 파일을 받아 S3에 업로드하고,
     * 저장된 파일의 URL을 JSON 형태로 반환합니다.
     * @param file form-data로 전송된 이미지 파일 (key 이름은 "image")
     * @return {"url": "https://..."} 형태의 JSON
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일이 비어있습니다."));
        }

        try {
            String fileUrl = s3UploadService.uploadFile(file);
            // 프론트엔드가 사용하기 쉽도록 JSON 객체로 감싸서 반환
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            // 서버 로그에 에러 기록
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 업로드 중 오류가 발생했습니다."));
        }
    }
}