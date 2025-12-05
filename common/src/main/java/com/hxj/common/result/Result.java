package com.hxj.common.result;

import com.hxj.common.enums.ResponseCodeEnum;
import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {
    
    private boolean success;
    private String code;
    private String message;
    private T data;
    
    private Result() {
        this.success = true;
    }
    
    private Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Result<T> success() {
        return new Result<>(true, "200", "操作成功", null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "200", "操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, "200", message, data);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(false, "500", message, null);
    }
    
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(false, code, message, null);
    }
    
    public static <T> Result<T> error(ResponseCodeEnum responseCodeEnum) {
        return new Result<>(false, responseCodeEnum.getErrorCode(), responseCodeEnum.getErrorMessage(), null);
    }
}
