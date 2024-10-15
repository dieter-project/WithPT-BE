package com.sideproject.withpt.application.image;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final AwsS3Uploader awsS3Uploader;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.url}")
    private String awsUrl;

    public void uploadAndSaveImages(List<MultipartFile> files, UsageType usage, String usageIdentificationId, Member member) {
        for (MultipartFile file : files) {
            Map<String, String> imageUrl = awsS3Uploader.upload(usage.toString(), member.getId() + "/" + usageIdentificationId, file);
            String fullPath = awsUrl + "/" + imageUrl.get("uploadUrlPath");

            Image image = Image.builder()
                .member(member)
                .usageIdentificationId(usageIdentificationId)
                .usageType(usage)
                .uploadDate(LocalDate.now())
                .url(imageUrl.get("url"))
                .uploadUrlPath(fullPath)
                .attachType(file.getContentType())
                .build();

            imageRepository.save(image);
        }
    }

    public void uploadAndSaveImages(List<MultipartFile> files, UsageType usage, LocalDate uploadDate, Member member) {
        for (MultipartFile file : files) {
            Map<String, String> imageUrl = awsS3Uploader.upload(usage.toString(), member.getId() + "/" + uploadDate, file);
            String fullPath = awsUrl + "/" + imageUrl.get("uploadUrlPath");

            Image image = Image.builder()
                .member(member)
                .usageType(usage)
                .uploadDate(uploadDate)
                .url(imageUrl.get("url"))
                .uploadUrlPath(fullPath)
                .attachType(file.getContentType())
                .build();

            imageRepository.save(image);
        }
    }

    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
            .orElseThrow(() -> GlobalException.EMPTY_DELETE_FILE);

        awsS3Uploader.delete(image.getUsageType().toString(), image.getUploadUrlPath());
        imageRepository.deleteById(id);
    }

    public void deleteImageByIdentificationAndMember(String usageIdentificationId, Member member) {
        imageRepository.deleteAllByUsageIdentificationIdAndMember(usageIdentificationId, member);
    }

}
