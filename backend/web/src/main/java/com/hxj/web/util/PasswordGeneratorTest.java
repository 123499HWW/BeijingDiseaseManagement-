package com.hxj.web.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

/**
 * 密码生成测试工具类
 * 用于测试密码加密功能，模拟用户输入密码并输出加密后的版本
 */
public class PasswordGeneratorTest {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== 密码加密测试工具 ===");
        System.out.println("输入 'exit' 退出程序");
        System.out.println();
        
        while (true) {
            System.out.print("请输入要加密的密码: ");
            String password = scanner.nextLine();
            
            // 检查退出条件
            if ("exit".equalsIgnoreCase(password.trim())) {
                System.out.println("程序退出，再见！");
                break;
            }
            
            // 检查密码是否为空
            if (password.trim().isEmpty()) {
                System.out.println("❌ 密码不能为空，请重新输入！");
                System.out.println();
                continue;
            }
            
            try {
                // 生成加密密码
                String encodedPassword = passwordEncoder.encode(password);
                
                // 输出结果
                System.out.println();
                System.out.println("📝 原始密码: " + password);
                System.out.println("🔐 加密后密码: " + encodedPassword);
                System.out.println("📏 加密长度: " + encodedPassword.length());
                
                // 验证密码是否匹配
                boolean matches = passwordEncoder.matches(password, encodedPassword);
                System.out.println("✅ 密码验证: " + (matches ? "通过" : "失败"));
                
                // 生成SQL插入语句示例
                System.out.println();
                System.out.println("💾 SQL插入语句示例:");
                System.out.println("INSERT INTO tb_user (username, password, role, created_by, updated_by, remark) VALUES");
                System.out.println("('testuser', '" + encodedPassword + "', 'USER', 'SYSTEM', 'SYSTEM', '测试用户');");
                
                System.out.println();
                System.out.println("=" .repeat(80));
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("❌ 密码加密失败: " + e.getMessage());
                System.out.println();
            }
        }
        
        scanner.close();
    }
    
    /**
     * 批量生成测试密码
     */
    public static void generateTestPasswords() {
        String[] testPasswords = {
            "Admin123",
            "User123", 
            "Test123",
            "Password123",
            "Demo123"
        };
        
        System.out.println("=== 批量密码加密测试 ===");
        System.out.println();
        
        for (String password : testPasswords) {
            String encoded = passwordEncoder.encode(password);
            System.out.println("原始密码: " + password);
            System.out.println("加密密码: " + encoded);
            System.out.println("验证结果: " + passwordEncoder.matches(password, encoded));
            System.out.println("-".repeat(60));
        }
    }
    
    /**
     * 验证现有加密密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     */
    public static void verifyPassword(String rawPassword, String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        
        System.out.println("=== 密码验证测试 ===");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("验证结果: " + (matches ? "✅ 匹配" : "❌ 不匹配"));
        System.out.println();
    }
    
    /**
     * 生成用户注册SQL语句
     * @param username 用户名
     * @param password 原始密码
     * @param role 用户角色
     * @param remark 备注
     */
    public static void generateUserInsertSQL(String username, String password, String role, String remark) {
        String encodedPassword = passwordEncoder.encode(password);
        
        System.out.println("=== 用户注册SQL生成 ===");
        System.out.println("用户名: " + username);
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println();
        System.out.println("SQL语句:");
        System.out.println("INSERT INTO tb_user (username, password, role, created_by, updated_by, remark) VALUES");
        System.out.printf("('%s', '%s', '%s', 'SYSTEM', 'SYSTEM', '%s');%n", 
            username, encodedPassword, role, remark);
        System.out.println();
    }
}
