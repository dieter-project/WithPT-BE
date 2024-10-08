package com.sideproject.withpt.application.record.bookmark.service.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkCheckResponse {

    private boolean isDuplicated;
    private final String message = "북마크 등록이 가능합니다";

    public static BookmarkCheckResponse from(boolean isDuplicated) {
        return BookmarkCheckResponse.builder()
            .isDuplicated(isDuplicated)
            .build();
    }
}
