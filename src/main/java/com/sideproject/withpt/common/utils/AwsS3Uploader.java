package com.sideproject.withpt.common.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sideproject.withpt.common.exception.GlobalException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    // 파일 확장자 구분선
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String SEPARATOR = "_";

    public Map<String, String> upload(String directory, String subDirectory, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw GlobalException.EMPTY_FILE;
        }

        String fileName = buildFileName(directory, subDirectory,
            Objects.requireNonNull(multipartFile.getOriginalFilename()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            log.info("이미지 업로드 : " + fileName);
            // S3에 업로드
            amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw GlobalException.FILE_UPLOAD_FAILED;
        }

        return Map.of(
            "url", amazonS3Client.getUrl(bucket, fileName).toString(),
            "uploadUrlPath", fileName
        );
    }

    public void delete(String directory, String imageUrl) {
        try {
            String objectKey = imageUrl.substring(imageUrl.indexOf(directory));
            log.info("objectKey {}", objectKey);
            amazonS3Client.deleteObject(bucket, objectKey);
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
            throw GlobalException.FILE_DELETE_FAILED;
        }
    }

    public String buildFileName(String directory, String category, String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return directory + "/" + category + "/" + fileName + SEPARATOR + now + fileExtension;
    }
}
