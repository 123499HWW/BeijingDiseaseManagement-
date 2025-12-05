package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.CurbAssessmentQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.mapper.PhysicalExaminationDetailMapper;
import com.hxj.common.mapper.PatientExaminationRelationMapper;
import com.hxj.common.mapper.CurbAssessmentResultMapper;
import com.hxj.common.mapper.PatientCurbRelationMapper;
import com.hxj.common.vo.CurbAssessmentPageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * CURB-65评分评估服务
 * 负责计算患者的CURB-65评分
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurbAssessmentService {

    private final PatientMapper patientMapper;
    private final PhysicalExaminationDetailMapper physicalExaminationDetailMapper;
    private final PatientExaminationRelationMapper patientExaminationRelationMapper;
    private final CurbAssessmentResultMapper curbAssessmentResultMapper;
    private final PatientCurbRelationMapper patientCurbRelationMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CurbAssessmentResult performAssessment(Long patientId) {
        log.debug("开始为患者执行CURB-65评估，patientId: {}", patientId);
        
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new RuntimeException("患者不存在: " + patientId);
        }
        
        return assessSinglePatient(patient, "system");
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     * @return 评估结果统计
     */
    @Transactional(rollbackFor = Exception.class)
    public AssessmentResult performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行CURB-65评估，患者数量: {}", patientIds.size());
        
        AssessmentResult result = new AssessmentResult();
        result.setTotalCount(patientIds.size());
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                result.incrementSuccessCount();
            } catch (Exception e) {
                log.error("CURB-65评估失败，patientId: {}", patientId, e);
                result.incrementFailureCount();
                result.addErrorMessage("患者" + patientId + "评估失败: " + e.getMessage());
            }
        }
        
        log.info("CURB-65批量评估完成: {}", result);
        return result;
    }
    
    /**
     * 为所有患者执行CURB-65评分评估
     * 
     * @param createdBy 创建人
     * @return 评估结果统计
     */
    @Transactional(rollbackFor = Exception.class)
    public AssessmentResult assessAllPatients(String createdBy) {
        log.info("开始为所有患者执行CURB-65评分评估，操作人: {}", createdBy);
        
        AssessmentResult result = new AssessmentResult();
        
        try {
            // 查询所有未删除的患者
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Patient::getIsDeleted, 0);
            
            List<Patient> patients = patientMapper.selectList(queryWrapper);
            result.setTotalCount(patients.size());
            
            log.info("找到需要评估的患者数: {}", patients.size());
            
            // 逐个评估患者
            for (Patient patient : patients) {
                try {
                    assessSinglePatient(patient, createdBy);
                    result.incrementSuccessCount();
                } catch (Exception e) {
                    log.error("评估患者CURB-65失败, patientId: {}", patient.getPatientId(), e);
                    result.incrementFailureCount();
                    result.addErrorMessage(String.format("患者ID %d 评估失败: %s", 
                                                        patient.getPatientId(), e.getMessage()));
                }
            }
            
            log.info("CURB-65评分评估完成，成功: {}, 失败: {}", 
                    result.getSuccessCount(), result.getFailureCount());
            
        } catch (Exception e) {
            log.error("CURB-65评估过程中发生异常", e);
            throw new RuntimeException("CURB-65评估失败", e);
        }
        
        return result;
    }

    /**
     * 为单个患者执行CURB-65评分评估
     * 
     * @param patient 患者信息
     * @param createdBy 创建人
     */
    @Transactional(rollbackFor = Exception.class)
    public CurbAssessmentResult assessSinglePatient(Patient patient, String createdBy) {
        log.debug("开始评估患者CURB-65, patientId: {}", patient.getPatientId());
        
        // 检查是否已经评估过
        LambdaQueryWrapper<PatientCurbRelation> existsWrapper = new LambdaQueryWrapper<>();
        existsWrapper.eq(PatientCurbRelation::getPatientId, patient.getPatientId())
                    .eq(PatientCurbRelation::getIsDeleted, 0);
        
        if (patientCurbRelationMapper.selectCount(existsWrapper) > 0) {
            log.warn("患者 {} 已经评估过CURB-65，跳过", patient.getPatientId());
            return null;
        }
        
        // 创建评估结果对象
        CurbAssessmentResult assessmentResult = new CurbAssessmentResult();
        
        // 1. 评估年龄 - 年龄≥65岁
        assessmentResult.setAgeResult(assessAge(patient));
        
        // 2. 评估意识障碍 - 查询体检详细信息中的一般状况
        assessmentResult.setConfusionResult(assessConfusion(patient.getPatientId()));
        
        // 3. 评估尿素氮 - 尿素氮＞7mmol/L
        assessmentResult.setUreaResult(assessUrea(patient));
        
        // 4. 评估呼吸频率 - 呼吸频率≥30次/分
        assessmentResult.setRespirationResult(assessRespiration(patient.getPatientId()));
        
        // 5. 评估血压 - SBP＜90mmHg或DBP≤60mmHg
        assessmentResult.setBloodPressureResult(assessBloodPressure(patient.getPatientId()));
        
        // 计算总得分和风险等级
        assessmentResult.calculateTotalScore();
        
        // 设置通用字段
        LocalDateTime now = LocalDateTime.now();
        assessmentResult.setCreatedAt(now);
        assessmentResult.setCreatedBy(createdBy);
        assessmentResult.setUpdatedAt(now);
        assessmentResult.setUpdatedBy(createdBy);
        assessmentResult.setIsDeleted(0);
        
        // 保存评估结果
        curbAssessmentResultMapper.insert(assessmentResult);
        
        // 创建关联关系
        PatientCurbRelation relation = new PatientCurbRelation();
        relation.setPatientId(patient.getPatientId());
        relation.setCurbId(assessmentResult.getCurbId());
        relation.setAssessmentDate(now);
        relation.setAssessmentType("ADMISSION");
        relation.setRemark("系统自动评估生成");
        
        // 设置通用字段
        relation.setCreatedAt(now);
        relation.setCreatedBy(createdBy);
        relation.setUpdatedAt(now);
        relation.setUpdatedBy(createdBy);
        relation.setIsDeleted(0);
        
        // 保存关联关系
        patientCurbRelationMapper.insert(relation);
        
        log.debug("患者CURB-65评估完成, patientId: {}, curbId: {}, 总得分: {}", 
                 patient.getPatientId(), assessmentResult.getCurbId(), assessmentResult.getTotalScore());
        
        return assessmentResult;
    }

    /**
     * 评估年龄 - 年龄≥65岁为true
     */
    private Boolean assessAge(Patient patient) {
        if (patient.getAge() != null) {
            return patient.getAge() >= 65;
        }
        
        // 如果年龄字段为空，尝试通过住院日期估算（假设住院时的年龄）
        if (patient.getAdmissionDate() != null) {
            // 这里需要患者的出生日期来计算准确年龄
            // 由于当前Patient实体中没有出生日期字段，暂时使用age字段
            log.warn("患者 {} 年龄信息缺失，无法准确评估年龄项", patient.getPatientId());
            return false;
        }
        
        return false;
    }

    /**
     * 评估意识障碍 - 查询体检详细信息中是否含有"意识障碍"
     */
    private Boolean assessConfusion(Long patientId) {
        try {
            // 查询该患者的体检详细信息
            LambdaQueryWrapper<PatientExaminationRelation> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(PatientExaminationRelation::getPatientId, patientId)
                          .eq(PatientExaminationRelation::getIsDeleted, 0);
            
            List<PatientExaminationRelation> relations = patientExaminationRelationMapper.selectList(relationWrapper);
            
            for (PatientExaminationRelation relation : relations) {
                // 查询体检详细信息
                PhysicalExaminationDetail detail = physicalExaminationDetailMapper.selectById(relation.getDetailId());
                if (detail != null && StringUtils.hasText(detail.getGeneralCondition())) {
                    // 模糊搜索是否包含"意识障碍"相关关键词
                    String condition = detail.getGeneralCondition().toLowerCase();
                    if (condition.contains("意识障碍") || condition.contains("意识不清") || 
                        condition.contains("昏迷") || condition.contains("嗜睡") || 
                        condition.contains("意识模糊") || condition.contains("神志不清")) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("评估意识障碍失败, patientId: {}", patientId, e);
            return false;
        }
    }

    /**
     * 评估尿素氮 - 尿素氮＞7mmol/L为true
     */
    private Boolean assessUrea(Patient patient) {
        if (patient.getBloodUreaNitrogen() != null) {
            return patient.getBloodUreaNitrogen().compareTo(new BigDecimal("7")) > 0;
        }
        return false;
    }

    /**
     * 评估呼吸频率 - 呼吸频率≥30次/分为true
     */
    private Boolean assessRespiration(Long patientId) {
        try {
            // 查询该患者的体检详细信息
            LambdaQueryWrapper<PatientExaminationRelation> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(PatientExaminationRelation::getPatientId, patientId)
                          .eq(PatientExaminationRelation::getIsDeleted, 0);
            
            List<PatientExaminationRelation> relations = patientExaminationRelationMapper.selectList(relationWrapper);
            
            for (PatientExaminationRelation relation : relations) {
                PhysicalExaminationDetail detail = physicalExaminationDetailMapper.selectById(relation.getDetailId());
                if (detail != null && detail.getRespiration() != null) {
                    if (detail.getRespiration() >= 30) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("评估呼吸频率失败, patientId: {}", patientId, e);
            return false;
        }
    }

    /**
     * 评估血压 - SBP＜90mmHg或DBP≤60mmHg为true
     */
    private Boolean assessBloodPressure(Long patientId) {
        try {
            // 查询该患者的体检详细信息
            LambdaQueryWrapper<PatientExaminationRelation> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.eq(PatientExaminationRelation::getPatientId, patientId)
                          .eq(PatientExaminationRelation::getIsDeleted, 0);
            
            List<PatientExaminationRelation> relations = patientExaminationRelationMapper.selectList(relationWrapper);
            
            for (PatientExaminationRelation relation : relations) {
                PhysicalExaminationDetail detail = physicalExaminationDetailMapper.selectById(relation.getDetailId());
                if (detail != null) {
                    // 检查收缩压 < 90 或 舒张压 <= 60
                    boolean lowSystolic = detail.getSystolicBp() != null && detail.getSystolicBp() < 90;
                    boolean lowDiastolic = detail.getDiastolicBp() != null && detail.getDiastolicBp() <= 60;
                    
                    if (lowSystolic || lowDiastolic) {
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("评估血压失败, patientId: {}", patientId, e);
            return false;
        }
    }

    /**
     * 分页查询CURB评分结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<CurbAssessmentPageVO> queryCurbAssessmentPage(CurbAssessmentQueryDTO queryDTO) {
        log.info("开始分页查询CURB评分结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<CurbAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<CurbAssessmentPageVO> result = curbAssessmentResultMapper.selectCurbAssessmentPage(
            page, 
            queryDTO.getTotalScore(), 
            queryDTO.getRiskLevel(),
            queryDTO.getGender(),
            queryDTO.getMinAge(),
            queryDTO.getMaxAge()
        );
        
        log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return result;
    }

    /**
     * 评估结果统计类
     */
    public static class AssessmentResult {
        private int totalCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private StringBuilder errorMessages = new StringBuilder();
        
        public void incrementSuccessCount() {
            this.successCount++;
        }
        
        public void incrementFailureCount() {
            this.failureCount++;
        }
        
        public void addErrorMessage(String message) {
            if (!errorMessages.isEmpty()) {
                errorMessages.append("\n");
            }
            errorMessages.append(message);
        }
        
        // Getters and Setters
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public String getErrorMessages() { return errorMessages.toString(); }
        
        @Override
        public String toString() {
            return String.format("CURB-65评估结果: 总数=%d, 成功=%d, 失败=%d", 
                               totalCount, successCount, failureCount);
        }
    }
}
