package com.hxj.exception.dto;

/**
 * 统一的错误响应类
 * 用于各模块的错误信息返回
 */
public class ErrorResponse {
    
    private String code;
    private String message;
    private Object data;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
    
    public ErrorResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // Getter and Setter methods
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}
