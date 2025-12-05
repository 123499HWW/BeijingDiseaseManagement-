package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.SofaAssessment;
import com.hxj.common.vo.SofaAssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * SOFA评分Mapper接口
 */
@Mapper
public interface SofaAssessmentMapper extends BaseMapper<SofaAssessment> {
    
    /**
     * 分页查询SOFA评分结果（包含患者信息）
     * @param page 分页对象
     * @param totalScore 总分（可选）
     * @param severityLevel 严重程度（可选）
     * @param minTotalScore 最小总分（可选）
     * @param maxTotalScore 最大总分（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "sofa.*, " +
            "p.patient_number, p.gender, p.age, " +
            "spr.patient_id, " +
            "DATE_FORMAT(spr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "spr.relation_type as assessment_type " +
            "FROM sofa_assessment sofa " +
            "LEFT JOIN sofa_patient_relation spr ON sofa.assessment_id = spr.assessment_id " +
            "LEFT JOIN patient_info p ON spr.patient_id = p.patient_id " +
            "WHERE sofa.is_deleted = 0 " +
            "<if test='totalScore != null'>" +
            "  AND sofa.total_score = #{totalScore} " +
            "</if>" +
            "<if test='severityLevel != null and severityLevel != \"\"'>" +
            "  AND sofa.severity_level = #{severityLevel} " +
            "</if>" +
            "<if test='minTotalScore != null'>" +
            "  AND sofa.total_score &gt;= #{minTotalScore} " +
            "</if>" +
            "<if test='maxTotalScore != null'>" +
            "  AND sofa.total_score &lt;= #{maxTotalScore} " +
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
            "ORDER BY sofa.created_at DESC" +
            "</script>")
    IPage<SofaAssessmentPageVO> selectSofaPage(Page<SofaAssessmentPageVO> page,
                                               @Param("totalScore") Integer totalScore,
                                               @Param("severityLevel") String severityLevel,
                                               @Param("minTotalScore") Integer minTotalScore,
                                               @Param("maxTotalScore") Integer maxTotalScore,
                                               @Param("gender") String gender,
                                               @Param("minAge") Integer minAge,
                                               @Param("maxAge") Integer maxAge);
}
