package com.sideproject.withpt.application.certificate.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class CertificateException extends GlobalException {

    private final CertificateErrorCode errorCode;

    public CertificateException(CertificateErrorCode errorCode){
        super(errorCode);
        this.errorCode = errorCode;
    }
}
