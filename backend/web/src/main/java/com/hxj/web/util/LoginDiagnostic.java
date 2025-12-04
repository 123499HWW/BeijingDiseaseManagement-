package com.hxj.web.util;

import com.hxj.common.entity.User;
import com.hxj.common.mapper.UserMapper;
import com.hxj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 登录诊断工具
 * 用于排查登录密码验证问题
 */
@Component
public class LoginDiagnostic implements CommandLineRunner {
    
    @Autowired(required = false)
    private UserMapper userMapper;
    
    @Autowired(required = false)
    private UserService userService;
    
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "diagnose".equals(args[0])) {
            diagnoseLogin();
        }
    }
    
    public void diagnoseLogin() {
        System.out.println("=== 登录诊断工具 ===");
        System.out.println();
        
        try {
            // 检查依赖注入
            System.out.println("1. 检查依赖注入:");
            System.out.println("   UserMapper: " + (userMapper != null ? "✅ 已注入" : "❌ 未注入"));
            System.out.println("   UserService: " + (userService != null ? "✅ 已注入" : "❌ 未注入"));
            System.out.println("   PasswordEncoder: " + (passwordEncoder != null ? "✅ 已注入" : "❌ 未注入"));
            System.out.println();
            
            if (userService == null || passwordEncoder == null) {
                System.out.println("❌ 关键依赖未注入，无法继续诊断");
                return;
            }
            
            // 检查用户数据
            System.out.println("2. 检查用户数据:");
            try {
                User adminUser = userService.findByUsername("admin");
                User normalUser = userService.findByUsername("user");
                
                System.out.println("   admin用户: " + (adminUser != null ? "✅ 存在" : "❌ 不存在"));
                if (adminUser != null) {
                    System.out.println("     用户ID: " + adminUser.getUserId());
                    System.out.println("     角色: " + adminUser.getRole());
                    System.out.println("     密码哈希: " + adminUser.getPassword());
                    System.out.println("     是否删除: " + adminUser.getIsDeleted());
                }
                
                System.out.println("   user用户: " + (normalUser != null ? "✅ 存在" : "❌ 不存在"));
                if (normalUser != null) {
                    System.out.println("     用户ID: " + normalUser.getUserId());
                    System.out.println("     角色: " + normalUser.getRole());
                    System.out.println("     密码哈希: " + normalUser.getPassword());
                    System.out.println("     是否删除: " + normalUser.getIsDeleted());
                }
                System.out.println();
                
                // 测试密码验证
                System.out.println("3. 测试密码验证:");
                if (adminUser != null) {
                    testPasswordMatch("admin", "Admin123", adminUser.getPassword());
                    testPasswordMatch("admin", "admin", adminUser.getPassword());
                    testPasswordMatch("admin", "123456", adminUser.getPassword());
                }
                
                if (normalUser != null) {
                    testPasswordMatch("user", "User123", normalUser.getPassword());
                    testPasswordMatch("user", "user", normalUser.getPassword());
                    testPasswordMatch("user", "123456", normalUser.getPassword());
                }
                
            } catch (Exception e) {
                System.out.println("❌ 查询用户数据失败: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.out.println("❌ 诊断过程出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testPasswordMatch(String username, String password, String hash) {
        try {
            boolean matches = passwordEncoder.matches(password, hash);
            System.out.println("   " + username + " / " + password + ": " + (matches ? "✅ 匹配" : "❌ 不匹配"));
        } catch (Exception e) {
            System.out.println("   " + username + " / " + password + ": ❌ 验证出错 - " + e.getMessage());
        }
    }
    
    public static void printManualInstructions() {
        System.out.println("=== 手动排查步骤 ===");
        System.out.println();
        System.out.println("1. 检查数据库连接:");
        System.out.println("   - 确认MySQL服务已启动");
        System.out.println("   - 验证数据库连接配置 (application-dev.yml)");
        System.out.println("   - 确认数据库 respiratory_infection 存在");
        System.out.println();
        System.out.println("2. 检查用户数据:");
        System.out.println("   - 执行: SELECT * FROM tb_user;");
        System.out.println("   - 确认用户数据存在且密码哈希正确");
        System.out.println();
        System.out.println("3. 检查MyBatis配置:");
        System.out.println("   - 确认 @MapperScan 注解正确");
        System.out.println("   - 验证逻辑删除字段配置");
        System.out.println();
        System.out.println("4. 重新生成密码哈希:");
        System.out.println("   - 使用 RealPasswordGenerator 生成新的哈希");
        System.out.println("   - 更新数据库中的密码数据");
        System.out.println();
        System.out.println("5. 查看应用日志:");
        System.out.println("   - 启动应用时的错误信息");
        System.out.println("   - 登录时的详细日志");
    }
}
