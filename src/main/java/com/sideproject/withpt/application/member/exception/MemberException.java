package com.sideproject.withpt.application.member.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class MemberException extends GlobalException {

    public static final MemberException DUPLICATE_NICKNAME = new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);

    private final MemberErrorCode errorCode;

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
