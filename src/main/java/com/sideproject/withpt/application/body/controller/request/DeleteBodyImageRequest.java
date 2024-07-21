package com.sideproject.withpt.application.body.controller.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteBodyImageRequest {

    private List<Long> imageIds;
}
