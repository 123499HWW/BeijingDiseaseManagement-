package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.Covid19CriticalAssessment;
import com.hxj.common.vo.Covid19CriticalPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * COVID-19危重型诊断Mapper接口
 */
@Mapper
public interface Covid19CriticalAssessmentMapper extends BaseMapper<Covid19CriticalAssessment> {
    
    /**
     * 分页查询COVID-19危重型诊断结果（包含患者信息）
     * @param page 分页对象
     * @param isCriticalType 是否为危重型（可选）
     * @param criteriaCount 满足标准数（可选）
     * @param severityLevel 严重程度等级（可选）
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "c19c.*, " +
            "p.patient_number, p.gender, p.age, " +
            "ccpr.patient_id, " +
            "DATE_FORMAT(ccpr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "ccpr.relation_type as assessment_type " +
            "FROM covid19_critical_assessment c19c " +
            "LEFT JOIN covid19_critical_patient_relation ccpr ON c19c.assessment_id = ccpr.assessment_id " +
            "LEFT JOIN patient_info p ON ccpr.patient_id = p.patient_id " +
            "WHERE c19c.is_deleted = 0 " +
            "<if test='isCriticalType != null'>" +
            "  AND c19c.is_critical_type = #{isCriticalType} " +
            "</if>" +
            "<if test='criteriaCount != null'>" +
            "  AND c19c.criteria_count = #{criteriaCount} " +
            "</if>" +
            "<if test='severityLevel != null and severityLevel != &quot;&quot;'>" +
            "  AND c19c.severity_level = #{severityLevel} " +
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
            "ORDER BY c19c.created_at DESC" +
            "</script>")
    IPage<Covid19CriticalPageVO> selectCovid19CriticalPage(Page<Covid19CriticalPageVO> page,
                                                           @Param("isCriticalType") Boolean isCriticalType,
                                                           @Param("criteriaCount") Integer criteriaCount,
                                                           @Param("severityLevel") String severityLevel,
                                                           @Param("gender") String gender,
                                                           @Param("minAge") Integer minAge,
                                                           @Param("maxAge") Integer maxAge);
}
