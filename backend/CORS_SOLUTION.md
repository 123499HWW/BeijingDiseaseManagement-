# CORS跨域问题解决方案

## 问题描述
前端调用 `/api/patient/list` 接口时报跨域错误。

## 问题原因
Spring Security 6.x 版本中需要在 SecurityFilterChain 中明确启用 CORS 支持，仅在 WebMvcConfig 中配置 CORS 是不够的。

## 解决方案

### 1. 启用 CorsConfig 配置类
文件：`web/src/main/java/com/hxj/web/config/CorsConfig.java`
- 取消 `@Configuration` 注解的注释，启用配置类
- 提供 `CorsConfigurationSource` Bean 供 Spring Security 使用

### 2. 在 Spring Security 中启用 CORS
文件：`jwt/src/main/java/com/hxj/jwt/config/JwtConfig.java`
- 在 `SecurityFilterChain` 配置中添加 `.cors()` 启用 CORS 支持
- Spring Security 会自动检测并使用 `CorsConfigurationSource` Bean

### 3. WebMvcConfig 配置保留
文件：`web/src/main/java/com/hxj/web/config/WebMvcConfig.java`
- 保留原有的 CORS 配置作为额外的支持

## 配置详情

### CorsConfig.java 配置
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // 允许所有源
        configuration.addAllowedMethod("*");         // 允许所有方法
        configuration.addAllowedHeader("*");         // 允许所有请求头
        configuration.setAllowCredentials(true);     // 允许发送认证信息
        configuration.setMaxAge(3600L);              // 预检请求缓存时间
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### JwtConfig.java 配置
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()  // 启用CORS
        .csrf(csrf -> csrf.disable())
        // ... 其他配置
}
```

## 测试验证

1. **重启应用**：修改配置后需要重启 Spring Boot 应用
2. **前端测试**：前端调用 `/api/patient/list` 接口应该不再报跨域错误
3. **浏览器检查**：在浏览器开发者工具的 Network 面板查看响应头，应包含：
   - `Access-Control-Allow-Origin`
   - `Access-Control-Allow-Methods`
   - `Access-Control-Allow-Headers`
   - `Access-Control-Allow-Credentials`

## 注意事项

1. **生产环境配置**：在生产环境中，不建议使用 `*` 允许所有源，应该配置具体的前端域名
2. **认证信息**：如果需要发送 Cookie 或认证信息，`allowCredentials` 必须设置为 `true`
3. **预检请求**：复杂请求会先发送 OPTIONS 预检请求，确保服务器正确处理
4. **Spring Security 优先级**：Spring Security 的 CORS 配置优先级高于 Spring MVC 的配置

## 常见问题

### Q: 为什么只配置 WebMvcConfig 不够？
A: Spring Security 有自己的过滤器链，在 Spring MVC 之前执行。如果 Spring Security 没有正确配置 CORS，请求会在到达 MVC 层之前被拒绝。

### Q: 如何针对特定接口配置不同的 CORS 策略？
A: 可以在 `CorsConfigurationSource` 中为不同的路径模式注册不同的 `CorsConfiguration`。

### Q: 如何调试 CORS 问题？
A: 
- 查看浏览器控制台的错误信息
- 检查网络请求的请求头和响应头
- 开启 Spring Security 的调试日志：`logging.level.org.springframework.security=DEBUG`
