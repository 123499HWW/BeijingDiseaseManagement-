package com.hxj.web.controller;

import com.hxj.jwt.util.JwtUtil;
import com.hxj.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 公开接口测试
     */
    @GetMapping("/public")
    public Result<String> publicTest() {
        return Result.success("公开接口访问成功");
    }
    
    /**
     * 需要认证的接口测试
     */
    @GetMapping("/protected")
    public Result<Map<String, Object>> protectedTest(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? 
                      authHeader.substring(7) : null;
        
        Map<String, Object> data = new HashMap<>();
        data.put("message", "认证接口访问成功");
        data.put("username", username);
        
        if (token != null) {
            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                data.put("userId", userId);
            } catch (Exception e) {
                data.put("userId", "无法获取用户ID");
            }
        }
        
        return Result.success(data);
    }
}
