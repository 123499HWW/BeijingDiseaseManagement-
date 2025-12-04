package com.hxj.common.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 普通用户需要提供旧密码
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
