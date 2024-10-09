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
    private String message;

    public static BookmarkCheckResponse from(boolean isDuplicated, String message) {
        return BookmarkCheckResponse.builder()
            .isDuplicated(isDuplicated)
            .message(message)
            .build();
    }
}
