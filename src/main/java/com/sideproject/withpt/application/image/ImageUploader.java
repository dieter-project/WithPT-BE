package com.sideproject.withpt.application.image;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.domain.record.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final AwsS3Uploader awsS3Uploader;
    private final ImageRepository imageRepository;

    public void uploadAndSaveImages(List<MultipartFile> files, Long entityId, Usages usage) {
        for (MultipartFile file : files) {
            Image image = Image.builder()
                    .entityId(entityId)
                    .usage(usage)
                    .url(file.getOriginalFilename())
                    .attachType(file.getContentType())
                    .build();

            awsS3Uploader.upload(usage.toString(), "image", file);
            imageRepository.save(image);
        }
    }

    public void uploadAndSaveImages(List<MultipartFile> files, LocalDate uploadDate, Usages usage) {
        for (MultipartFile file : files) {
            Image image = Image.builder()
                    .usage(usage)
                    .url(file.getOriginalFilename())
                    .uploadDate(uploadDate)
                    .attachType(file.getContentType())
                    .build();

            awsS3Uploader.upload(usage.toString(), "image", file);
            imageRepository.save(image);
        }
    }

}
