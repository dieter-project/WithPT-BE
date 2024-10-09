package com.sideproject.withpt.application.record.diet.repository.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.Usages;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageDto {

    private Long id;
    private String usageIdentificationId;
    private Usages usages;
    private LocalDate uploadDate;
    private String url;
    private String attachType;

    @QueryProjection
    public ImageDto(Long id, String usageIdentificationId, Usages usages, LocalDate uploadDate, String url, String attachType) {
        this.id = id;
        this.usageIdentificationId = usageIdentificationId;
        this.usages = usages;
        this.uploadDate = uploadDate;
        this.url = url;
        this.attachType = attachType;
    }
}
