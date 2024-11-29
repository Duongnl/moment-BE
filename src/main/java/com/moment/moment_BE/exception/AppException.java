package com.moment.moment_BE.exception;

// dùng để chở errorcode đến globalexception
public class AppException extends  RuntimeException{

    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        // goi contructer cua lop cha, lop cha truyen vao thong diep, appException.getMassage()
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
