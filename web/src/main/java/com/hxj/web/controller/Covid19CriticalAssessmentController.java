package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.Covid19CriticalQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.entity.Covid19CriticalAssessment;
import com.hxj.common.vo.Covid19CriticalPageVO;
import com.hxj.service.Covid19CriticalAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * COVID-19危重型诊断控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/covid19-critical")
public class Covid19CriticalAssessmentController {

    @Autowired
    private Covid19CriticalAssessmentService assessmentService;

    /**
     * 对单个患者进行COVID-19危重型诊断
     * @param patientId 患者ID
     * @return 诊断结果
     */
    @PostMapping("/assess/{patientId}")
    public Result<Covid19CriticalAssessment> assessSinglePatient(@PathVariable Long patientId) {
        try {
            log.info("开始对患者{}进行COVID-19危重型诊断", patientId);
            Covid19CriticalAssessment assessment = assessmentService.assessSinglePatient(patientId);
            log.info("患者{}COVID-19危重型诊断完成", patientId);
            return Result.success(assessment);
        } catch (Exception e) {
            log.error("患者{}COVID-19危重型诊断失败", patientId, e);
            return Result.error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 对所有患者进行COVID-19危重型诊断
     * @return 批量诊断结果
     */
    @PostMapping("/assess/all")
    public Result<BatchAssessmentResult> assessAllPatients() {
        try {
            log.info("开始批量COVID-19危重型诊断");
            BatchAssessmentResult result = assessmentService.assessAllPatients();
            log.info("批量COVID-19危重型诊断完成");
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量COVID-19危重型诊断失败", e);
            return Result.error("批量诊断失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询COVID-19危重型诊断结果
     * 支持根据诊断结果、满足标准数、严重程度、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
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
                String[] validLevels = {"非危重型", "危重型", "危重型（高危）", "危重型（极危重）"};
                if (!Arrays.asList(validLevels).contains(queryDTO.getSeverityLevel())) {
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
            IPage<Covid19CriticalPageVO> result = assessmentService.queryCovid19CriticalPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询COVID-19危重型诊断结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * @return 状态信息
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("COVID-19危重型诊断服务运行正常");
    }
}
