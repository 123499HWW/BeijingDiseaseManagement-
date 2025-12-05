package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.CurbAssessmentResult;
import com.hxj.common.vo.CurbAssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * CURB-65评分结果Mapper接口
 */
@Mapper
public interface CurbAssessmentResultMapper extends BaseMapper<CurbAssessmentResult> {
    
    /**
     * 分页查询CURB评分结果（包含患者编号）
     * @param page 分页对象
     * @param totalScore 总分（可选）
     * @param riskLevel 风险等级（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "c.curb_id, c.age_result, c.confusion_result, c.urea_result, " +
            "c.respiration_result, c.blood_pressure_result, c.total_score, " +
            "c.risk_level, c.remark, c.created_at, c.created_by, " +
            "c.updated_at, c.updated_by, c.is_deleted, " +
            "p.patient_number, p.gender, p.age, " +
            "pcr.patient_id, " +
            "DATE_FORMAT(pcr.assessment_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "pcr.assessment_type " +
            "FROM curb_assessment_result c " +
            "LEFT JOIN patient_curb_relation pcr ON c.curb_id = pcr.curb_id " +
            "LEFT JOIN patient_info p ON pcr.patient_id = p.patient_id " +
            "WHERE c.is_deleted = 0 " +
            "<if test='totalScore != null'>" +
            "  AND c.total_score = #{totalScore} " +
            "</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'>" +
            "  AND c.risk_level = #{riskLevel} " +
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
            "ORDER BY c.created_at DESC" +
            "</script>")
    IPage<CurbAssessmentPageVO> selectCurbAssessmentPage(Page<CurbAssessmentPageVO> page, 
                                                          @Param("totalScore") Integer totalScore, 
                                                          @Param("riskLevel") String riskLevel,
                                                          @Param("gender") String gender,
                                                          @Param("minAge") Integer minAge,
                                                          @Param("maxAge") Integer maxAge);
}
