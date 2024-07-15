package com.sideproject.withpt.application.image;

import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.utils.AwsS3Uploader;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final AwsS3Uploader awsS3Uploader;
    private final ImageRepository imageRepository;

    public void uploadAndSaveImages(List<MultipartFile> files, Usages usage, String usageIdentificationId, Member member) {
        for (MultipartFile file : files) {
            String imageUrl = awsS3Uploader.upload(usage.toString(), member.getId() + "/" + usageIdentificationId, file);

            Image image = Image.builder()
                .member(member)
                .usageIdentificationId(usageIdentificationId)
                .usages(usage)
                .uploadDate(LocalDate.now())
                .url(imageUrl)
                .attachType(file.getContentType())
                .build();

            imageRepository.save(image);
        }
    }

    public void uploadAndSaveImages(List<MultipartFile> files, LocalDate uploadDate, Usages usage, Member member) {
        for (MultipartFile file : files) {
            String imageUrl = awsS3Uploader.upload(usage.toString(), "image", file);

            Image image = Image.builder()
                .usages(usage)
                .url(imageUrl)
                .uploadDate(uploadDate)
                .attachType(file.getContentType())
                .build();

            imageRepository.save(image);
        }
    }

    public void deleteImage(String url) {
        Image image = imageRepository.findByUrl(url).orElseThrow(() -> GlobalException.EMPTY_DELETE_FILE);

        awsS3Uploader.delete(image.getUsages().toString(), image.getUrl());
        imageRepository.deleteByUrl(url);
    }

}
