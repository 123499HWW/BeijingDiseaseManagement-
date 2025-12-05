package com.hxj.common.enums;

import com.hxj.common.exception.BaseExceptionInterface;

public enum ResponseCodeEnum implements BaseExceptionInterface {
    SYSTEM_ERROR("1000","后台报错，请查看日志"),
    USERNAME_ERROR("1001", "用户名错误"),
    USER_PWD_ERROR("1001","用户密码错误" ),
    LOGIN_FAIL("1002", "登录失败，请重试"),
    UNAUTHORIZED("1003", "无访问权限，请先登录"),
    USER_NOT_EXIST("1004", "该用户不存在或已被删除"),
    PWD_NOT_VALID("1005", "用户用户名或密码错误"),
    USERNAME_EXIST("1006", "该用户已存在" ),
    USER_DEL_FAILED("1007","用户删除失败"),
    USER_BAN_FAILED("1008","用户禁用失败"),
    USER_ENABLE_FAILED("1009","用户启用失败"),
    REQBODY_NOT_VALID("1010","请求体格式错误或缺失"),
    PARAM_ERROR("1011", "患者ID列表不能为空");

    private final String errorCode;
    private final String errorMessage;
    
    /**
     * 枚举构造函数
     */
    ResponseCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 实现BaseExceptionInterface接口方法
     */
    @Override
    public String getErrorCode() {
        return this.errorCode;
    }
    
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
