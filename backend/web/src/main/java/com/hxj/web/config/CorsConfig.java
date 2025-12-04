package com.hxj.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS跨域配置
 * 提供CorsConfigurationSource Bean供Spring Security使用
 */
@Configuration
public class CorsConfig {


    /**
     * CORS配置源 - 提供给Spring Security使用
     * 该Bean会被Spring Security自动检测并应用
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（使用OriginPattern支持通配符）
        configuration.addAllowedOriginPattern("*");
        
        // 允许的方法
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("PATCH");
        
        // 允许的请求头
        configuration.addAllowedHeader("*");
        
        // 允许发送认证信息
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
