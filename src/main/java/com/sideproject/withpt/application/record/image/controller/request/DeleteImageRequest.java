package com.sideproject.withpt.application.record.image.controller.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteImageRequest {

    private List<Long> imageIds;
}
