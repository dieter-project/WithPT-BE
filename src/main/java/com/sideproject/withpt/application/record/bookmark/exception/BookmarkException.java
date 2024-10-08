package com.sideproject.withpt.application.record.bookmark.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class BookmarkException extends GlobalException {


    public static final BookmarkException BOOKMARK_NOT_EXIST = new BookmarkException(BookmarkErrorCode.BOOKMARK_NOT_EXIST);
    public static final BookmarkException BOOKMARK_ALREADY_EXISTS = new BookmarkException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);

    private final BookmarkErrorCode errorCode;

    public BookmarkException(BookmarkErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
