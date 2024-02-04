package com.sideproject.withpt.application.chat.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class ChatException extends GlobalException {

    private final ChatErrorCode errorCode;

    public ChatException(ChatErrorCode errorCode){
        super(errorCode);
        this.errorCode = errorCode;
    }

}
