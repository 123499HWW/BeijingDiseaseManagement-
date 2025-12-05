package com.hxj.common.dto.user;

import com.hxj.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户角色请求DTO
 */
@Data
public class ChangeRoleRequest {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotNull(message = "角色不能为空")
    private UserRole role;
}
