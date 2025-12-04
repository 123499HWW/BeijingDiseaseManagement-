package com.hxj.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.SeverePneumoniaQueryDTO;
import com.hxj.common.result.Result;
import com.hxj.common.entity.SeverePneumoniaDiagnosis;
import com.hxj.common.vo.SeverePneumoniaPageVO;
import com.hxj.service.SeverePneumoniaDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 重症肺炎诊断控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/severe-pneumonia")
public class SeverePneumoniaDiagnosisController {

    @Autowired
    private SeverePneumoniaDiagnosisService diagnosisService;

    /**
     * 对单个患者进行重症肺炎诊断
     * @param patientId 患者ID
     * @return 诊断结果
     */
    @PostMapping("/diagnose/{patientId}")
    public Result<SeverePneumoniaDiagnosis> diagnoseSinglePatient(@PathVariable Long patientId) {
        try {
            log.info("开始对患者{}进行重症肺炎诊断", patientId);
            SeverePneumoniaDiagnosis diagnosis = diagnosisService.diagnoseSinglePatient(patientId);
            log.info("患者{}重症肺炎诊断完成", patientId);
            return Result.success(diagnosis);
        } catch (Exception e) {
            log.error("患者{}重症肺炎诊断失败", patientId, e);
            return Result.error("诊断失败: " + e.getMessage());
        }
    }

    /**
     * 对所有患者进行重症肺炎诊断
     * @return 批量诊断结果
     */
    @PostMapping("/diagnose/all")
    public Result<BatchAssessmentResult> diagnoseAllPatients() {
        try {
            log.info("开始批量重症肺炎诊断");
            BatchAssessmentResult result = diagnosisService.diagnoseAllPatients();
            log.info("批量重症肺炎诊断完成");
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量重症肺炎诊断失败", e);
            return Result.error("批量诊断失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询重症肺炎诊断结果
     * 支持根据诊断结果、标准满足数、性别、年龄范围进行筛选
     * 
     * @param queryDTO 查询条件对象
     * @return 分页查询结果
     */
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
            IPage<SeverePneumoniaPageVO> result = diagnosisService.querySeverePneumoniaPage(queryDTO);
            
            log.info("分页查询成功，总记录数: {}, 当前页记录数: {}", 
                    result.getTotal(), result.getRecords().size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询重症肺炎诊断结果失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * @return 状态信息
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("重症肺炎诊断服务运行正常");
    }
}
