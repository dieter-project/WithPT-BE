package com.sideproject.withpt.application.record.bookmark.controller.request;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkDeleteRequest {

    @NotNull(message = "북마크 삭제 시 아이디는 필수입니다.")
    @Min(1)
    private List<Long> bookmarkIds;

}
