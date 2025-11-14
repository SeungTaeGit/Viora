package com.viora.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * 파일을 S3에 업로드하고, 파일에 접근할 수 있는 URL을 반환합니다.
     * @param file 업로드할 파일
     ** @return S3에 저장된 파일의 공개 URL
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        s3Template.upload(bucketName, uniqueFilename, file.getInputStream());

        String s3UrlFormat = "https://%s.s3.%s.amazonaws.com/%s";
        return String.format(s3UrlFormat, bucketName, region, uniqueFilename);
    }
}