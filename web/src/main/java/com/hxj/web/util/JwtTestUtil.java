package com.hxj.web.util;

import com.hxj.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * JWT测试工具
 * 用于生成测试token和验证JWT配置
 */
@Component
public class JwtTestUtil implements CommandLineRunner {
    
    @Autowired(required = false)
    private JwtUtil jwtUtil;
    
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "jwt-test".equals(args[0])) {
            testJwt();
        }
    }
    
    public void testJwt() {
        System.out.println("=== JWT配置测试 ===");
        System.out.println();
        
        if (jwtUtil == null) {
            System.out.println("❌ JwtUtil未注入，请检查JWT模块配置");
            return;
        }
        
        System.out.println("✅ JwtUtil已注入");
        
        // 生成测试token
        try {
            String testToken = jwtUtil.generateToken("admin", 1L);
            System.out.println("✅ Token生成成功");
            System.out.println("Token: " + testToken);
            System.out.println();
            
            // 验证token
            String username = jwtUtil.getUsernameFromToken(testToken);
            Long userId = jwtUtil.getUserIdFromToken(testToken);
            boolean isValid = jwtUtil.validateToken(testToken, "admin");
            
            System.out.println("Token解析结果:");
            System.out.println("  用户名: " + username);
            System.out.println("  用户ID: " + userId);
            System.out.println("  是否有效: " + isValid);
            System.out.println();
            
            // 提供测试用的curl命令
            System.out.println("=== 测试命令 ===");
            System.out.println("1. 登录获取token:");
            System.out.println("curl -X POST http://localhost:1129/api/auth/login \\");
            System.out.println("  -H \"Content-Type: application/json\" \\");
            System.out.println("  -d '{\"username\":\"admin\",\"password\":\"Admin123\"}'");
            System.out.println();
            
            System.out.println("2. 使用token调用受保护接口:");
            System.out.println("curl -X POST http://localhost:1129/api/user/change-password \\");
            System.out.println("  -H \"Content-Type: application/json\" \\");
            System.out.println("  -H \"Authorization: Bearer " + testToken + "\" \\");
            System.out.println("  -d '{\"username\":\"admin\",\"newPassword\":\"NewPassword123\"}'");
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("❌ JWT测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== JWT手动测试说明 ===");
        System.out.println();
        System.out.println("403错误的可能原因:");
        System.out.println("1. 请求头缺少Authorization");
        System.out.println("2. Token格式错误（应为: Bearer <token>）");
        System.out.println("3. Token已过期或无效");
        System.out.println("4. JWT配置未正确加载");
        System.out.println("5. UserDetailsService配置问题");
        System.out.println();
        
        System.out.println("调试步骤:");
        System.out.println("1. 先调用登录接口获取token");
        System.out.println("2. 在请求头中添加: Authorization: Bearer <token>");
        System.out.println("3. 检查应用日志中的JWT相关错误");
        System.out.println("4. 确认数据库中用户数据正确");
    }
}
