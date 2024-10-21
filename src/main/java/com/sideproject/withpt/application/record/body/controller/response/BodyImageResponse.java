package com.sideproject.withpt.application.record.body.controller.response;

import com.sideproject.withpt.domain.record.Image;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BodyImageResponse {

    private LocalDate uploadDate;
    private List<String> url;

    @Builder
    private BodyImageResponse(LocalDate uploadDate, List<String> url) {
        this.uploadDate = uploadDate;
        this.url = url;
    }

    public static BodyImageResponse from(List<Image> image) {
        List<String> imageUrls = image.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());

        return BodyImageResponse.builder()
                .uploadDate(image.get(0).getUploadDate())
                .url(imageUrls)
                .build();
    }

}
