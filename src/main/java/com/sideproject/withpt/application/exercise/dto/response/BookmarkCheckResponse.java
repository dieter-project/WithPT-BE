package com.sideproject.withpt.application.exercise.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkCheckResponse {

    private boolean duplicateBookmark;

    public static BookmarkCheckResponse from(boolean isDuplicated){
        return BookmarkCheckResponse.builder()
            .duplicateBookmark(isDuplicated)
            .build();
    }
}
