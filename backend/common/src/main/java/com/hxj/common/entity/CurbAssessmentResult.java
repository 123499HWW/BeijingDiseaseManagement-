package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CURB-65评分结果实体类
 * 用于存储社区获得性肺炎CURB-65评分的评估结果
 */
@Data
@TableName("curb_assessment_result")
public class CurbAssessmentResult {

    /**
     * CURB评分结果ID
     */
    @TableId(value = "curb_id", type = IdType.AUTO)
    private Long curbId;

    /**
     * 年龄结果 - 年龄≥65岁为true
     */
    @TableField("age_result")
    private Boolean ageResult;

    /**
     * 意识障碍结果 - 存在意识障碍为true
     */
    @TableField("confusion_result")
    private Boolean confusionResult;

    /**
     * 尿素氮结果 - 尿素氮＞7mmol/L为true
     */
    @TableField("urea_result")
    private Boolean ureaResult;

    /**
     * 呼吸频率结果 - 呼吸频率≥30次/分为true
     */
    @TableField("respiration_result")
    private Boolean respirationResult;

    /**
     * 血压结果 - SBP＜90mmHg或DBP≤60mmHg为true
     */
    @TableField("blood_pressure_result")
    private Boolean bloodPressureResult;

    /**
     * 总得分 - 为true的项目数量总和
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 风险等级
     * 0-1分：低风险
     * 2分：中风险
     * 3-5分：高风险
     */
    @TableField("risk_level")
    private String riskLevel;

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
     * 计算总得分
     */
    public void calculateTotalScore() {
        int score = 0;
        if (Boolean.TRUE.equals(ageResult)) score++;
        if (Boolean.TRUE.equals(confusionResult)) score++;
        if (Boolean.TRUE.equals(ureaResult)) score++;
        if (Boolean.TRUE.equals(respirationResult)) score++;
        if (Boolean.TRUE.equals(bloodPressureResult)) score++;
        
        this.totalScore = score;
        this.riskLevel = determineRiskLevel(score);
    }

    /**
     * 根据得分确定风险等级
     */
    private String determineRiskLevel(int score) {
        if (score <= 1) {
            return "低风险";
        } else if (score == 2) {
            return "中风险";
        } else {
            return "高风险";
        }
    }
}
