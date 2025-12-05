package com.hxj.common.dto;

import lombok.Data;

/**
 * 基础分页查询DTO
 * 包含通用的分页参数和患者基本信息查询条件
 */
@Data
public class BasePageQueryDTO {
    
    /**
     * 性别（男/女）
     */
    private String gender;
    
    /**
     * 最小年龄
     */
    private Integer minAge;
    
    /**
     * 最大年龄
     */
    private Integer maxAge;
    
    /**
     * 当前页码（默认1）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数（默认10）
     */
    private Integer pageSize = 10;
}
