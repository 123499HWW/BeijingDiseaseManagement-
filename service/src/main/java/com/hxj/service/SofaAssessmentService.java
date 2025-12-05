package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.SofaAssessmentQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import com.hxj.common.vo.SofaAssessmentPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SOFA评分服务
 * SOFA (Sequential Organ Failure Assessment) 用于评估器官功能衰竭的严重程度
 */
@Slf4j
@Service
public class SofaAssessmentService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    @Autowired
    private SofaAssessmentMapper assessmentMapper;

    @Autowired
    private SofaPatientRelationMapper relationMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public SofaAssessment performAssessment(Long patientId) {
        return assessSinglePatient(patientId);
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行SOFA评估，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("SOFA评估失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("SOFA批量评估完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 评估单个患者
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional
    public SofaAssessment assessSinglePatient(Long patientId) {
        log.info("开始对患者{}进行SOFA评分", patientId);

        // 查询患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.warn("患者{}不存在", patientId);
            throw new RuntimeException("患者不存在");
        }

        // 执行评估
        SofaAssessment assessment = performAssessment(patient, "System");

        // 保存评估结果
        assessmentMapper.insert(assessment);

        // 创建并保存关联关系
        SofaPatientRelation relation = new SofaPatientRelation();
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

        log.info("患者{}SOFA评分完成，总分：{}，严重程度：{}", 
                patientId, assessment.getTotalScore(), assessment.getSeverityLevel());

        return assessment;
    }

    /**
     * 批量评估所有患者
     * @return 批量评估结果
     */
    @Transactional
    public BatchAssessmentResult assessAllPatients() {
        log.info("开始批量SOFA评分");

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
                    log.debug("患者{}已有SOFA评分记录，跳过", patient.getPatientId());
                    skipCount++;
                    continue;
                }

                // 执行评估
                SofaAssessment assessment = performAssessment(patient, "System");

                // 保存评估结果
                assessmentMapper.insert(assessment);

                // 创建并保存关联关系
                SofaPatientRelation relation = new SofaPatientRelation();
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
                log.debug("患者{}SOFA评分成功", patient.getPatientId());

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

        log.info("批量SOFA评分完成，{}", result.getMessage());
        return result;
    }

    /**
     * 执行SOFA评分
     * 
     * SOFA评分标准（6个器官系统，每个0-4分，总分0-24分）：
     * 1. 呼吸系统：氧合指数(PaO2/FiO2)
     * 2. 凝血系统：血小板计数
     * 3. 肝脏系统：胆红素
     * 4. 心血管系统：平均动脉压(MAP)和血管活性药物
     * 5. 神经系统：格拉斯哥昏迷评分(GCS)
     * 6. 肾脏系统：肌酐
     */
    private SofaAssessment performAssessment(Patient patient, String createdBy) {
        SofaAssessment assessment = new SofaAssessment();
        
        // 基本信息
        assessment.setAssessmentTime(LocalDateTime.now());
        assessment.setAssessmentMethod("自动评估");
        assessment.setDataSource("患者基本信息、检验报告和体检详情");
        assessment.setCreatedBy(createdBy);
        assessment.setUpdatedBy(createdBy);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        assessment.setIsDeleted(0);

        int totalScore = 0;
        int organFailures = 0;

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        PhysicalExaminationDetail latestExam = examinations != null && !examinations.isEmpty() ? 
                examinations.get(0) : null;

        // ==================== 1. 呼吸系统评分 ====================
        // 基于氧合指数(PaO2/FiO2)
        int respirationScore = 0;
        if (patient.getArterialOxygenationIndex() != null) {
            BigDecimal oxyIndex = patient.getArterialOxygenationIndex();
            assessment.setOxygenationIndex(oxyIndex);
            
            if (oxyIndex.compareTo(new BigDecimal("400")) < 0) {
                respirationScore = 1; // <400
            }
            if (oxyIndex.compareTo(new BigDecimal("300")) < 0) {
                respirationScore = 2; // <300
            }
            if (oxyIndex.compareTo(new BigDecimal("200")) < 0) {
                respirationScore = 3; // <200 with respiratory support
            }
            if (oxyIndex.compareTo(new BigDecimal("100")) < 0) {
                respirationScore = 4; // <100 with respiratory support
            }
            
            log.debug("患者{}氧合指数{}，呼吸系统评分{}", patient.getPatientId(), oxyIndex, respirationScore);
        } else {
            log.debug("患者{}氧合指数数据不可用，呼吸系统评分0", patient.getPatientId());
        }
        assessment.setRespirationScore(respirationScore);
        totalScore += respirationScore;
        if (respirationScore >= 2) organFailures++;

        // ==================== 2. 凝血系统评分 ====================
        // 基于血小板计数
        int coagulationScore = 0;
        if (patient.getPlateletCount() != null) {
            BigDecimal platelet = patient.getPlateletCount();
            assessment.setPlateletCount(platelet);
            
            if (platelet.compareTo(new BigDecimal("150")) < 0) {
                coagulationScore = 1; // <150
            }
            if (platelet.compareTo(new BigDecimal("100")) < 0) {
                coagulationScore = 2; // <100
            }
            if (platelet.compareTo(new BigDecimal("50")) < 0) {
                coagulationScore = 3; // <50
            }
            if (platelet.compareTo(new BigDecimal("20")) < 0) {
                coagulationScore = 4; // <20
            }
            
            log.debug("患者{}血小板计数{}×10^9/L，凝血系统评分{}", patient.getPatientId(), platelet, coagulationScore);
        } else {
            log.debug("患者{}血小板计数数据不可用，凝血系统评分0", patient.getPatientId());
        }
        assessment.setCoagulationScore(coagulationScore);
        totalScore += coagulationScore;
        if (coagulationScore >= 2) organFailures++;

        // ==================== 3. 肝脏系统评分 ====================
        // 基于胆红素
        int liverScore = 0;
        if (patient.getTotalBilirubin() != null) {
            BigDecimal bilirubin = patient.getTotalBilirubin();
            assessment.setBilirubin(bilirubin);
            
            if (bilirubin.compareTo(new BigDecimal("33")) >= 0) {
                liverScore = 1; // ≥33 μmol/L (2.0 mg/dl)
            }
            if (bilirubin.compareTo(new BigDecimal("102")) >= 0) {
                liverScore = 2; // ≥102 μmol/L (6.0 mg/dl)
            }
            if (bilirubin.compareTo(new BigDecimal("204")) >= 0) {
                liverScore = 3; // ≥204 μmol/L (12.0 mg/dl)
            }
            if (bilirubin.compareTo(new BigDecimal("204")) > 0) {
                liverScore = 4; // >204 μmol/L (>12.0 mg/dl)
            }
            
            log.debug("患者{}总胆红素{}μmol/L，肝脏系统评分{}", patient.getPatientId(), bilirubin, liverScore);
        } else {
            log.debug("患者{}胆红素数据不可用，肝脏系统评分0", patient.getPatientId());
        }
        assessment.setLiverScore(liverScore);
        totalScore += liverScore;
        if (liverScore >= 2) organFailures++;

        // ==================== 4. 心血管系统评分 ====================
        // 基于平均动脉压(MAP)和血管活性药物使用
        int cardiovascularScore = 0;
        
        // 计算平均动脉压 MAP = (收缩压 + 2×舒张压) / 3
        if (latestExam != null && latestExam.getSystolicBp() != null && latestExam.getDiastolicBp() != null) {
            Integer systolic = latestExam.getSystolicBp();
            Integer diastolic = latestExam.getDiastolicBp();
            assessment.setSystolicBp(systolic);
            assessment.setDiastolicBp(diastolic);
            
            // MAP = 1/3 * SBP + 2/3 * DBP
            BigDecimal map = new BigDecimal(systolic).add(new BigDecimal(diastolic * 2))
                    .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
            assessment.setMeanArterialPressure(map);
            
            // 检查是否使用血管活性药物
            boolean vasoactiveDrugs = Boolean.TRUE.equals(patient.getDopamineUsed()) ||
                                     Boolean.TRUE.equals(patient.getDobutamineUsed()) ||
                                     Boolean.TRUE.equals(patient.getNorepinephrineUsed()) ||
                                     Boolean.TRUE.equals(patient.getVasoactiveDrugsUsed());
            assessment.setVasoactiveDrugs(vasoactiveDrugs);
            
            // 评分标准
            if (map.compareTo(new BigDecimal("70")) < 0) {
                cardiovascularScore = 1; // MAP < 70 mmHg
            }
            if (vasoactiveDrugs) {
                // 使用血管活性药物，根据药物类型和剂量评分
                if (Boolean.TRUE.equals(patient.getDopamineUsed()) || 
                    Boolean.TRUE.equals(patient.getDobutamineUsed())) {
                    cardiovascularScore = Math.max(cardiovascularScore, 2); // 多巴胺 ≤5 或任何剂量多巴酚丁胺
                }
                if (Boolean.TRUE.equals(patient.getNorepinephrineUsed())) {
                    cardiovascularScore = Math.max(cardiovascularScore, 3); // 使用去甲肾上腺素
                }
            }
            
            log.debug("患者{}MAP {}mmHg，血管活性药物{}，心血管系统评分{}", 
                    patient.getPatientId(), map, vasoactiveDrugs ? "是" : "否", cardiovascularScore);
        } else {
            log.debug("患者{}血压数据不可用，心血管系统评分0", patient.getPatientId());
        }
        assessment.setCardiovascularScore(cardiovascularScore);
        totalScore += cardiovascularScore;
        if (cardiovascularScore >= 2) organFailures++;

        // ==================== 5. 神经系统评分 ====================
        // 基于格拉斯哥昏迷评分(GCS)
        // 注：由于GCS数据不可用，暂时设为0分
        int cnsScore = 0;
        assessment.setGlasgowComaScore(15); // 默认正常值
        assessment.setCnsScore(cnsScore);
        totalScore += cnsScore;
        if (cnsScore >= 2) organFailures++;
        log.debug("患者{}格拉斯哥评分数据不可用，神经系统评分0", patient.getPatientId());

        // ==================== 6. 肾脏系统评分 ====================
        // 基于肌酐
        int renalScore = 0;
        if (patient.getSerumCreatinine() != null) {
            BigDecimal creatinine = patient.getSerumCreatinine();
            assessment.setCreatinine(creatinine);
            
            if (creatinine.compareTo(new BigDecimal("110")) >= 0) {
                renalScore = 1; // ≥110 μmol/L (1.2 mg/dl)
            }
            if (creatinine.compareTo(new BigDecimal("171")) >= 0) {
                renalScore = 2; // ≥171 μmol/L (2.0 mg/dl)
            }
            if (creatinine.compareTo(new BigDecimal("300")) >= 0) {
                renalScore = 3; // ≥300 μmol/L (3.5 mg/dl)
            }
            if (creatinine.compareTo(new BigDecimal("440")) >= 0) {
                renalScore = 4; // ≥440 μmol/L (5.0 mg/dl)
            }
            
            log.debug("患者{}肌酐{}μmol/L，肾脏系统评分{}", patient.getPatientId(), creatinine, renalScore);
        } else {
            log.debug("患者{}肌酐数据不可用，肾脏系统评分0", patient.getPatientId());
        }
        assessment.setRenalScore(renalScore);
        totalScore += renalScore;
        if (renalScore >= 2) organFailures++;

        // 设置总分和器官衰竭数量
        assessment.setTotalScore(totalScore);
        assessment.setOrganFailures(organFailures);

        // 判断严重程度
        if (totalScore >= 15) {
            assessment.setSeverityLevel("极重度");
            assessment.setAssessmentConclusion(String.format("SOFA评分%d分，%d个器官系统衰竭，病情极其危重", 
                    totalScore, organFailures));
            assessment.setRecommendedAction("立即收入ICU，需要多器官支持治疗，密切监测所有生命体征和器官功能");
        } else if (totalScore >= 12) {
            assessment.setSeverityLevel("重度");
            assessment.setAssessmentConclusion(String.format("SOFA评分%d分，%d个器官系统衰竭，病情危重", 
                    totalScore, organFailures));
            assessment.setRecommendedAction("建议立即转入ICU，可能需要器官支持治疗，加强监护和治疗");
        } else if (totalScore >= 8) {
            assessment.setSeverityLevel("中度");
            assessment.setAssessmentConclusion(String.format("SOFA评分%d分，%d个器官系统衰竭，病情较重", 
                    totalScore, organFailures));
            assessment.setRecommendedAction("密切监测器官功能，考虑转入重症监护病房，及时调整治疗方案");
        } else if (totalScore >= 4) {
            assessment.setSeverityLevel("轻度");
            assessment.setAssessmentConclusion(String.format("SOFA评分%d分，%d个器官系统受累，病情相对稳定", 
                    totalScore, organFailures));
            assessment.setRecommendedAction("继续监测器官功能变化，积极治疗原发病，预防器官功能恶化");
        } else {
            assessment.setSeverityLevel("正常或轻微");
            assessment.setAssessmentConclusion(String.format("SOFA评分%d分，器官功能基本正常", totalScore));
            assessment.setRecommendedAction("继续常规治疗和监测，定期复查SOFA评分");
        }

        log.debug("患者{}SOFA评分完成：总分{}分，器官衰竭数{}，严重程度：{}", 
                patient.getPatientId(), totalScore, organFailures, assessment.getSeverityLevel());

        return assessment;
    }

    /**
     * 分页查询SOFA评分结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<SofaAssessmentPageVO> querySofaPage(SofaAssessmentQueryDTO queryDTO) {
        log.info("开始分页查询SOFA评分结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<SofaAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<SofaAssessmentPageVO> result = assessmentMapper.selectSofaPage(
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
}
