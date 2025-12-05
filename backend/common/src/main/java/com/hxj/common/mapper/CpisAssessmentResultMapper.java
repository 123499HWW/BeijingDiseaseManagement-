package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.CpisAssessmentResult;
import com.hxj.common.vo.CpisAssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * CPIS评分结果Mapper接口
 */
@Mapper
public interface CpisAssessmentResultMapper extends BaseMapper<CpisAssessmentResult> {

    /**
     * 根据CPIS ID查询评分结果
     */
    @Select("SELECT * FROM cpis_assessment_result WHERE cpis_id = #{cpisId} AND is_deleted = 0")
    CpisAssessmentResult selectByCpisId(@Param("cpisId") Long cpisId);
    
    /**
     * 分页查询CPIS评分结果（包含患者信息）
     */
    @Select("<script>" +
            "SELECT " +
            "cpis.*, " +
            "p.patient_number, p.gender, p.age, " +
            "cpr.patient_id, " +
            "DATE_FORMAT(cpr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "cpr.relation_type as assessment_type " +
            "FROM cpis_assessment_result cpis " +
            "LEFT JOIN cpis_patient_relation cpr ON cpis.cpis_id = cpr.cpis_id " +
            "LEFT JOIN patient_info p ON cpr.patient_id = p.patient_id " +
            "WHERE cpis.is_deleted = 0 " +
            "<if test='totalScore != null'>" +
            "  AND cpis.total_score = #{totalScore} " +
            "</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'>" +
            "  AND cpis.risk_level = #{riskLevel} " +
            "</if>" +
            "<if test='minTotalScore != null'>" +
            "  AND cpis.total_score &gt;= #{minTotalScore} " +
            "</if>" +
            "<if test='maxTotalScore != null'>" +
            "  AND cpis.total_score &lt;= #{maxTotalScore} " +
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
            "ORDER BY cpis.created_at DESC" +
            "</script>")
    IPage<CpisAssessmentPageVO> selectCpisAssessmentPage(Page<CpisAssessmentPageVO> page,
                                                          @Param("totalScore") Integer totalScore,
                                                          @Param("riskLevel") String riskLevel,
                                                          @Param("minTotalScore") Integer minTotalScore,
                                                          @Param("maxTotalScore") Integer maxTotalScore,
                                                          @Param("gender") String gender,
                                                          @Param("minAge") Integer minAge,
                                                          @Param("maxAge") Integer maxAge);
}
