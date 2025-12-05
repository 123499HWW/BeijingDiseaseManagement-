package com.hxj.common.dto.user;

import com.hxj.common.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 */
@Data
public class UserInfoResponse {
    
    private Long userId;
    
    private String username;
    
    private UserRole role;
    
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    private LocalDateTime updatedAt;
    
    private String updatedBy;
    
    private String remark;
}
