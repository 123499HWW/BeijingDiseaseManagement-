package com.hxj.service;

import com.hxj.common.dto.login.LoginRequest;
import com.hxj.common.dto.login.LoginResponse;
import com.hxj.common.dto.login.RegisterRequest;
import com.hxj.common.dto.user.ChangePasswordRequest;
import com.hxj.common.dto.user.ChangeRoleRequest;
import com.hxj.common.dto.user.UserInfoResponse;
import com.hxj.common.dto.user.UserQueryRequest;
import com.hxj.common.entity.User;
import com.hxj.common.result.PageResult;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册 - 默认创建USER角色用户
     */
    void register(RegisterRequest request, String createdBy);
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户ID查找用户
     */
    User findByUserId(Long userId);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 修改密码
     * - USER用户需要提供旧密码，只能修改自己的密码
     * - ADMIN用户修改自己的密码时需要提供旧密码
     * - ADMIN用户重置其他用户密码时，将密码重置为默认密码'abc123'
     */
    void changePassword(ChangePasswordRequest request, String operatorUsername);
    
    /**
     * 修改用户角色 - 只有ADMIN用户可以操作
     */
    void changeUserRole(ChangeRoleRequest request, String operatorUsername);
    
    /**
     * 删除用户（逻辑删除） - 只有ADMIN用户可以操作
     */
    void deleteUser(Long userId, String operatorUsername);
    
    /**
     * 分页查询用户列表 - 支持用户名和角色条件查询
     */
    PageResult<UserInfoResponse> queryUsers(UserQueryRequest request);
}
