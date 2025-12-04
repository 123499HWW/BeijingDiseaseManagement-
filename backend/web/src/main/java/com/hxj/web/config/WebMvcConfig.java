package com.hxj.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     * 使用allowedOriginPatterns而不是allowedOrigins来支持通配符和credentials
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源（使用OriginPatterns支持通配符和credentials）
                .allowedOriginPatterns("*")
                // 允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                // 允许的请求头
                .allowedHeaders("*")
                // 暴露的响应头
                .exposedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", 
                               "Access-Control-Request-Method", "Access-Control-Request-Headers", 
                               "Authorization", "Content-Disposition")
                // 允许发送Cookie和认证信息
                .allowCredentials(true)
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }

    /**
     * 静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 文件上传路径映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
