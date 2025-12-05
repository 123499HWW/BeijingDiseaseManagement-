package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.QsofaAssessmentQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import com.hxj.common.vo.QsofaAssessmentPageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * qSOFA评分服务
 * qSOFA (Quick Sequential Organ Failure Assessment) 用于快速识别脓毒症高风险患者
 */
@Slf4j
@Service
public class QsofaAssessmentService {

    @Resource
    private PatientMapper patientMapper;

    @Resource
    private PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    @Resource
    private QsofaAssessmentMapper assessmentMapper;

    @Resource
    private QsofaPatientRelationMapper relationMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public QsofaAssessment performAssessment(Long patientId) {
        return assessSinglePatient(patientId);
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行qSOFA评估，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("qSOFA评估失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("qSOFA批量评估完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 评估单个患者
     * @param patientId 患者ID
     * @return 评佰结果
     */
    @Transactional
    public QsofaAssessment assessSinglePatient(Long patientId) {
        log.info("开始对患者{}进行qSOFA评分", patientId);

        // 查询患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.warn("患者{}不存在", patientId);
            throw new RuntimeException("患者不存在");
        }

        // 执行评估
        QsofaAssessment assessment = performAssessment(patient, "System");

        // 保存评估结果
        assessmentMapper.insert(assessment);

        // 创建并保存关联关系
        QsofaPatientRelation relation = new QsofaPatientRelation();
        relation.setAssessmentId(assessment.getAssessmentId());
        relation.setPatientId(patientId);
        relation.setPatientNumber(patient.getPatientNumber());
        relation.setRelationType("ASSESSMENT");
        relation.setRelationStatus("ACTIVE");
        relation.setCreatedBy("System");
        relation.setUpdatedBy("System");
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        relation.setIsDeleted(0);
        relationMapper.insert(relation);

        log.info("患者{}qSOFA评分完成，总分：{}，风险等级：{}", 
                patientId, assessment.getTotalScore(), assessment.getRiskLevel());

        return assessment;
    }

    /**
     * 批量评估所有患者
     * @return 批量评估结果
     */
    @Transactional
    public BatchAssessmentResult assessAllPatients() {
        log.info("开始批量qSOFA评分");

        BatchAssessmentResult result = new BatchAssessmentResult();
        result.setStartTime(LocalDateTime.now());

        // 查询所有未删除的患者
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        List<Patient> patients = patientMapper.selectList(queryWrapper);

        if (patients == null || patients.isEmpty()) {
            log.warn("未找到任何患者");
            result.setEndTime(LocalDateTime.now());
            result.setTotalCount(0);
            result.setMessage("未找到任何患者");
            return result;
        }

        int totalCount = patients.size();
        int successCount = 0;
        int failureCount = 0;
        int skipCount = 0;
        List<String> errors = new ArrayList<>();

        for (Patient patient : patients) {
            try {
                // 检查是否已有评估记录
                List<Long> existingAssessmentIds = relationMapper.selectAssessmentIdsByPatientId(patient.getPatientId());
                if (!existingAssessmentIds.isEmpty()) {
                    log.debug("患者{}已有qSOFA评分记录，跳过", patient.getPatientId());
                    skipCount++;
                    continue;
                }

                // 执行评估
                QsofaAssessment assessment = performAssessment(patient, "System");

                // 保存评估结果
                assessmentMapper.insert(assessment);

                // 创建并保存关联关系
                QsofaPatientRelation relation = new QsofaPatientRelation();
                relation.setAssessmentId(assessment.getAssessmentId());
                relation.setPatientId(patient.getPatientId());
                relation.setPatientNumber(patient.getPatientNumber());
                relation.setRelationType("ASSESSMENT");
                relation.setRelationStatus("ACTIVE");
                relation.setCreatedBy("System");
                relation.setUpdatedBy("System");
                relation.setCreatedAt(LocalDateTime.now());
                relation.setUpdatedAt(LocalDateTime.now());
                relation.setIsDeleted(0);
                relationMapper.insert(relation);

                successCount++;
                log.debug("患者{}qSOFA评分成功", patient.getPatientId());

            } catch (Exception e) {
                failureCount++;
                String errorMsg = String.format("患者%s评分失败: %s", 
                        patient.getPatientId(), e.getMessage());
                errors.add(errorMsg);
                log.error(errorMsg, e);
            }
        }

        result.setEndTime(LocalDateTime.now());
        result.setTotalCount(totalCount);
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setSkipCount(skipCount);
        result.setErrors(errors);
        result.setMessage(String.format("批量评分完成：总数%d，成功%d，失败%d，跳过%d",
                totalCount, successCount, failureCount, skipCount));

        log.info("批量qSOFA评分完成，{}", result.getMessage());
        return result;
    }

    /**
     * 执行qSOFA评分
     * qSOFA评分标准（每项1分，总分0-3分）：
     * 1. 意识障碍
     * 2. 呼吸频率≥22次/分
     * 3. 收缩压≤90mmHg
     * 总分≥2分提示脓毒症高风险
     */
    private QsofaAssessment performAssessment(Patient patient, String createdBy) {
        QsofaAssessment assessment = new QsofaAssessment();
        
        // 基本信息
        assessment.setAssessmentTime(LocalDateTime.now());
        assessment.setAssessmentMethod("自动评估");
        assessment.setDataSource("患者基本信息和体检详情");
        assessment.setCreatedBy(createdBy);
        assessment.setUpdatedBy(createdBy);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        assessment.setIsDeleted(0);

        int totalScore = 0;
        List<String> criteria = new ArrayList<>();

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        PhysicalExaminationDetail latestExam = examinations != null && !examinations.isEmpty() ? 
                examinations.getFirst() : null;

        // ==================== qSOFA评分项判断 ====================
        
        // 1. 意识障碍（从体检详情的general_condition字段查询）
        boolean consciousnessAltered = false;
        String consciousnessDesc = "";
        if (latestExam != null && latestExam.getGeneralCondition() != null) {
            String generalCondition = latestExam.getGeneralCondition();
            if (generalCondition.contains("意识障碍")) {
                consciousnessAltered = true;
                consciousnessDesc = "意识障碍";
                log.debug("患者{}体检记录显示存在意识障碍", patient.getPatientId());
            }
        } else {
            log.debug("患者{}意识状态数据不可用", patient.getPatientId());
        }
        
        assessment.setConsciousnessAltered(consciousnessAltered);
        assessment.setConsciousnessDescription(consciousnessDesc);
        if (consciousnessAltered) {
            totalScore++;
            criteria.add("意识障碍");
            log.debug("患者{}存在意识障碍，qSOFA评分+1", patient.getPatientId());
        }

        // 2. 呼吸频率≥22次/分
        if (latestExam != null && latestExam.getRespiration() != null) {
            Integer respiratoryRate = latestExam.getRespiration();
            assessment.setRespiratoryRate(respiratoryRate);
            assessment.setRespiratoryRateHigh(respiratoryRate >= 22);
            if (assessment.getRespiratoryRateHigh()) {
                totalScore++;
                criteria.add("呼吸频率≥22次/分");
                log.debug("患者{}呼吸频率{}次/分，≥22次/分，qSOFA评分+1", 
                        patient.getPatientId(), respiratoryRate);
            }
        } else {
            assessment.setRespiratoryRateHigh(false);
            log.debug("患者{}呼吸频率数据不可用", patient.getPatientId());
        }

        // 3. 收缩压≤90mmHg
        if (latestExam != null && latestExam.getSystolicBp() != null) {
            Integer systolicBp = latestExam.getSystolicBp();
            Integer diastolicBp = latestExam.getDiastolicBp();
            
            assessment.setSystolicBp(systolicBp);
            assessment.setDiastolicBp(diastolicBp);
            
            if (diastolicBp != null) {
                assessment.setBloodPressure(systolicBp + "/" + diastolicBp);
            } else {
                assessment.setBloodPressure(systolicBp + "/-");
            }
            
            assessment.setSystolicBpLow(systolicBp <= 90);
            if (assessment.getSystolicBpLow()) {
                totalScore++;
                criteria.add("收缩压≤90mmHg");
                log.debug("患者{}收缩压{}mmHg，≤90mmHg，qSOFA评分+1", 
                        patient.getPatientId(), systolicBp);
            }
        } else {
            assessment.setSystolicBpLow(false);
            log.debug("患者{}血压数据不可用", patient.getPatientId());
        }

        // 设置评分结果
        assessment.setTotalScore(totalScore);

        // 判断风险等级
        if (totalScore >= 2) {
            assessment.setRiskLevel("高风险");
            assessment.setAssessmentConclusion(String.format("qSOFA评分%d分（≥2分），提示脓毒症高风险", totalScore));
            assessment.setRecommendedAction("建议立即进行进一步评估，包括乳酸检测、血培养等，" +
                    "考虑启动脓毒症集束化治疗，密切监测生命体征");
        } else {
            assessment.setRiskLevel("低风险");
            assessment.setAssessmentConclusion(String.format("qSOFA评分%d分（<2分），脓毒症风险较低", totalScore));
            assessment.setRecommendedAction("继续观察，定期重新评估，如病情变化及时复查qSOFA评分");
        }

        // 记录满足的标准
        if (!criteria.isEmpty()) {
            log.debug("患者{}满足以下qSOFA标准：{}", patient.getPatientId(), String.join("、", criteria));
        }

        log.debug("患者{}qSOFA评分完成：总分{}分，风险等级：{}", 
                patient.getPatientId(), totalScore, assessment.getRiskLevel());

        return assessment;
    }

    /**
     * 分页查询qSOFA评分结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<QsofaAssessmentPageVO> queryQsofaPage(QsofaAssessmentQueryDTO queryDTO) {
        log.info("开始分页查询qSOFA评分结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<QsofaAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<QsofaAssessmentPageVO> result = assessmentMapper.selectQsofaPage(
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
}
