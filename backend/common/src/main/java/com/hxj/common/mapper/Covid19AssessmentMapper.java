package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.Covid19Assessment;
import com.hxj.common.vo.Covid19AssessmentPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * COVID-19诊断Mapper接口
 */
@Mapper
public interface Covid19AssessmentMapper extends BaseMapper<Covid19Assessment> {
    
    /**
     * 分页查询COVID-19重型诊断结果（包含患者信息）
     * @param page 分页对象
     * @param isSevereType 是否为重型（可选）
     * @param criteriaCount 满足标准数（可选）
     * @param severityLevel 严重程度等级（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "c19.*, " +
            "p.patient_number, p.gender, p.age, " +
            "cpr.patient_id, " +
            "DATE_FORMAT(cpr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "cpr.relation_type as assessment_type " +
            "FROM covid19_assessment c19 " +
            "LEFT JOIN covid19_patient_relation cpr ON c19.assessment_id = cpr.assessment_id " +
            "LEFT JOIN patient_info p ON cpr.patient_id = p.patient_id " +
            "WHERE c19.is_deleted = 0 " +
            "<if test='isSevereType != null'>" +
            "  AND c19.is_severe_type = #{isSevereType} " +
            "</if>" +
            "<if test='criteriaCount != null'>" +
            "  AND c19.criteria_count = #{criteriaCount} " +
            "</if>" +
            "<if test='severityLevel != null and severityLevel != &quot;&quot;'>" +
            "  AND c19.severity_level = #{severityLevel} " +
            "</if>" +
            "<if test='gender != null and gender != &quot;&quot;'>" +
            "  AND p.gender = #{gender} " +
            "</if>" +
            "<if test='minAge != null'>" +
            "  AND p.age &gt;= #{minAge} " +
            "</if>" +
            "<if test='maxAge != null'>" +
            "  AND p.age &lt;= #{maxAge} " +
            "</if>" +
            "ORDER BY c19.created_at DESC" +
            "</script>")
    IPage<Covid19AssessmentPageVO> selectCovid19Page(Page<Covid19AssessmentPageVO> page,
                                                     @Param("isSevereType") Boolean isSevereType,
                                                     @Param("criteriaCount") Integer criteriaCount,
                                                     @Param("severityLevel") String severityLevel,
                                                     @Param("gender") String gender,
                                                     @Param("minAge") Integer minAge,
                                                     @Param("maxAge") Integer maxAge);
}
