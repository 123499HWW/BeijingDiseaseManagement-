package com.hxj.web.controller;

import com.hxj.common.dto.user.ChangePasswordRequest;
import com.hxj.common.dto.user.ChangeRoleRequest;
import com.hxj.common.dto.user.UserInfoResponse;
import com.hxj.common.dto.user.UserQueryRequest;
import com.hxj.common.exception.BizException;
import com.hxj.common.result.PageResult;
import com.hxj.common.result.Result;
import com.hxj.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 修改密码
     * - USER用户需要提供旧密码，只能修改自己的密码
     * - ADMIN用户修改自己的密码时需要提供旧密码
     * - ADMIN用户重置其他用户密码时，将密码重置为默认密码'abc123'
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            userService.changePassword(request, operatorUsername);
            return Result.success("密码修改成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改用户角色 - 只有ADMIN用户可以操作
     */
    @PostMapping("/change-role")
    public Result<Void> changeUserRole(@Valid @RequestBody ChangeRoleRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            userService.changeUserRole(request, operatorUsername);
            return Result.success("用户角色修改成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户（逻辑删除） - 只有ADMIN用户可以操作
     */
    @DeleteMapping("/delete/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            userService.deleteUser(userId, operatorUsername);
            return Result.success("用户删除成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户列表 - 支持用户名和角色条件查询
     */
    @PostMapping("/list")
    public Result<PageResult<UserInfoResponse>> queryUsers(@RequestBody UserQueryRequest request) {
        try {
            PageResult<UserInfoResponse> result = userService.queryUsers(request);
            return Result.success(result);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
