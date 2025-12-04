package com.hxj.common.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    private String username;
    
    @NotBlank(message = "密码不能为空")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$",
//             message = "密码必须包含大小写字母和数字，长度8-20位")
    private String password;
    
    private String remark; // 备注信息
    
    // 手动添加getter方法（如果Lombok不工作）
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getRemark() {
        return remark;
    }
    
    // 手动添加setter方法
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
