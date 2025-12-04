# 剩余分页查询接口实现代码

## 已完成
1. ✅ CURB评分表
2. ✅ PSI评分表  
3. ✅ CPIS评分表

## 待实现（4个表）
4. 重症肺炎诊断表 (severe_pneumonia_diagnosis)
5. qSOFA评分表 (qsofa_assessment)
6. SOFA评分表 (sofa_assessment)
7. COVID-19重型诊断表 (covid19_assessment)
8. COVID-19危重型诊断表 (covid19_critical_assessment)

---

## 4. 重症肺炎诊断表

### 4.1 创建PageVO
```java
// SeverePneumoniaPageVO.java
package com.hxj.common.vo;

import com.hxj.common.entity.SeverePneumoniaDiagnosis;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeverePneumoniaPageVO extends SeverePneumoniaDiagnosis {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

### 4.2 修改Mapper
```java
// 在SeverePneumoniaDiagnosisMapper中添加：
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.SeverePneumoniaPageVO;

@Select("<script>" +
        "SELECT " +
        "spd.*, " +
        "p.patient_number, p.gender, p.age, " +
        "spr.patient_id, " +
        "DATE_FORMAT(spr.diagnosis_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
        "spr.diagnosis_type as assessment_type " +
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
```

### 4.3 Service添加方法
```java
// 在SeverePneumoniaDiagnosisService中添加：
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.SeverePneumoniaQueryDTO;
import com.hxj.common.vo.SeverePneumoniaPageVO;

public IPage<SeverePneumoniaPageVO> querySeverePneumoniaPage(SeverePneumoniaQueryDTO queryDTO) {
    log.info("开始分页查询重症肺炎诊断结果，查询条件: {}", queryDTO);
    
    Page<SeverePneumoniaPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    
    IPage<SeverePneumoniaPageVO> result = severePneumoniaDiagnosisMapper.selectSeverePneumoniaPage(
        page,
        queryDTO.getIsSeverePneumonia(),
        queryDTO.getMajorCriteriaCount(),
        queryDTO.getMinorCriteriaCount(),
        queryDTO.getGender(),
        queryDTO.getMinAge(),
        queryDTO.getMaxAge()
    );
    
    log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
            result.getTotal(), result.getRecords().size());
    
    return result;
}
```

### 4.4 Controller添加接口
```java
// 在SeverePneumoniaDiagnosisController中添加：
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.SeverePneumoniaQueryDTO;
import com.hxj.common.vo.SeverePneumoniaPageVO;

@PostMapping("/page")
public Result<IPage<SeverePneumoniaPageVO>> querySeverePneumoniaPage(@RequestBody SeverePneumoniaQueryDTO queryDTO) {
    
    log.info("分页查询重症肺炎诊断结果，查询条件: {}", queryDTO);
    
    try {
        // 设置默认值
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }
        
        // 参数校验
        if (queryDTO.getMajorCriteriaCount() != null && queryDTO.getMajorCriteriaCount() < 0) {
            return Result.error("主要标准满足数不能小于0");
        }
        
        if (queryDTO.getMinorCriteriaCount() != null && queryDTO.getMinorCriteriaCount() < 0) {
            return Result.error("次要标准满足数不能小于0");
        }
        
        if (queryDTO.getGender() != null && !queryDTO.getGender().isEmpty()) {
            if (!"男".equals(queryDTO.getGender()) && !"女".equals(queryDTO.getGender())) {
                return Result.error("性别必须是：男或女");
            }
        }
        
        if (queryDTO.getMinAge() != null && queryDTO.getMinAge() < 0) {
            return Result.error("最小年龄不能小于0");
        }
        
        if (queryDTO.getMaxAge() != null && queryDTO.getMaxAge() < 0) {
            return Result.error("最大年龄不能小于0");
        }
        
        if (queryDTO.getMinAge() != null && queryDTO.getMaxAge() != null &&
            queryDTO.getMinAge() > queryDTO.getMaxAge()) {
            return Result.error("最小年龄不能大于最大年龄");
        }
        
        // 调用Service层执行查询
        IPage<SeverePneumoniaPageVO> result = severePneumoniaDiagnosisService.querySeverePneumoniaPage(queryDTO);
        
        log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return Result.success(result);
        
    } catch (Exception e) {
        log.error("分页查询重症肺炎诊断结果失败", e);
        return Result.error("查询失败: " + e.getMessage());
    }
}
```

---

## 5. qSOFA评分表

### 5.1 创建PageVO
```java
// QsofaAssessmentPageVO.java
package com.hxj.common.vo;

import com.hxj.common.entity.QsofaAssessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QsofaAssessmentPageVO extends QsofaAssessment {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

### 5.2 Mapper添加方法
```java
// 在QsofaAssessmentMapper中添加：
IPage<QsofaAssessmentPageVO> selectQsofaPage(Page<QsofaAssessmentPageVO> page,
                                              @Param("totalScore") Integer totalScore,
                                              @Param("riskLevel") String riskLevel,
                                              @Param("minScore") Integer minScore,
                                              @Param("maxScore") Integer maxScore,
                                              @Param("gender") String gender,
                                              @Param("minAge") Integer minAge,
                                              @Param("maxAge") Integer maxAge);
```

---

## 6. SOFA评分表

### 6.1 创建PageVO
```java
// SofaAssessmentPageVO.java
package com.hxj.common.vo;

import com.hxj.common.entity.SofaAssessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SofaAssessmentPageVO extends SofaAssessment {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

---

## 7. COVID-19重型诊断表

### 7.1 创建PageVO
```java
// Covid19AssessmentPageVO.java
package com.hxj.common.vo;

import com.hxj.common.entity.Covid19Assessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Covid19AssessmentPageVO extends Covid19Assessment {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

---

## 8. COVID-19危重型诊断表

### 8.1 创建PageVO
```java
// Covid19CriticalPageVO.java
package com.hxj.common.vo;

import com.hxj.common.entity.Covid19CriticalAssessment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Covid19CriticalPageVO extends Covid19CriticalAssessment {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

## 实现注意事项

1. 所有接口都使用POST方法
2. 请求体使用JSON格式
3. 分页参数统一使用pageNum和pageSize
4. 所有表都添加性别和年龄区间筛选
5. 参数校验要完善
6. 日志记录要完整
7. 三表联查关系要正确
