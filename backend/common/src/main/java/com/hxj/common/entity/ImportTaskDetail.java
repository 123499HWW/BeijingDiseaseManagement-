package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入任务详情实体类
 * 记录每条导入记录的详细信息和错误原因
 */
@Data
@TableName("import_task_detail")
public class ImportTaskDetail {

    /**
     * 详情ID
     */
    @TableId(value = "detail_id", type = IdType.AUTO)
    private Long detailId;

    /**
     * 任务ID - 关联import_task表
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 行号
     */
    @TableField("row_index")
    private Integer rowNumber;

    /**
     * 处理状态
     * SUCCESS: 成功
     * FAILED: 失败
     * SKIPPED: 跳过
     */
    @TableField("process_status")
    private String processStatus;

    /**
     * 原始数据（JSON格式）
     */
    @TableField("original_data")
    private String originalData;

    /**
     * 处理后的数据ID（如果成功导入）
     */
    @TableField("processed_data_id")
    private Long processedDataId;

    /**
     * 错误代码
     */
    @TableField("error_code")
    private String errorCode;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 验证失败字段
     */
    @TableField("validation_errors")
    private String validationErrors;

    /**
     * 处理时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    // ==================== 通用字段 ====================

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 更新人
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
