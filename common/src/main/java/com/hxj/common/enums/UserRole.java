package com.hxj.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    USER("USER", "普通用户");

    private final String code;
    private final String description;
    
    /**
     * 枚举构造函数
     */
    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * JSON序列化时使用code值
     */
    @JsonValue
    public String getCode() {
        return code;
    }
    
    /**
     * JSON反序列化时的自定义处理
     * 支持空字符串或null值，返回null而不是抛出异常
     */
    @JsonCreator
    public static UserRole fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        for (UserRole role : UserRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        
        // 如果找不到匹配的枚举值，返回null而不是抛出异常
        return null;
    }
}
