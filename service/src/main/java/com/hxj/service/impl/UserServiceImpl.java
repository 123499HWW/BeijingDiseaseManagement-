package com.hxj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hxj.common.dto.login.LoginRequest;
import com.hxj.common.dto.login.LoginResponse;
import com.hxj.common.dto.login.RegisterRequest;
import com.hxj.common.dto.user.ChangePasswordRequest;
import com.hxj.common.dto.user.ChangeRoleRequest;
import com.hxj.common.dto.user.UserInfoResponse;
import com.hxj.common.dto.user.UserQueryRequest;
import com.hxj.common.entity.User;
import com.hxj.common.result.PageResult;
import com.hxj.common.enums.ResponseCodeEnum;
import com.hxj.common.enums.UserRole;
import com.hxj.common.exception.BizException;
import com.hxj.common.mapper.UserMapper;
import com.hxj.jwt.util.JwtUtil;
import com.hxj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final String DEFAULT_ADMIN_PASSWORD = "abc123";
    
    @Override
    public void register(RegisterRequest request, String createdBy) {
        // 检查用户名是否已存在
        User existingUser = findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BizException(ResponseCodeEnum.USERNAME_EXIST);
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER); // 默认创建USER角色
        user.setRemark(request.getRemark());
        user.setIsDeleted(0); // 默认未删除
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(createdBy);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(createdBy);
        
        userMapper.insert(user);
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = findByUsername(request.getUsername());
        if (user == null) {
            throw new BizException(ResponseCodeEnum.PWD_NOT_VALID);
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ResponseCodeEnum.PWD_NOT_VALID);
        }
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());
        
        return new LoginResponse(token, user.getUserId(), user.getUsername(), user.getRole(), user.getRemark());
    }
    
    @Override
    public User findByUserId(Long userId) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUserId, userId);
        return userMapper.selectOne(query);
    }
    
    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        return userMapper.selectOne(query);
    }
    
    @Override
    public void changePassword(ChangePasswordRequest request, String operatorUsername) {
        // 查找操作者
        User operator = findByUsername(operatorUsername);
        if (operator == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        
        // 查找目标用户
        User targetUser = findByUsername(request.getUsername());
        if (targetUser == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        
        String newPassword;
        
        if (operator.getRole() == UserRole.ADMIN) {
            // ADMIN用户的密码修改逻辑
            if (operator.getUsername().equals(request.getUsername())) {
                // ADMIN修改自己的密码，需要验证旧密码
                if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
                    throw new BizException(ResponseCodeEnum.REQBODY_NOT_VALID.getErrorCode(), "旧密码不能为空");
                }
                
                // 验证旧密码
                if (!passwordEncoder.matches(request.getOldPassword(), targetUser.getPassword())) {
                    throw new BizException(ResponseCodeEnum.USER_PWD_ERROR);
                }
                
                // 使用新密码
                newPassword = passwordEncoder.encode(request.getNewPassword());
            } else {
                // ADMIN重置其他用户密码，直接重置为默认密码
                newPassword = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);
            }
        } else if (operator.getRole() == UserRole.USER) {
            // USER用户只能修改自己的密码，且需要验证旧密码
            if (!operator.getUsername().equals(request.getUsername())) {
                throw new BizException(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), "普通用户只能修改自己的密码");
            }
            
            if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
                throw new BizException(ResponseCodeEnum.REQBODY_NOT_VALID.getErrorCode(), "旧密码不能为空");
            }
            
            // 验证旧密码
            if (!passwordEncoder.matches(request.getOldPassword(), targetUser.getPassword())) {
                throw new BizException(ResponseCodeEnum.USER_PWD_ERROR);
            }
            
            newPassword = passwordEncoder.encode(request.getNewPassword());
        } else {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        
        // 更新密码
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, request.getUsername())
                    .set(User::getPassword, newPassword)
                    .set(User::getUpdatedAt, LocalDateTime.now())
                    .set(User::getUpdatedBy, operatorUsername);
        
        userMapper.update(null, updateWrapper);
    }
    
    @Override
    public void changeUserRole(ChangeRoleRequest request, String operatorUsername) {
        // 查找操作者
        User operator = findByUsername(operatorUsername);
        if (operator == null || operator.getRole() != UserRole.ADMIN) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), "只有管理员可以修改用户角色");
        }
        
        // 查找目标用户
        User targetUser = findByUsername(request.getUsername());
        if (targetUser == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        
        // 更新用户角色
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, request.getUsername())
                    .set(User::getRole, request.getRole())
                    .set(User::getUpdatedAt, LocalDateTime.now())
                    .set(User::getUpdatedBy, operatorUsername);
        
        userMapper.update(null, updateWrapper);
    }
    
    @Override
    public void deleteUser(Long userId, String operatorUsername) {
        // 查找操作者
        User operator = findByUsername(operatorUsername);
        if (operator == null || operator.getRole() != UserRole.ADMIN) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), "只有管理员可以删除用户");
        }
        
        // 查找目标用户
        User targetUser = findByUserId(userId);
        if (targetUser == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        
        // 不能删除自己
        if (operator.getUserId().equals(userId)) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "不能删除自己");
        }
        
        // 执行逻辑删除（MyBatis-Plus会自动处理）
        userMapper.deleteById(targetUser.getUserId());
    }
    
    @Override
    public PageResult<UserInfoResponse> queryUsers(UserQueryRequest request) {
        // 构建分页对象
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户名模糊查询
        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(User::getUsername, request.getUsername());
        }
        
        // 角色精确查询
        if (request.getRole() != null) {
            queryWrapper.eq(User::getRole, request.getRole());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreatedAt);
        
        // 执行分页查询
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        List<UserInfoResponse> responseList = userPage.getRecords().stream()
                .map(this::convertToUserInfoResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(responseList, userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
    }
    
    /**
     * 转换User实体为UserInfoResponse
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
