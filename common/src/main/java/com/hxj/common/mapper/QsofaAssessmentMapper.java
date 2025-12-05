package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.QsofaAssessment;
import com.hxj.common.vo.QsofaAssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * qSOFA评分Mapper接口
 */
@Mapper
public interface QsofaAssessmentMapper extends BaseMapper<QsofaAssessment> {
    
    /**
     * 分页查询qSOFA评分结果（包含患者信息）
     * @param page 分页对象
     * @param totalScore 总分（可选）
     * @param riskLevel 风险等级（可选）
     * @param minScore 最小总分（可选）
     * @param maxScore 最大总分（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "qsofa.*, " +
            "p.patient_number, p.gender, p.age, " +
            "qpr.patient_id, " +
            "DATE_FORMAT(qpr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "qpr.relation_type as assessment_type " +
            "FROM qsofa_assessment qsofa " +
            "LEFT JOIN qsofa_patient_relation qpr ON qsofa.assessment_id = qpr.assessment_id " +
            "LEFT JOIN patient_info p ON qpr.patient_id = p.patient_id " +
            "WHERE qsofa.is_deleted = 0 " +
            "<if test='totalScore != null'>" +
            "  AND qsofa.total_score = #{totalScore} " +
            "</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'>" +
            "  AND qsofa.risk_level = #{riskLevel} " +
            "</if>" +
            "<if test='minScore != null'>" +
            "  AND qsofa.total_score &gt;= #{minScore} " +
            "</if>" +
            "<if test='maxScore != null'>" +
            "  AND qsofa.total_score &lt;= #{maxScore} " +
            "</if>" +
            "<if test='gender != null and gender != \"\"'>" +
            "  AND p.gender = #{gender} " +
            "</if>" +
            "<if test='minAge != null'>" +
            "  AND p.age &gt;= #{minAge} " +
            "</if>" +
            "<if test='maxAge != null'>" +
            "  AND p.age &lt;= #{maxAge} " +
            "</if>" +
            "ORDER BY qsofa.created_at DESC" +
            "</script>")
    IPage<QsofaAssessmentPageVO> selectQsofaPage(Page<QsofaAssessmentPageVO> page,
                                                 @Param("totalScore") Integer totalScore,
                                                 @Param("riskLevel") String riskLevel,
                                                 @Param("minScore") Integer minScore,
                                                 @Param("maxScore") Integer maxScore,
                                                 @Param("gender") String gender,
                                                 @Param("minAge") Integer minAge,
                                                 @Param("maxAge") Integer maxAge);
}
