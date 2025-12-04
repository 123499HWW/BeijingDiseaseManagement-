# 批量分页查询接口实现指南

## 已完成的表
1. ✅ CURB评分表 (curb_assessment_result)
2. ✅ PSI评分表 (psi_assessment_result)

## 待实现的表
3. CPIS评分表 (cpis_assessment_result)
4. 重症肺炎诊断表 (severe_pneumonia_diagnosis)
5. qSOFA评分表 (qsofa_assessment)
6. SOFA评分表 (sofa_assessment)
7. COVID-19重型诊断表 (covid19_assessment)
8. COVID-19危重型诊断表 (covid19_critical_assessment)

## 实现步骤

### 对于每个表，需要：

1. **创建查询DTO** (已创建)
   - 继承BasePageQueryDTO
   - 添加表特有的查询条件

2. **创建分页VO**
   - 继承对应的实体类
   - 添加patient_number、gender、age等字段

3. **修改Mapper**
   - 添加分页查询方法
   - 三表联查SQL

4. **修改Service**
   - 添加分页查询方法

5. **修改Controller**
   - 添加POST /page接口
   - 参数校验

## 通用模板

### VO模板
```java
@Data
@EqualsAndHashCode(callSuper = true)
public class XxxPageVO extends XxxEntity {
    private String patientNumber;
    private String gender;
    private Integer age;
    private Long patientId;
    private String assessmentDate;
    private String assessmentType;
}
```

### Mapper方法模板
```java
IPage<XxxPageVO> selectXxxPage(Page<XxxPageVO> page,
    @Param("param1") Type param1,
    @Param("gender") String gender,
    @Param("minAge") Integer minAge,
    @Param("maxAge") Integer maxAge);
```

### Service方法模板
```java
public IPage<XxxPageVO> queryXxxPage(XxxQueryDTO queryDTO) {
    Page<XxxPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
    return xxxMapper.selectXxxPage(page, ...params);
}
```

### Controller方法模板
```java
@PostMapping("/page")
public Result<IPage<XxxPageVO>> queryXxxPage(@RequestBody XxxQueryDTO queryDTO) {
    // 参数校验
    // 调用service
    return Result.success(result);
}
```

## 表关联关系
- 所有评估表都通过对应的relation表关联到patient_info
- 联查时需要处理is_deleted条件

## 注意事项
1. 所有接口使用POST方法
2. 分页参数名：pageNum、pageSize
3. 都需要添加性别和年龄区间筛选
4. 参数校验要完善
5. 日志记录要完整
