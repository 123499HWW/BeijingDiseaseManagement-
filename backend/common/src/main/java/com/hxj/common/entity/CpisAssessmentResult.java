package com.hxj.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CPIS评分结果实体类
 */
@Data
@TableName("cpis_assessment_result")
public class CpisAssessmentResult {

    /**
     * CPIS评分ID
     */
    @TableId(value = "cpis_id", type = IdType.AUTO)
    private Long cpisId;

    // ==================== 评分指标 ====================

    /**
     * 体温评分(0-2分)
     */
    @TableField("temperature_score")
    private Integer temperatureScore;

    /**
     * 血白细胞评分(0-2分)
     */
    @TableField("wbc_score")
    private Integer wbcScore;

    /**
     * 分泌物评分(0-2分)
     */
    @TableField("secretion_score")
    private Integer secretionScore;

    /**
     * 氧合指数评分(0-2分)
     */
    @TableField("oxygenation_index_score")
    private Integer oxygenationIndexScore;

    /**
     * 胸片浸润影评分(0-2分)
     */
    @TableField("chest_xray_score")
    private Integer chestXrayScore;

    /**
     * 气管吸取物或痰培养评分(0-2分)
     */
    @TableField("culture_score")
    private Integer cultureScore;

    // ==================== 实际数值记录 ====================

    /**
     * 体温(℃)
     */
    @TableField("temperature")
    private BigDecimal temperature;

    /**
     * 白细胞计数(×10^9/L)
     */
    @TableField("wbc_count")
    private BigDecimal wbcCount;

    /**
     * 分泌物类型
     */
    @TableField("secretion_type")
    private String secretionType;

    /**
     * 氧合指数(mmHg)
     */
    @TableField("oxygenation_index")
    private BigDecimal oxygenationIndex;

    /**
     * 胸片发现
     */
    @TableField("chest_xray_finding")
    private String chestXrayFinding;

    /**
     * 培养结果
     */
    @TableField("culture_result")
    private String cultureResult;

    // ==================== 总分和风险评估 ====================

    /**
     * CPIS总分(0-12分)
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 风险等级(低风险/高风险)
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 评估结论
     */
    @TableField("assessment_conclusion")
    private String assessmentConclusion;

    /**
     * 建议措施
     */
    @TableField("recommended_action")
    private String recommendedAction;

    // ==================== 评估信息 ====================

    /**
     * 评估时间
     */
    @TableField("assessment_time")
    private LocalDateTime assessmentTime;

    /**
     * 评估方式
     */
    @TableField("assessment_method")
    private String assessmentMethod;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

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
     * 备注
     */
    @TableField("remark")
    private String remark;
}
