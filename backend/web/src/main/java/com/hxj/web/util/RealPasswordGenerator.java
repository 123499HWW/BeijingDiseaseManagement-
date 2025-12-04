package com.hxj.web.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 真实的BCrypt密码生成器
 * 使用Spring Security的BCryptPasswordEncoder
 */
@Component
public class RealPasswordGenerator implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "generate-passwords".equals(args[0])) {
            generatePasswords();
        }
    }
    
    public void generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== 真实BCrypt密码生成 ===");
        System.out.println();
        
        // 生成Admin123的哈希
        String adminPassword = "Admin123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("管理员密码: " + adminPassword);
        System.out.println("BCrypt哈希: " + adminHash);
        System.out.println("验证结果: " + encoder.matches(adminPassword, adminHash));
        System.out.println();
        
        // 生成User123的哈希
        String userPassword = "User123";
        String userHash = encoder.encode(userPassword);
        System.out.println("用户密码: " + userPassword);
        System.out.println("BCrypt哈希: " + userHash);
        System.out.println("验证结果: " + encoder.matches(userPassword, userHash));
        System.out.println();
        
        // 生成SQL
        System.out.println("=== 更新SQL语句 ===");
        System.out.println("-- 删除现有测试数据");
        System.out.println("DELETE FROM tb_user WHERE username IN ('admin', 'user');");
        System.out.println();
        System.out.println("-- 插入正确的测试数据");
        System.out.println("INSERT INTO `tb_user` (`username`, `password`, `role`, `created_by`, `updated_by`, `remark`) VALUES");
        System.out.println("('" + "admin" + "', '" + adminHash + "', 'ADMIN', 'SYSTEM', 'SYSTEM', '系统管理员'),");
        System.out.println("('" + "user" + "', '" + userHash + "', 'USER', 'SYSTEM', 'SYSTEM', '测试用户');");
        System.out.println();
        
        // 测试现有数据库中的哈希
        System.out.println("=== 测试现有数据库哈希 ===");
        String existingHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        System.out.println("现有哈希: " + existingHash);
        System.out.println("Admin123匹配: " + encoder.matches("Admin123", existingHash));
        System.out.println("User123匹配: " + encoder.matches("User123", existingHash));
        System.out.println("abc123匹配: " + encoder.matches("abc123", existingHash));
        System.out.println("123456匹配: " + encoder.matches("123456", existingHash));
    }
    
    public static void main(String[] args) {
        RealPasswordGenerator generator = new RealPasswordGenerator();
        generator.generatePasswords();
    }
}
