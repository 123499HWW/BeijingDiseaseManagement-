package com.hxj.web.controller;

import com.hxj.common.result.Result;
import com.hxj.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT调试控制器
 * 用于测试JWT认证是否正常工作
 */
@RestController
@RequestMapping("/api/debug")
public class JwtDebugController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 测试JWT认证状态
     */
    @GetMapping("/auth-status")
    public Result<Object> getAuthStatus(HttpServletRequest request) {
        try {
            // 获取认证信息
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // 获取token
            String authHeader = request.getHeader("Authorization");
            final String token = (authHeader != null && authHeader.startsWith("Bearer ")) 
                ? authHeader.substring(7) : null;
            
            // 构建响应数据
            Object responseData = new Object() {
                public final boolean isAuthenticated = auth != null && auth.isAuthenticated();
                public final String username = auth != null ? auth.getName() : null;
                public final String authorities = auth != null ? auth.getAuthorities().toString() : null;
                public final boolean hasToken = token != null;
                public final String tokenPreview = token != null ? token.substring(0, Math.min(20, token.length())) + "..." : null;
                public final boolean tokenValid = token != null ? jwtUtil.isValidToken(token) : false;
            };
            
            return Result.success("认证状态检查完成", responseData);
            
        } catch (Exception e) {
            return Result.error("认证状态检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成测试token
     */
    @PostMapping("/generate-token")
    public Result<String> generateTestToken(@RequestParam String username, @RequestParam Long userId) {
        try {
            String token = jwtUtil.generateToken(username, userId);
            return Result.success("Token生成成功", token);
        } catch (Exception e) {
            return Result.error("Token生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证token
     */
    @PostMapping("/validate-token")
    public Result<Object> validateToken(@RequestParam String token, @RequestParam String username) {
        try {
            final boolean isValid = jwtUtil.validateToken(token, username);
            final String tokenUsername = jwtUtil.getUsernameFromToken(token);
            final Long userId = jwtUtil.getUserIdFromToken(token);
            final boolean isExpired = jwtUtil.isTokenExpired(token);
            
            Object responseData = new Object() {
                public final boolean valid = isValid;
                public final String extractedUsername = tokenUsername;
                public final Long extractedUserId = userId;
                public final boolean expired = isExpired;
            };
            
            return Result.success("Token验证完成", responseData);
        } catch (Exception e) {
            return Result.error("Token验证失败: " + e.getMessage());
        }
    }
}
