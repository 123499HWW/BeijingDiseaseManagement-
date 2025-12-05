package com.hxj.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum Gender {
    MALE("男", "男性"),
    FEMALE("女", "女性");

    @EnumValue  // 告诉MyBatis-Plus使用这个字段的值与数据库进行映射
    private final String code;
    private final String description;
    
    /**
     * 枚举构造函数
     */
    Gender(String code, String description) {
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
     */
    @JsonCreator
    public static Gender fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        for (Gender gender : Gender.values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        
        return null;
    }
}
