# 剩余4个表的分页查询实现代码

## 1. qSOFA评分表实现

### Mapper修改 (QsofaAssessmentMapper.java)
```java
// 在文件开头添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.QsofaAssessmentPageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 在接口中添加方法
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

### Service修改 (QsofaAssessmentService.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.QsofaAssessmentQueryDTO;
import com.hxj.common.vo.QsofaAssessmentPageVO;

// 添加方法
public IPage<QsofaAssessmentPageVO> queryQsofaPage(QsofaAssessmentQueryDTO queryDTO) {
    log.info("开始分页查询qSOFA评分结果，查询条件: {}", queryDTO);
    
    Page<QsofaAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    
    IPage<QsofaAssessmentPageVO> result = qsofaAssessmentMapper.selectQsofaPage(
        page,
        queryDTO.getTotalScore(),
        queryDTO.getRiskLevel(),
        queryDTO.getMinScore(),
        queryDTO.getMaxScore(),
        queryDTO.getGender(),
        queryDTO.getMinAge(),
        queryDTO.getMaxAge()
    );
    
    log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
            result.getTotal(), result.getRecords().size());
    
    return result;
}
```

### Controller修改 (QsofaAssessmentController.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.QsofaAssessmentQueryDTO;
import com.hxj.common.vo.QsofaAssessmentPageVO;

// 添加接口方法
@PostMapping("/page")
public Result<IPage<QsofaAssessmentPageVO>> queryQsofaPage(@RequestBody QsofaAssessmentQueryDTO queryDTO) {
    
    log.info("分页查询qSOFA评分结果，查询条件: {}", queryDTO);
    
    try {
        // 设置默认值
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }
        
        // 参数校验
        if (queryDTO.getTotalScore() != null && (queryDTO.getTotalScore() < 0 || queryDTO.getTotalScore() > 3)) {
            return Result.error("总分必须在0-3之间");
        }
        
        if (queryDTO.getMinScore() != null && (queryDTO.getMinScore() < 0 || queryDTO.getMinScore() > 3)) {
            return Result.error("最小总分必须在0-3之间");
        }
        
        if (queryDTO.getMaxScore() != null && (queryDTO.getMaxScore() < 0 || queryDTO.getMaxScore() > 3)) {
            return Result.error("最大总分必须在0-3之间");
        }
        
        if (queryDTO.getMinScore() != null && queryDTO.getMaxScore() != null &&
            queryDTO.getMinScore() > queryDTO.getMaxScore()) {
            return Result.error("最小总分不能大于最大总分");
        }
        
        if (queryDTO.getRiskLevel() != null && !queryDTO.getRiskLevel().isEmpty()) {
            if (!"低风险".equals(queryDTO.getRiskLevel()) && !"高风险".equals(queryDTO.getRiskLevel())) {
                return Result.error("风险等级必须是：低风险或高风险");
            }
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
        IPage<QsofaAssessmentPageVO> result = qsofaAssessmentService.queryQsofaPage(queryDTO);
        
        log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return Result.success(result);
        
    } catch (Exception e) {
        log.error("分页查询qSOFA评分结果失败", e);
        return Result.error("查询失败: " + e.getMessage());
    }
}
```

---

## 2. SOFA评分表实现

### Mapper修改 (SofaAssessmentMapper.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.SofaAssessmentPageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 添加方法
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

### Service修改 (SofaAssessmentService.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.SofaAssessmentQueryDTO;
import com.hxj.common.vo.SofaAssessmentPageVO;

// 添加方法
public IPage<SofaAssessmentPageVO> querySofaPage(SofaAssessmentQueryDTO queryDTO) {
    log.info("开始分页查询SOFA评分结果，查询条件: {}", queryDTO);
    
    Page<SofaAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    
    IPage<SofaAssessmentPageVO> result = sofaAssessmentMapper.selectSofaPage(
        page,
        queryDTO.getTotalScore(),
        queryDTO.getSeverityLevel(),
        queryDTO.getMinTotalScore(),
        queryDTO.getMaxTotalScore(),
        queryDTO.getGender(),
        queryDTO.getMinAge(),
        queryDTO.getMaxAge()
    );
    
    log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
            result.getTotal(), result.getRecords().size());
    
    return result;
}
```

---

## 3. COVID-19重型诊断表实现

### Mapper修改 (Covid19AssessmentMapper.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.Covid19AssessmentPageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 添加方法
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

### Service修改 (Covid19AssessmentService.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.Covid19AssessmentQueryDTO;
import com.hxj.common.vo.Covid19AssessmentPageVO;

// 添加方法
public IPage<Covid19AssessmentPageVO> queryCovid19Page(Covid19AssessmentQueryDTO queryDTO) {
    log.info("开始分页查询COVID-19重型诊断结果，查询条件: {}", queryDTO);
    
    Page<Covid19AssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    
    IPage<Covid19AssessmentPageVO> result = covid19AssessmentMapper.selectCovid19Page(
        page,
        queryDTO.getIsSevereType(),
        queryDTO.getCriteriaCount(),
        queryDTO.getSeverityLevel(),
        queryDTO.getGender(),
        queryDTO.getMinAge(),
        queryDTO.getMaxAge()
    );
    
    log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
            result.getTotal(), result.getRecords().size());
    
    return result;
}
```

---

## 4. COVID-19危重型诊断表实现

### Mapper修改 (Covid19CriticalAssessmentMapper.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.vo.Covid19CriticalPageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 添加方法
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

### Service修改 (Covid19CriticalAssessmentService.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.Covid19CriticalQueryDTO;
import com.hxj.common.vo.Covid19CriticalPageVO;

// 添加方法
public IPage<Covid19CriticalPageVO> queryCovid19CriticalPage(Covid19CriticalQueryDTO queryDTO) {
    log.info("开始分页查询COVID-19危重型诊断结果，查询条件: {}", queryDTO);
    
    Page<Covid19CriticalPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    
    IPage<Covid19CriticalPageVO> result = covid19CriticalAssessmentMapper.selectCovid19CriticalPage(
        page,
        queryDTO.getIsCriticalType(),
        queryDTO.getCriteriaCount(),
        queryDTO.getSeverityLevel(),
        queryDTO.getGender(),
        queryDTO.getMinAge(),
        queryDTO.getMaxAge()
    );
    
    log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
            result.getTotal(), result.getRecords().size());
    
    return result;
}
```

### Controller修改 (Covid19CriticalAssessmentController.java)
```java
// 添加imports
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.Covid19CriticalQueryDTO;
import com.hxj.common.vo.Covid19CriticalPageVO;

// 添加接口方法
@PostMapping("/page")
public Result<IPage<Covid19CriticalPageVO>> queryCovid19CriticalPage(@RequestBody Covid19CriticalQueryDTO queryDTO) {
    
    log.info("分页查询COVID-19危重型诊断结果，查询条件: {}", queryDTO);
    
    try {
        // 设置默认值
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }
        
        // 参数校验
        if (queryDTO.getCriteriaCount() != null && queryDTO.getCriteriaCount() < 0) {
            return Result.error("满足标准数不能小于0");
        }
        
        if (queryDTO.getSeverityLevel() != null && !queryDTO.getSeverityLevel().isEmpty()) {
            List<String> validLevels = Arrays.asList("非危重型", "危重型", "危重型（高危）", "危重型（极危重）");
            if (!validLevels.contains(queryDTO.getSeverityLevel())) {
                return Result.error("严重程度等级必须是：非危重型、危重型、危重型（高危）或危重型（极危重）");
            }
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
        IPage<Covid19CriticalPageVO> result = covid19CriticalAssessmentService.queryCovid19CriticalPage(queryDTO);
        
        log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return Result.success(result);
        
    } catch (Exception e) {
        log.error("分页查询COVID-19危重型诊断结果失败", e);
        return Result.error("查询失败: " + e.getMessage());
    }
}
```

## 实施步骤

1. **复制代码**：根据每个表的对应文件，复制上述代码
2. **粘贴到对应位置**：
   - Mapper层：在接口中添加分页查询方法
   - Service层：在类中添加分页查询方法
   - Controller层：在控制器中添加POST接口
3. **检查imports**：确保所有必要的import语句都已添加
4. **测试接口**：使用Postman或其他工具测试每个接口

## 接口路径汇总

- qSOFA: `POST /api/qsofa/page`
- SOFA: `POST /api/sofa/page`
- COVID-19重型: `POST /api/covid19/page`
- COVID-19危重型: `POST /api/covid19-critical/page`
