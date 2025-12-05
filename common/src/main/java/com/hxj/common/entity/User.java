package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hxj.common.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("tb_user")
public class User {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    private String username;
    
    private String password;
    
    @EnumValue
    private UserRole role;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("created_by")
    private String createdBy;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("updated_by")
    private String updatedBy;
    
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
    
    private String remark;
}
