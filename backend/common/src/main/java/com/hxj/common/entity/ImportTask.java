package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入任务实体类
 * 记录Excel导入任务的基本信息和状态
 */
@Data
@TableName("import_task")
public class ImportTask {

    /**
     * 任务ID
     */
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 任务编号 - 唯一标识
     */
    @TableField("task_number")
    private String taskNumber;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 导入类型
     * PATIENT_DATA: 患者数据导入
     * USER_DATA: 用户数据导入
     */
    @TableField("import_type")
    private String importType;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 任务状态
     * PENDING: 待处理
     * PROCESSING: 处理中
     * COMPLETED: 已完成
     * FAILED: 失败
     * CANCELLED: 已取消
     */
    @TableField("task_status")
    private String taskStatus;

    /**
     * 总记录数
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 成功记录数
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 失败记录数
     */
    @TableField("failure_count")
    private Integer failureCount;

    /**
     * 跳过记录数
     */
    @TableField("skip_count")
    private Integer skipCount;

    /**
     * 处理开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 处理结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 处理耗时（毫秒）
     */
    @TableField("duration")
    private Long duration;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

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

    /**
     * 计算成功率
     */
    public Double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) (successCount != null ? successCount : 0) / totalCount * 100;
    }

    /**
     * 生成任务编号
     */
    public static String generateTaskNumber() {
        return "IMPORT_" + System.currentTimeMillis();
    }
}
