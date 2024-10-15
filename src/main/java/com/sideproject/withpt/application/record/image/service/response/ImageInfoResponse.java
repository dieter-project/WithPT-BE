package com.sideproject.withpt.application.record.image.service.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.Usages;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageInfoResponse {

    private Long id;
    private Usages usages;
    private LocalDate uploadDate;
    private String url;
    private String attachType;

    @QueryProjection
    public ImageInfoResponse(Long id, Usages usages, LocalDate uploadDate, String url, String attachType) {
        this.id = id;
        this.usages = usages;
        this.uploadDate = uploadDate;
        this.url = url;
        this.attachType = attachType;
    }
}
