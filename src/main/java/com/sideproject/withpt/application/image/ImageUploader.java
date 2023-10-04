package com.sideproject.withpt.application.image;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.domain.record.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final AwsS3Uploader awsS3Uploader;
    private final ImageRepository imageRepository;

    public void uploadAndSaveImages(List<MultipartFile> files, Long entityId, Usages usage) {
        for (MultipartFile file : files) {
            Image image = Image.builder()
                    .entity_id(entityId)
                    .usage(usage)
                    .url(file.getOriginalFilename())
                    .attach_type(file.getContentType())
                    .build();

            awsS3Uploader.upload(usage.toString(), "image", file);
            imageRepository.save(image);
        }
    }

}
