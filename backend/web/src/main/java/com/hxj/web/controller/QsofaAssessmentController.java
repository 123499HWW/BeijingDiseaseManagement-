package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.QsofaAssessmentQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.entity.QsofaAssessment;
import com.hxj.common.vo.QsofaAssessmentPageVO;
import com.hxj.service.QsofaAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * qSOFA评分控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/qsofa")
public class QsofaAssessmentController {

    @Autowired
    private QsofaAssessmentService assessmentService;

    /**
     * 对单个患者进行qSOFA评分
     * @param patientId 患者ID
     * @return 评分结果
     */
    @PostMapping("/assess/{patientId}")
    public Result<QsofaAssessment> assessSinglePatient(@PathVariable Long patientId) {
        try {
            log.info("开始对患者{}进行qSOFA评分", patientId);
            QsofaAssessment assessment = assessmentService.assessSinglePatient(patientId);
            log.info("患者{}qSOFA评分完成", patientId);
            return Result.success(assessment);
        } catch (Exception e) {
            log.error("患者{}qSOFA评分失败", patientId, e);
            return Result.error("评分失败: " + e.getMessage());
        }
    }

    /**
     * 对所有患者进行qSOFA评分
     * @return 批量评分结果
     */
    @PostMapping("/assess/all")
    public Result<BatchAssessmentResult> assessAllPatients() {
        try {
            log.info("开始批量qSOFA评分");
            BatchAssessmentResult result = assessmentService.assessAllPatients();
            log.info("批量qSOFA评分完成");
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量qSOFA评分失败", e);
            return Result.error("批量评分失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询qSOFA评分结果
     * 支持根据总分、风险等级、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
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
            IPage<QsofaAssessmentPageVO> result = assessmentService.queryQsofaPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询qSOFA评分结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * @return 状态信息
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("qSOFA评分服务运行正常");
    }
}
