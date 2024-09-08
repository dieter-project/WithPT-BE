package com.sideproject.withpt.application.record.diet.service.response;

import com.sideproject.withpt.application.record.diet.repository.response.ImageDto;
import com.sideproject.withpt.application.type.Usages;
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
    private Usages usages;
    private LocalDate uploadDate;
    private String url;
    private String attachType;

    @Builder
    private ImageResponse(Long id, String usageIdentificationId, Usages usages, LocalDate uploadDate, String url, String attachType) {
        this.id = id;
        this.usageIdentificationId = usageIdentificationId;
        this.usages = usages;
        this.uploadDate = uploadDate;
        this.url = url;
        this.attachType = attachType;
    }

    public static ImageResponse of(ImageDto imageDto) {
        return ImageResponse.builder()
            .id(imageDto.getId())
            .usageIdentificationId(imageDto.getUsageIdentificationId())
            .usages(imageDto.getUsages())
            .uploadDate(imageDto.getUploadDate())
            .url(imageDto.getUrl())
            .attachType(imageDto.getAttachType())
            .build();
    }
}
