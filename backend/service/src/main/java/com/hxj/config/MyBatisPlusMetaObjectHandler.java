package com.hxj.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充创建时间、更新时间等字段
 */
@Slf4j
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "created_at", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updated_at", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充创建人（如果有用户上下文，可以从中获取）
        this.strictInsertFill(metaObject, "createdBy", String.class, getCurrentUser());
        this.strictInsertFill(metaObject, "created_by", String.class, getCurrentUser());
        
        // 自动填充更新人
        this.strictInsertFill(metaObject, "updatedBy", String.class, getCurrentUser());
        this.strictInsertFill(metaObject, "updated_by", String.class, getCurrentUser());
        
        // 自动填充删除标志
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "is_deleted", Integer.class, 0);
        
        log.debug("插入填充完成");
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updated_at", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新人
        this.strictUpdateFill(metaObject, "updatedBy", String.class, getCurrentUser());
        this.strictUpdateFill(metaObject, "updated_by", String.class, getCurrentUser());
        
        log.debug("更新填充完成");
    }

    /**
     * 获取当前用户
     * TODO: 可以从SecurityContext或其他用户上下文中获取当前用户
     */
    private String getCurrentUser() {
        // 暂时返回系统用户，后续可以集成用户认证系统
        return "system";
    }
}
