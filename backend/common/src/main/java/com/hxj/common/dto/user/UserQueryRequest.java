package com.hxj.common.dto.user;

import com.hxj.common.enums.UserRole;
import lombok.Data;

/**
 * 用户查询请求DTO
 */
@Data
public class UserQueryRequest {

    // 用户名模糊查询
    private String username;

    // 角色精确查询
    private UserRole role;

    // 页码，默认第1页
    private Integer pageNum = 1;

    // 每页大小，默认10条
    private Integer pageSize = 10;
}
