package com.hxj.common.dto.login;

import com.hxj.common.enums.UserRole;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    
    private String token;

    private Long userId;
    
    private String username;
    
    private UserRole role;
    
    private String remark;
    
    public LoginResponse(String token, Long userId, String username, UserRole role, String remark) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.remark = remark;
    }
}
