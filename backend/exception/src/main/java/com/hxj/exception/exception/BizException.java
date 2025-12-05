package com.hxj.exception.exception;

/**
 * 业务异常类
 * 避免依赖common模块
 */
public class BizException extends RuntimeException {
    
    private String errorCode;
    private String errorMessage;
    
    public BizException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public BizException(String errorMessage) {
        super(errorMessage);
        this.errorCode = "500";
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
