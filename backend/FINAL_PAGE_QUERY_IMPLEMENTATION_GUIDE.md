# 分页查询接口最终实现指南

## 实现状态

### ✅ 已完成的文件创建
1. **基础架构**
   - BasePageQueryDTO（通用基类）
   - 各表的QueryDTO（7个）

2. **PageVO文件**（全部已创建）
   - CurbAssessmentPageVO ✅
   - PsiAssessmentPageVO ✅
   - CpisAssessmentPageVO ✅
   - SeverePneumoniaPageVO ✅
   - QsofaAssessmentPageVO ✅
   - SofaAssessmentPageVO ✅
   - Covid19AssessmentPageVO ✅
   - Covid19CriticalPageVO ✅

### ✅ 已完成接口实现
1. CURB评分表（完整实现）
2. PSI评分表（完整实现）
3. CPIS评分表（完整实现）

### ⏳ 待完成的修改（剩余5个表）

## 剩余表的具体实现代码

### 1. 重症肺炎诊断表 (severe_pneumonia_diagnosis)

#### Mapper修改
在 `SeverePneumoniaDiagnosisMapper.java` 添加：
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.SeverePneumoniaPageVO;
import org.apache.ibatis.annotations.Select;

// 添加方法
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

#### Service修改
在 `SeverePneumoniaDiagnosisService.java` 添加：
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.SeverePneumoniaQueryDTO;
import com.hxj.common.vo.SeverePneumoniaPageVO;

// 添加方法
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

### 2. qSOFA评分表 (qsofa_assessment)

#### Mapper修改
在 `QsofaAssessmentMapper.java` 添加：
```java
@Select("<script>" +
        "SELECT " +
        "qsofa.*, " +
        "p.patient_number, p.gender, p.age, " +
        "qpr.patient_id, " +
        "DATE_FORMAT(qpr.assessment_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
        "qpr.assessment_type " +
        "FROM qsofa_assessment qsofa " +
        "LEFT JOIN qsofa_patient_relation qpr ON qsofa.qsofa_id = qpr.qsofa_id " +
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
```

### 3. SOFA评分表 (sofa_assessment)

#### Mapper修改
在 `SofaAssessmentMapper.java` 添加：
```java
@Select("<script>" +
        "SELECT " +
        "sofa.*, " +
        "p.patient_number, p.gender, p.age, " +
        "spr.patient_id, " +
        "DATE_FORMAT(spr.assessment_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
        "spr.assessment_type " +
        "FROM sofa_assessment sofa " +
        "LEFT JOIN sofa_patient_relation spr ON sofa.sofa_id = spr.sofa_id " +
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
```

### 4. COVID-19重型诊断表 (covid19_assessment)

#### Mapper修改
在 `Covid19AssessmentMapper.java` 添加：
```java
@Select("<script>" +
        "SELECT " +
        "c19.*, " +
        "p.patient_number, p.gender, p.age, " +
        "cpr.patient_id, " +
        "DATE_FORMAT(cpr.assessment_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
        "cpr.assessment_type " +
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
        "<if test='severityLevel != null and severityLevel != \"\"'>" +
        "  AND c19.severity_level = #{severityLevel} " +
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
        "ORDER BY c19.created_at DESC" +
        "</script>")
IPage<Covid19AssessmentPageVO> selectCovid19Page(Page<Covid19AssessmentPageVO> page,
                                                 @Param("isSevereType") Boolean isSevereType,
                                                 @Param("criteriaCount") Integer criteriaCount,
                                                 @Param("severityLevel") String severityLevel,
                                                 @Param("gender") String gender,
                                                 @Param("minAge") Integer minAge,
                                                 @Param("maxAge") Integer maxAge);
```

### 5. COVID-19危重型诊断表 (covid19_critical_assessment)

#### Mapper修改
在 `Covid19CriticalAssessmentMapper.java` 添加：
```java
@Select("<script>" +
        "SELECT " +
        "c19c.*, " +
        "p.patient_number, p.gender, p.age, " +
        "ccpr.patient_id, " +
        "DATE_FORMAT(ccpr.assessment_date, '%Y-%m-%d %H:%i:%s') as assessment_date, " +
        "ccpr.assessment_type " +
        "FROM covid19_critical_assessment c19c " +
        "LEFT JOIN covid19_critical_patient_relation ccpr ON c19c.critical_id = ccpr.critical_id " +
        "LEFT JOIN patient_info p ON ccpr.patient_id = p.patient_id " +
        "WHERE c19c.is_deleted = 0 " +
        "<if test='isCriticalType != null'>" +
        "  AND c19c.is_critical_type = #{isCriticalType} " +
        "</if>" +
        "<if test='criteriaCount != null'>" +
        "  AND c19c.criteria_count = #{criteriaCount} " +
        "</if>" +
        "<if test='severityLevel != null and severityLevel != \"\"'>" +
        "  AND c19c.severity_level = #{severityLevel} " +
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
        "ORDER BY c19c.created_at DESC" +
        "</script>")
IPage<Covid19CriticalPageVO> selectCovid19CriticalPage(Page<Covid19CriticalPageVO> page,
                                                       @Param("isCriticalType") Boolean isCriticalType,
                                                       @Param("criteriaCount") Integer criteriaCount,
                                                       @Param("severityLevel") String severityLevel,
                                                       @Param("gender") String gender,
                                                       @Param("minAge") Integer minAge,
                                                       @Param("maxAge") Integer maxAge);
```

## 通用Controller方法模板

每个Controller都添加以下模式的分页查询方法：

```java
@PostMapping("/page")
public Result<IPage<XxxPageVO>> queryXxxPage(@RequestBody XxxQueryDTO queryDTO) {
    
    log.info("分页查询Xxx结果，查询条件: {}", queryDTO);
    
    try {
        // 设置默认值
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }
        
        // [这里添加特定的参数校验]
        
        // 通用参数校验
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
        IPage<XxxPageVO> result = xxxService.queryXxxPage(queryDTO);
        
        log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return Result.success(result);
        
    } catch (Exception e) {
        log.error("分页查询Xxx结果失败", e);
        return Result.error("查询失败: " + e.getMessage());
    }
}
```

## 注意事项

1. **表名和字段对应**：
   - severe_pneumonia_diagnosis → diagnosis_id
   - qsofa_assessment → qsofa_id
   - sofa_assessment → sofa_id
   - covid19_assessment → assessment_id
   - covid19_critical_assessment → critical_id

2. **关联表命名规则**：
   - xxx_patient_relation 是关联表名称
   - 日期字段可能是 assessment_date 或 diagnosis_date

3. **所有接口统一规范**：
   - 使用POST方法
   - 路径：/api/{module}/page
   - 请求体：JSON格式
   - 返回：IPage包装的分页数据
