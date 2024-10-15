package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.ImageDto;
import com.sideproject.withpt.common.type.UsageType;
import com.sideproject.withpt.domain.record.Image;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResponse {

    private Long id;
    private String usageIdentificationId;
    private UsageType usageType;
    private LocalDate uploadDate;
    private String url;
    private String attachType;

    @Builder
    private ImageResponse(Long id, String usageIdentificationId, UsageType usageType, LocalDate uploadDate, String url, String attachType) {
        this.id = id;
        this.usageIdentificationId = usageIdentificationId;
        this.usageType = usageType;
        this.uploadDate = uploadDate;
        this.url = url;
        this.attachType = attachType;
    }

    public static ImageResponse of(ImageDto imageDto) {
        return ImageResponse.builder()
            .id(imageDto.getId())
            .usageIdentificationId(imageDto.getUsageIdentificationId())
            .usageType(imageDto.getUsageType())
            .uploadDate(imageDto.getUploadDate())
            .url(imageDto.getUrl())
            .attachType(imageDto.getAttachType())
            .build();
    }

    public static ImageResponse of(Image image) {
        return ImageResponse.builder()
            .id(image.getId())
            .usageIdentificationId(image.getUsageIdentificationId())
            .usageType(image.getUsageType())
            .uploadDate(image.getUploadDate())
            .url(image.getUrl())
            .attachType(image.getAttachType())
            .build();
    }
}
