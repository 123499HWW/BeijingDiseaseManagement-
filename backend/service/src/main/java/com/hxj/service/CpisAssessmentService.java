package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.CpisAssessmentQueryDTO;
import com.hxj.common.entity.CpisAssessmentResult;
import com.hxj.common.entity.CpisPatientRelation;
import com.hxj.common.entity.Patient;
import com.hxj.common.entity.PhysicalExaminationDetail;
import com.hxj.common.mapper.CpisAssessmentResultMapper;
import com.hxj.common.mapper.CpisPatientRelationMapper;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.mapper.PhysicalExaminationDetailMapper;
import com.hxj.common.vo.CpisAssessmentPageVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CPIS评分服务
 * Clinical Pulmonary Infection Score (临床肺部感染评分)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CpisAssessmentService {

    private final CpisAssessmentResultMapper cpisAssessmentResultMapper;
    private final CpisPatientRelationMapper cpisPatientRelationMapper;
    private final PatientMapper patientMapper;
    private final PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public CpisAssessmentResult performAssessment(Long patientId) {
        return assessSinglePatient(patientId, "system");
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行CPIS评估，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("CPIS评估失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("CPIS批量评估完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 为单个患者执行CPIS评分
     */
    @Transactional
    public CpisAssessmentResult assessSinglePatient(Long patientId, String createdBy) {
        log.info("开始为患者{}执行CPIS评分", patientId);

        // 获取患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new RuntimeException("患者不存在: " + patientId);
        }

        // 检查是否已经有评分结果
        List<Long> existingCpisIds = cpisPatientRelationMapper.selectCpisIdsByPatientId(patientId);
        if (!existingCpisIds.isEmpty()) {
            log.info("患者{}已有CPIS评分结果，返回最新结果", patientId);
            return cpisAssessmentResultMapper.selectByCpisId(existingCpisIds.get(0));
        }

        // 执行CPIS评分计算
        CpisAssessmentResult result = calculateCpisScore(patient, createdBy);

        // 保存评分结果
        cpisAssessmentResultMapper.insert(result);

        // 创建患者关联关系
        CpisPatientRelation relation = new CpisPatientRelation();
        relation.setCpisId(result.getCpisId());
        relation.setPatientId(patientId);
        relation.setPatientNumber(patient.getPatientNumber());
        relation.setRelationType("ASSESSMENT");
        relation.setRelationStatus("ACTIVE");
        relation.setCreatedBy(createdBy);
        relation.setUpdatedBy(createdBy);
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        relation.setIsDeleted(0);
        cpisPatientRelationMapper.insert(relation);

        log.info("患者{}的CPIS评分完成，CPIS ID: {}, 总分: {}, 风险等级: {}", 
                patientId, result.getCpisId(), result.getTotalScore(), result.getRiskLevel());

        return result;
    }

    /**
     * 为所有患者执行CPIS评分
     */
    @Transactional
    public AssessmentResult assessAllPatients(String createdBy) {
        log.info("开始为所有患者执行CPIS评分，操作人: {}", createdBy);

        AssessmentResult assessmentResult = new AssessmentResult();
        List<CpisAssessmentResult> results = new ArrayList<>();
        List<CpisPatientRelation> relations = new ArrayList<>();

        // 查询所有患者
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getIsDeleted, 0);
        List<Patient> patients = patientMapper.selectList(queryWrapper);

        assessmentResult.setTotalCount(patients.size());

        for (Patient patient : patients) {
            try {
                // 检查是否已经有评分结果
                List<Long> existingCpisIds = cpisPatientRelationMapper.selectCpisIdsByPatientId(patient.getPatientId());
                if (!existingCpisIds.isEmpty()) {
                    log.debug("患者{}已有CPIS评分结果，跳过", patient.getPatientId());
                    assessmentResult.setSkipCount(assessmentResult.getSkipCount() + 1);
                    continue;
                }

                // 执行CPIS评分
                CpisAssessmentResult result = calculateCpisScore(patient, createdBy);
                
                // 先插入CPIS评分结果以获取自动生成的ID
                cpisAssessmentResultMapper.insert(result);
                results.add(result);

                // 创建关联关系
                CpisPatientRelation relation = new CpisPatientRelation();
                relation.setCpisId(result.getCpisId());
                relation.setPatientId(patient.getPatientId());
                relation.setPatientNumber(patient.getPatientNumber());
                relation.setRelationType("ASSESSMENT");
                relation.setRelationStatus("ACTIVE");
                relation.setCreatedBy(createdBy);
                relation.setUpdatedBy(createdBy);
                relation.setCreatedAt(LocalDateTime.now());
                relation.setUpdatedAt(LocalDateTime.now());
                relation.setIsDeleted(0);
                relations.add(relation);

                assessmentResult.setSuccessCount(assessmentResult.getSuccessCount() + 1);

            } catch (Exception e) {
                log.error("患者{}的CPIS评分失败: {}", patient.getPatientId(), e.getMessage(), e);
                assessmentResult.setFailureCount(assessmentResult.getFailureCount() + 1);
            }
        }

        // 批量保存关联关系
        if (!relations.isEmpty()) {
            cpisPatientRelationMapper.batchInsert(relations);
        }

        log.info("CPIS评分完成，总数: {}, 成功: {}, 失败: {}, 跳过: {}",
                assessmentResult.getTotalCount(), assessmentResult.getSuccessCount(),
                assessmentResult.getFailureCount(), assessmentResult.getSkipCount());

        return assessmentResult;
    }

    /**
     * 计算CPIS评分
     * CPIS评分标准：
     * - 体温：36.5-38.4℃(0分)，38.5-38.9℃(1分)，≥39℃或≤36℃(2分)
     * - 白细胞计数：固定0分（数据不可用）
     * - 分泌物：无"咳痰"(0分)，有"咳痰"(1分)
     * - 氧合指数：>240或无ARDS(0分)，≤240且无ARDS(2分)
     * - 胸片：固定0分（数据不可用）
     * - 气管吸取物/痰培养：固定0分（数据不可用）
     * 总分范围0-5分，≤6分为低风险，>6分为高风险
     */
    private CpisAssessmentResult calculateCpisScore(Patient patient, String createdBy) {
        CpisAssessmentResult result = new CpisAssessmentResult();
        
        // 基本信息
        result.setAssessmentTime(LocalDateTime.now());
        result.setAssessmentMethod("自动评分");
        result.setDataSource("患者基本信息和体检详情");
        result.setCreatedBy(createdBy);
        result.setUpdatedBy(createdBy);
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());
        result.setIsDeleted(0);

        int totalScore = 0;

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        PhysicalExaminationDetail latestExam = null;
        if (!examinations.isEmpty()) {
            latestExam = examinations.get(0);
        }

        // 1. 体温评分 (0-2分)
        int temperatureScore = 0;
        if (latestExam != null && latestExam.getTemperature() != null) {
            BigDecimal temp = latestExam.getTemperature();
            result.setTemperature(temp);
            
            if (temp.compareTo(new BigDecimal("36.5")) >= 0 && temp.compareTo(new BigDecimal("38.4")) <= 0) {
                temperatureScore = 0;
                log.debug("患者{}体温{}℃，正常范围，评分: 0", patient.getPatientId(), temp);
            } else if (temp.compareTo(new BigDecimal("38.5")) >= 0 && temp.compareTo(new BigDecimal("38.9")) <= 0) {
                temperatureScore = 1;
                log.debug("患者{}体温{}℃，轻度发热，评分: 1", patient.getPatientId(), temp);
            } else if (temp.compareTo(new BigDecimal("39")) >= 0 || temp.compareTo(new BigDecimal("36")) <= 0) {
                temperatureScore = 2;
                log.debug("患者{}体温{}℃，高热或低体温，评分: 2", patient.getPatientId(), temp);
            }
        }
        result.setTemperatureScore(temperatureScore);
        totalScore += temperatureScore;

        // 2. 白细胞计数评分 (0-2分)
        // 白细胞计数在表中无法查询，直接设为0分
        int wbcScore = 0;
        result.setWbcScore(wbcScore);
        log.debug("患者{}白细胞计数数据不可用，评分: 0", patient.getPatientId());
        totalScore += wbcScore;

        // 3. 分泌物评分 (0-1分)
        // 只查询chief_complaint字段是否有"咳痰"字样
        int secretionScore = 0;
        String secretionInfo = "无";
        
        if (patient.getChiefComplaint() != null) {
            String complaint = patient.getChiefComplaint();
            if (complaint.contains("咳痰")) {
                secretionScore = 1;
                secretionInfo = "有咳痰";
                log.debug("患者{}主诉包含'咳痰'，评分: 1", patient.getPatientId());
            } else {
                log.debug("患者{}主诉无'咳痰'，评分: 0", patient.getPatientId());
            }
        }
        
        result.setSecretionScore(secretionScore);
        result.setSecretionType(secretionInfo);
        totalScore += secretionScore;

        // 4. 氧合指数评分 (0-2分)
        int oxygenationScore = 0;
        if (patient.getArterialOxygenationIndex() != null) {
            BigDecimal oxyIndex = patient.getArterialOxygenationIndex();
            result.setOxygenationIndex(oxyIndex);
            
            if (oxyIndex.compareTo(new BigDecimal("240")) > 0) {
                oxygenationScore = 0;
                log.debug("患者{}氧合指数{}mmHg > 240，评分: 0", patient.getPatientId(), oxyIndex);
            } else {
                oxygenationScore = 2;
                log.debug("患者{}氧合指数{}mmHg ≤ 240，评分: 2", patient.getPatientId(), oxyIndex);
            }
        }
        result.setOxygenationIndexScore(oxygenationScore);
        totalScore += oxygenationScore;

        // 5. 胸片浸润影评分 (0-2分)
        // 胸片浸润影数据暂不可用，直接设为0分
        int chestXrayScore = 0;
        result.setChestXrayScore(chestXrayScore);
        result.setChestXrayFinding("数据不可用");
        log.debug("患者{}胸片浸润影数据不可用，评分: 0", patient.getPatientId());
        totalScore += chestXrayScore;

        // 6. 气管吸取物/痰培养评分 (0-2分)
        // 气管吸取物/痰培养数据暂不可用，直接设为0分
        int cultureScore = 0;
        result.setCultureScore(cultureScore);
        result.setCultureResult("数据不可用");
        log.debug("患者{}气管吸取物/痰培养数据不可用，评分: 0", patient.getPatientId());
        totalScore += cultureScore;

        // 设置总分
        result.setTotalScore(totalScore);

        // 根据总分确定风险等级
        if (totalScore <= 6) {
            result.setRiskLevel("低风险");
            result.setAssessmentConclusion("CPIS评分≤6分，提示肺部感染可能性较低");
            result.setRecommendedAction("继续观察，考虑其他诊断，避免不必要的抗生素使用");
        } else {
            result.setRiskLevel("高风险");
            result.setAssessmentConclusion("CPIS评分>6分，提示肺部感染可能性较高");
            result.setRecommendedAction("建议进行进一步的微生物学检查，考虑经验性抗感染治疗");
        }

        log.info("患者{}的CPIS评分计算完成，总分: {}，风险等级: {}", 
                patient.getPatientId(), totalScore, result.getRiskLevel());

        return result;
    }

    /**
     * 分页查询CPIS评分结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<CpisAssessmentPageVO> queryCpisAssessmentPage(CpisAssessmentQueryDTO queryDTO) {
        log.info("开始分页查询CPIS评分结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<CpisAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<CpisAssessmentPageVO> result = cpisAssessmentResultMapper.selectCpisAssessmentPage(
            page,
            queryDTO.getTotalScore(),
            queryDTO.getRiskLevel(),
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

    /**
     * 评估结果统计
     */
    @Data
    public static class AssessmentResult {
        private int totalCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private int skipCount = 0;

        @Override
        public String toString() {
            return String.format("CPIS评分结果: 总数=%d, 成功=%d, 失败=%d, 跳过=%d", 
                    totalCount, successCount, failureCount, skipCount);
        }
    }
}
