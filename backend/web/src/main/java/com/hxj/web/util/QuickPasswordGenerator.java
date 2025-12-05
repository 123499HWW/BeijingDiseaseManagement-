package com.hxj.web.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 快速密码生成器
 * 用于快速生成常用测试密码的加密版本
 */
public class QuickPasswordGenerator {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public static void main(String[] args) {
        System.out.println("=== 快速密码生成器 ===");
        System.out.println("生成常用测试密码的加密版本\n");
        
        // 生成管理员密码
        generatePassword("Admin123", "管理员密码");
        
        // 生成普通用户密码  
        generatePassword("User123", "普通用户密码");
        
        // 生成测试密码
        generatePassword("Test123", "测试密码");
        
        // 生成默认重置密码
        generatePassword("abc123", "默认重置密码");
        
        // 生成其他常用密码
        generatePassword("Password123", "通用密码");
        generatePassword("Demo123", "演示密码");
        
        System.out.println("\n=== 完整用户数据SQL ===");
        generateCompleteUserSQL();
    }
    
    /**
     * 生成单个密码的加密版本
     */
    private static void generatePassword(String rawPassword, String description) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        System.out.println("📝 " + description + ":");
        System.out.println("   原始密码: " + rawPassword);
        System.out.println("   加密密码: " + encodedPassword);
        System.out.println("   验证结果: " + (passwordEncoder.matches(rawPassword, encodedPassword) ? "✅" : "❌"));
        System.out.println();
    }
    
    /**
     * 生成完整的用户数据SQL
     */
    private static void generateCompleteUserSQL() {
        String adminPassword = passwordEncoder.encode("Admin123");
        String userPassword = passwordEncoder.encode("User123");
        
        System.out.println("-- 插入测试用户数据");
        System.out.println("INSERT INTO tb_user (username, password, role, created_by, updated_by, remark) VALUES");
        System.out.println("('admin', '" + adminPassword + "', 'ADMIN', 'SYSTEM', 'SYSTEM', '系统管理员'),");
        System.out.println("('user', '" + userPassword + "', 'USER', 'SYSTEM', 'SYSTEM', '普通用户');");
        System.out.println();
        
        System.out.println("-- 验证登录信息");
        System.out.println("-- 管理员账号: admin / Admin123");
        System.out.println("-- 普通用户账号: user / User123");
    }
    
    /**
     * 验证指定密码
     */
    public static void verifySpecificPassword(String rawPassword, String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("密码验证: " + rawPassword + " -> " + (matches ? "✅ 正确" : "❌ 错误"));
    }
}
