package com.hxj.web.security;

import com.hxj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 自定义用户详情服务实现
 * 放在Web模块中避免循环依赖
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 从数据库查找用户
            com.hxj.common.entity.User user = userService.findByUsername(username);
            if (user != null && user.getIsDeleted() == 0) {
                return User.builder()
                        .username(username)
                        .password(user.getPassword())
                        .authorities(new ArrayList<>())
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("用户查询失败: " + username, e);
        }
        
        throw new UsernameNotFoundException("用户不存在或已被删除: " + username);
    }
}
