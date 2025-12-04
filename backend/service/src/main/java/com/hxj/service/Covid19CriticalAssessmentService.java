package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.Covid19CriticalQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import com.hxj.common.vo.Covid19CriticalPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * COVID-19危重型诊断服务
 */
@Slf4j
@Service
public class Covid19CriticalAssessmentService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    @Autowired
    private Covid19CriticalAssessmentMapper assessmentMapper;

    @Autowired
    private Covid19CriticalPatientRelationMapper relationMapper;

    @Autowired(required = false)
    private SofaAssessmentService sofaAssessmentService;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评佰结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Covid19CriticalAssessment performAssessment(Long patientId) {
        return assessSinglePatient(patientId);
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行COVID-19危重型评佰，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("COVID-19危重型评佰失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("COVID-19危重型批量评佰完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 评估单个患者
     * @param patientId 患者ID
     * @return 评佰结果
     */
    @Transactional
    public Covid19CriticalAssessment assessSinglePatient(Long patientId) {
        log.info("开始对患者{}进行COVID-19危重型诊断", patientId);

        // 查询患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.warn("患者{}不存在", patientId);
            throw new RuntimeException("患者不存在");
        }

        // 执行评估
        Covid19CriticalAssessment assessment = performAssessment(patient, "System");

        // 保存评估结果
        assessmentMapper.insert(assessment);

        // 创建并保存关联关系
        Covid19CriticalPatientRelation relation = new Covid19CriticalPatientRelation();
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

        log.info("患者{}COVID-19危重型诊断完成，是否为危重型：{}", 
                patientId, assessment.getIsCriticalType() ? "是" : "否");

        return assessment;
    }

    /**
     * 批量评估所有患者
     * @return 批量评估结果
     */
    @Transactional
    public BatchAssessmentResult assessAllPatients() {
        log.info("开始批量COVID-19危重型诊断");

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
                    log.debug("患者{}已有COVID-19危重型诊断记录，跳过", patient.getPatientId());
                    skipCount++;
                    continue;
                }

                // 执行评估
                Covid19CriticalAssessment assessment = performAssessment(patient, "System");

                // 保存评估结果
                assessmentMapper.insert(assessment);

                // 创建并保存关联关系
                Covid19CriticalPatientRelation relation = new Covid19CriticalPatientRelation();
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
                log.debug("患者{}COVID-19危重型诊断成功", patient.getPatientId());

            } catch (Exception e) {
                failureCount++;
                String errorMsg = String.format("患者%s诊断失败: %s", 
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
        result.setMessage(String.format("批量诊断完成：总数%d，成功%d，失败%d，跳过%d",
                totalCount, successCount, failureCount, skipCount));

        log.info("批量COVID-19危重型诊断完成，{}", result.getMessage());
        return result;
    }

    /**
     * 执行COVID-19危重型诊断
     * 
     * 危重型诊断标准（满足以下任何一条）：
     * 1. 出现呼吸衰竭，且需要机械通气
     * 2. 出现休克
     * 3. 合并其他器官功能衰竭需ICU监护治疗
     */
    private Covid19CriticalAssessment performAssessment(Patient patient, String createdBy) {
        Covid19CriticalAssessment assessment = new Covid19CriticalAssessment();
        
        // 基本信息
        assessment.setAssessmentTime(LocalDateTime.now());
        assessment.setAssessmentMethod("自动评估");
        assessment.setDataSource("患者基本信息、体检详情和SOFA评分");
        assessment.setCreatedBy(createdBy);
        assessment.setUpdatedBy(createdBy);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        assessment.setIsDeleted(0);

        int criteriaMetCount = 0;
        List<String> diagnosisBasis = new ArrayList<>();

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        PhysicalExaminationDetail latestExam = examinations != null && !examinations.isEmpty() ? 
                examinations.get(0) : null;

        // ==================== 危重型诊断标准判断 ====================
        
        // 1. 呼吸衰竭且需要机械通气
        boolean respiratoryFailure = false;
        boolean mechanicalVentilation = false;
        
        // 检查氧合指数是否严重低下（<150mmHg通常提示呼吸衰竭）
        if (patient.getArterialOxygenationIndex() != null) {
            BigDecimal oxyIndex = patient.getArterialOxygenationIndex();
            assessment.setOxygenationIndex(oxyIndex);
            if (oxyIndex.compareTo(new BigDecimal("150")) < 0) {
                respiratoryFailure = true;
                log.debug("患者{}氧合指数{}mmHg，提示呼吸衰竭", patient.getPatientId(), oxyIndex);
            }
        }
        
        // 检查是否使用呼吸机
        if (Boolean.TRUE.equals(patient.getVentilatorUsed())) {
            mechanicalVentilation = true;
            assessment.setVentilatorUsed(true);
            log.debug("患者{}使用呼吸机", patient.getPatientId());
        } else {
            assessment.setVentilatorUsed(false);
        }
        
        assessment.setRespiratoryFailure(respiratoryFailure);
        assessment.setMechanicalVentilation(mechanicalVentilation);
        
        // 判断是否满足"呼吸衰竭且需要机械通气"
        if (respiratoryFailure && mechanicalVentilation) {
            criteriaMetCount++;
            diagnosisBasis.add("呼吸衰竭且需要机械通气");
            log.debug("患者{}满足危重型标准：呼吸衰竭且需要机械通气", patient.getPatientId());
        }

        // 2. 出现休克（收缩压<90mmHg或舒张压<60mmHg，或需要血管活性药物）
        boolean shockPresent = false;
        
        if (latestExam != null) {
            Integer systolic = latestExam.getSystolicBp();
            Integer diastolic = latestExam.getDiastolicBp();
            
            if (systolic != null && diastolic != null) {
                assessment.setSystolicBp(systolic);
                assessment.setDiastolicBp(diastolic);
                assessment.setBloodPressure(systolic + "/" + diastolic);
                
                // 判断是否为休克血压
                if (systolic < 90 || diastolic < 60) {
                    shockPresent = true;
                    log.debug("患者{}血压{}，提示休克", patient.getPatientId(), assessment.getBloodPressure());
                }
            }
        }
        
        // 检查是否使用血管活性药物（也提示休克）
        if (Boolean.TRUE.equals(patient.getDopamineUsed()) ||
            Boolean.TRUE.equals(patient.getDobutamineUsed()) ||
            Boolean.TRUE.equals(patient.getNorepinephrineUsed()) ||
            Boolean.TRUE.equals(patient.getVasoactiveDrugsUsed())) {
            shockPresent = true;
            log.debug("患者{}使用血管活性药物，提示休克", patient.getPatientId());
        }
        
        assessment.setShockPresent(shockPresent);
        
        if (shockPresent) {
            criteriaMetCount++;
            diagnosisBasis.add("出现休克");
            log.debug("患者{}满足危重型标准：出现休克", patient.getPatientId());
        }

        // 3. 合并其他器官功能衰竭需ICU监护治疗
        boolean icuAdmission = false;
        boolean organFailure = false;
        int organFailureCount = 0;
        List<String> failedOrgans = new ArrayList<>();
        
        // 检查是否ICU监护
        if (Boolean.TRUE.equals(patient.getIcuAdmission())) {
            icuAdmission = true;
            assessment.setIntensiveCare(true);
            log.debug("患者{}在ICU监护", patient.getPatientId());
        } else {
            assessment.setIntensiveCare(false);
        }
        
        assessment.setIcuAdmission(icuAdmission);
        
        // 检查器官功能衰竭（可以参考SOFA评分）
        // 简化判断：检查各个器官系统的指标
        
        // 肾功能衰竭（肌酐>176.8 μmol/L或2mg/dL）
        if (patient.getSerumCreatinine() != null && 
            patient.getSerumCreatinine().compareTo(new BigDecimal("176.8")) > 0) {
            organFailureCount++;
            failedOrgans.add("肾脏");
        }
        
        // 肝功能衰竭（胆红素>34.2 μmol/L或2mg/dL）
        if (patient.getTotalBilirubin() != null && 
            patient.getTotalBilirubin().compareTo(new BigDecimal("34.2")) > 0) {
            organFailureCount++;
            failedOrgans.add("肝脏");
        }
        
        // 凝血功能障碍（血小板<100×10^9/L）
        if (patient.getPlateletCount() != null && 
            patient.getPlateletCount().compareTo(new BigDecimal("100")) < 0) {
            organFailureCount++;
            failedOrgans.add("凝血系统");
        }
        
        // 循环系统衰竭（已在休克中判断）
        if (shockPresent) {
            organFailureCount++;
            failedOrgans.add("循环系统");
        }
        
        organFailure = organFailureCount > 0;
        assessment.setOrganFailure(organFailure);
        assessment.setOrganFailureCount(organFailureCount);
        
        if (!failedOrgans.isEmpty()) {
            assessment.setOrganFailureDetails(String.join("、", failedOrgans) + "功能衰竭");
        }
        
        // 判断是否满足"合并其他器官功能衰竭需ICU监护治疗"
        if (organFailure && icuAdmission) {
            criteriaMetCount++;
            diagnosisBasis.add("合并其他器官功能衰竭需ICU监护治疗");
            log.debug("患者{}满足危重型标准：合并器官功能衰竭需ICU监护", patient.getPatientId());
        }

        // 设置诊断结果
        assessment.setCriteriaMetCount(criteriaMetCount);
        assessment.setIsCriticalType(criteriaMetCount > 0);

        // 判断严重程度
        if (criteriaMetCount >= 3) {
            assessment.setSeverityLevel("危重型（极危重）");
            assessment.setAssessmentConclusion(String.format("满足%d项COVID-19危重型诊断标准，病情极其危重", 
                    criteriaMetCount));
            assessment.setRecommendedAction("立即进行全面生命支持治疗，包括机械通气、血管活性药物、" +
                    "器官功能支持（ECMO、CRRT等），密切监测所有生命体征和器官功能，" +
                    "考虑使用抗病毒药物、免疫调节治疗、抗凝治疗等综合措施");
        } else if (criteriaMetCount == 2) {
            assessment.setSeverityLevel("危重型（高危）");
            assessment.setAssessmentConclusion(String.format("满足%d项COVID-19危重型诊断标准，病情危重", 
                    criteriaMetCount));
            assessment.setRecommendedAction("需要在ICU进行密切监护和治疗，准备升级生命支持措施，" +
                    "积极进行器官功能保护，防止多器官功能衰竭，及时调整治疗方案");
        } else if (criteriaMetCount == 1) {
            assessment.setSeverityLevel("危重型");
            assessment.setAssessmentConclusion("满足1项COVID-19危重型诊断标准，诊断为危重型");
            assessment.setRecommendedAction("建议转入或继续在ICU治疗，给予相应的生命支持，" +
                    "密切监测病情变化，预防并发症，及时评估治疗效果");
        } else {
            assessment.setSeverityLevel("非危重型");
            assessment.setAssessmentConclusion("未满足COVID-19危重型诊断标准，暂不诊断为危重型");
            assessment.setRecommendedAction("继续密切监测病情，预防向危重型发展，" +
                    "注意早期识别病情恶化征象，及时调整治疗方案");
        }

        // 设置诊断依据
        if (!diagnosisBasis.isEmpty()) {
            assessment.setDiagnosisBasis(String.join("；", diagnosisBasis));
        } else {
            assessment.setDiagnosisBasis("未满足任何危重型诊断标准");
        }

        log.debug("患者{}COVID-19危重型诊断完成：满足{}项标准，{}型", 
                patient.getPatientId(), criteriaMetCount, 
                assessment.getIsCriticalType() ? "危重" : "非危重");

        return assessment;
    }

    /**
     * 分页查询COVID-19危重型诊断结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<Covid19CriticalPageVO> queryCovid19CriticalPage(Covid19CriticalQueryDTO queryDTO) {
        log.info("开始分页查询COVID-19危重型诊断结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<Covid19CriticalPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<Covid19CriticalPageVO> result = assessmentMapper.selectCovid19CriticalPage(
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
}
