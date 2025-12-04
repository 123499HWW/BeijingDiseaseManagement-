package com.hxj.web.controller;

import com.hxj.common.dto.login.LoginRequest;
import com.hxj.common.dto.login.LoginResponse;
import com.hxj.common.dto.login.RegisterRequest;
import com.hxj.common.exception.BizException;
import com.hxj.common.result.Result;
import com.hxj.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 默认由系统创建用户
            userService.register(request, "SYSTEM");
            return Result.success("注册成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
