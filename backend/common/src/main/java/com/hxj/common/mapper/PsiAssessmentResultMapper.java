package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.PsiAssessmentResult;
import com.hxj.common.vo.PsiAssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * PSI评分结果Mapper接口
 */
@Mapper
public interface PsiAssessmentResultMapper extends BaseMapper<PsiAssessmentResult> {

    /**
     * 根据PSI评分ID查询评分结果
     */
    PsiAssessmentResult selectByPsiId(@Param("psiId") Long psiId);

    /**
     * 根据PSI评分ID列表查询评分结果
     */
    List<PsiAssessmentResult> selectByPsiIds(@Param("psiIds") List<Long> psiIds);

    /**
     * 根据风险等级统计患者数量
     */
    List<PsiAssessmentResult> selectCountByRiskClass(@Param("createdBy") String createdBy);

    /**
     * 批量插入PSI评分结果
     */
    int batchInsert(@Param("list") List<PsiAssessmentResult> list);
    
    /**
     * 分页查询PSI评分结果（包含患者信息）
     * @param page 分页对象
     * @param totalScore 总分（可选）
     * @param riskClass 风险等级（可选）
     * @param minTotalScore 最小总分（可选）
     * @param maxTotalScore 最大总分（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "psi.*, " +
            "p.patient_number, p.gender, p.age, " +
            "ppr.patient_id, " +
            "DATE_FORMAT(ppr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "ppr.relation_type as assessment_type " +
            "FROM psi_assessment_result psi " +
            "LEFT JOIN psi_patient_relation ppr ON psi.psi_id = ppr.psi_id " +
            "LEFT JOIN patient_info p ON ppr.patient_id = p.patient_id " +
            "WHERE psi.is_deleted = 0 " +
            "<if test='totalScore != null'>" +
            "  AND psi.total_score = #{totalScore} " +
            "</if>" +
            "<if test='riskClass != null and riskClass != \"\"'>" +
            "  AND psi.risk_class = #{riskClass} " +
            "</if>" +
            "<if test='minTotalScore != null'>" +
            "  AND psi.total_score &gt;= #{minTotalScore} " +
            "</if>" +
            "<if test='maxTotalScore != null'>" +
            "  AND psi.total_score &lt;= #{maxTotalScore} " +
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
            "ORDER BY psi.created_at DESC" +
            "</script>")
    IPage<PsiAssessmentPageVO> selectPsiAssessmentPage(Page<PsiAssessmentPageVO> page,
                                                        @Param("totalScore") Integer totalScore,
                                                        @Param("riskClass") String riskClass,
                                                        @Param("minTotalScore") Integer minTotalScore,
                                                        @Param("maxTotalScore") Integer maxTotalScore,
                                                        @Param("gender") String gender,
                                                        @Param("minAge") Integer minAge,
                                                        @Param("maxAge") Integer maxAge);
}
