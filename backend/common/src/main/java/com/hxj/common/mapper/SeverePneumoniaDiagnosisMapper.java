package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.entity.SeverePneumoniaDiagnosis;
import com.hxj.common.vo.SeverePneumoniaPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 重症肺炎诊断Mapper接口
 */
@Mapper
public interface SeverePneumoniaDiagnosisMapper extends BaseMapper<SeverePneumoniaDiagnosis> {
    
    /**
     * 分页查询重症肺炎诊断结果（包含患者信息）
     * @param page 分页对象
     * @param isSeverePneumonia 是否重症肺炎
     * @param majorCriteriaCount 主要标准满足数
     * @param minorCriteriaCount 次要标准满足数
     * @param gender 性别（可选）
     * @param minAge 最小年龄（可选）
     * @param maxAge 最大年龄（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "spd.*, " +
            "p.patient_number, p.gender, p.age, " +
            "spr.patient_id, " +
            "DATE_FORMAT(spr.created_at, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
            "spr.relation_type as assessment_type " +
            "FROM severe_pneumonia_diagnosis spd " +
            "LEFT JOIN severe_pneumonia_patient_relation spr ON spd.diagnosis_id = spr.diagnosis_id " +
            "LEFT JOIN patient_info p ON spr.patient_id = p.patient_id " +
            "WHERE spd.is_deleted = 0 " +
            "<if test='isSeverePneumonia != null'>" +
            "  AND spd.is_severe_pneumonia = #{isSeverePneumonia} " +
            "</if>" +
            "<if test='majorCriteriaCount != null'>" +
            "  AND spd.major_criteria_count = #{majorCriteriaCount} " +
            "</if>" +
            "<if test='minorCriteriaCount != null'>" +
            "  AND spd.minor_criteria_count = #{minorCriteriaCount} " +
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
            "ORDER BY spd.created_at DESC" +
            "</script>")
    IPage<SeverePneumoniaPageVO> selectSeverePneumoniaPage(Page<SeverePneumoniaPageVO> page,
                                                            @Param("isSeverePneumonia") Boolean isSeverePneumonia,
                                                            @Param("majorCriteriaCount") Integer majorCriteriaCount,
                                                            @Param("minorCriteriaCount") Integer minorCriteriaCount,
                                                            @Param("gender") String gender,
                                                            @Param("minAge") Integer minAge,
                                                            @Param("maxAge") Integer maxAge);
}
