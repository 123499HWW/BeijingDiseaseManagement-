package com.hxj.common.vo;

import com.hxj.common.entity.ImportTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导入任务分页查询返回VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportTaskPageVO extends ImportTask {
    
    /**
     * 导入进度（百分比）
     */
    private Integer progressPercent;
    
    /**
     * 任务耗时（秒）
     */
    private Long durationSeconds;
    
    /**
     * 任务状态描述
     */
    private String taskStatusDesc;
    
    /**
     * 导入类型描述
     */
    private String importTypeDesc;
}
