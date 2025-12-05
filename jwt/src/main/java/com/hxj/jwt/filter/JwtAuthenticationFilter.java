package com.hxj.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxj.exception.dto.ErrorResponse;
import com.hxj.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // 跳过不需要认证的路径
        if (isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 检查Authorization头是否存在且以Bearer开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.warn("JWT token解析失败: " + e.getMessage());
                handleAuthenticationError(response, "Token无效，请重新登录");
                return;
            }
        } else {
            // 需要认证的接口但没有提供token
            handleAuthenticationError(response, "未登录，请先登录");
            return;
        }

        // 如果token有效且当前没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    handleAuthenticationError(response, "Token已过期，请重新登录");
                    return;
                }
            } catch (Exception e) {
                logger.warn("用户认证失败: " + e.getMessage());
                handleAuthenticationError(response, "认证失败，请重新登录");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * 判断是否为公开路径，不需要JWT认证
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/auth/") ||
               requestURI.startsWith("/api/public/") || 
               requestURI.startsWith("/api/debug/");
    }
    
    /**
     * 处理认证错误，返回JSON格式的错误信息
     */
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        ErrorResponse errorResult = new ErrorResponse("1003", message);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResult);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
