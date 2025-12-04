package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.SeverePneumoniaQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import com.hxj.common.vo.SeverePneumoniaPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 重症肺炎诊断服务
 */
@Slf4j
@Service
public class SeverePneumoniaDiagnosisService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    @Autowired
    private SeverePneumoniaDiagnosisMapper diagnosisMapper;

    @Autowired
    private SeverePneumoniaPatientRelationMapper relationMapper;

    /**
     * 为单个患者执行诊断（标准接口）
     * @param patientId 患者ID
     * @return 诊断结果
     */
    @Transactional(rollbackFor = Exception.class)
    public SeverePneumoniaDiagnosis performDiagnosis(Long patientId) {
        return diagnoseSinglePatient(patientId);
    }
    
    /**
     * 批量诊断患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchDiagnosis(List<Long> patientIds) {
        log.info("开始批量执行重症肺炎诊断，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performDiagnosis(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("重症肺炎诊断失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("重症肺炎批量诊断完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 诊断单个患者
     * @param patientId 患者ID
     * @return 诊断结果
     */
    @Transactional
    public SeverePneumoniaDiagnosis diagnoseSinglePatient(Long patientId) {
        log.info("开始对患者{}进行重症肺炎诊断", patientId);

        // 查询患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.warn("患者{}不存在", patientId);
            throw new RuntimeException("患者不存在");
        }

        // 检查是否已有诊断记录，如果有则删除旧记录
        List<Long> existingDiagnosisIds = relationMapper.selectDiagnosisIdsByPatientId(patientId);
        if (!existingDiagnosisIds.isEmpty()) {
            log.debug("患者{}已有诊断记录，将删除旧记录并重新诊断", patientId);
            // 逻辑删除旧的关联记录
            for (Long diagnosisId : existingDiagnosisIds) {
                // 删除关联关系
                QueryWrapper<SeverePneumoniaPatientRelation> relationQuery = new QueryWrapper<>();
                relationQuery.eq("diagnosis_id", diagnosisId);
                relationQuery.eq("patient_id", patientId);
                SeverePneumoniaPatientRelation oldRelation = new SeverePneumoniaPatientRelation();
                oldRelation.setIsDeleted(1);
                oldRelation.setUpdatedAt(LocalDateTime.now());
                oldRelation.setUpdatedBy("System");
                relationMapper.update(oldRelation, relationQuery);
                
                // 删除诊断记录
                SeverePneumoniaDiagnosis oldDiagnosis = new SeverePneumoniaDiagnosis();
                oldDiagnosis.setIsDeleted(1);
                oldDiagnosis.setUpdatedAt(LocalDateTime.now());
                oldDiagnosis.setUpdatedBy("System");
                QueryWrapper<SeverePneumoniaDiagnosis> diagnosisQuery = new QueryWrapper<>();
                diagnosisQuery.eq("diagnosis_id", diagnosisId);
                diagnosisMapper.update(oldDiagnosis, diagnosisQuery);
            }
        }

        // 执行诊断
        SeverePneumoniaDiagnosis diagnosis = performDiagnosis(patient, "System");

        // 保存诊断结果
        diagnosisMapper.insert(diagnosis);

        // 创建并保存关联关系
        SeverePneumoniaPatientRelation relation = new SeverePneumoniaPatientRelation();
        relation.setDiagnosisId(diagnosis.getDiagnosisId());
        relation.setPatientId(patientId);
        relation.setPatientNumber(patient.getPatientNumber());
        relation.setRelationType("DIAGNOSIS");
        relation.setRelationStatus("ACTIVE");
        relation.setCreatedBy("System");
        relation.setUpdatedBy("System");
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        relation.setIsDeleted(0);
        relationMapper.insert(relation);

        log.info("患者{}重症肺炎诊断完成，诊断结果：{}", patientId, 
                diagnosis.getIsSeverePneumonia() ? "重症肺炎" : "非重症肺炎");

        return diagnosis;
    }

    /**
     * 批量诊断所有患者
     * @return 批量诊断结果
     */
    @Transactional
    public BatchAssessmentResult diagnoseAllPatients() {
        log.info("开始批量重症肺炎诊断");

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
        int updateCount = 0;  // 记录更新的数量
        List<String> errors = new ArrayList<>();

        for (Patient patient : patients) {
            try {
                // 检查是否已有诊断记录，如果有则删除旧记录
                List<Long> existingDiagnosisIds = relationMapper.selectDiagnosisIdsByPatientId(patient.getPatientId());
                boolean isUpdate = false;
                if (!existingDiagnosisIds.isEmpty()) {
                    log.debug("患者{}已有诊断记录，将删除旧记录并重新诊断", patient.getPatientId());
                    isUpdate = true;
                    // 逻辑删除旧的关联记录
                    for (Long diagnosisId : existingDiagnosisIds) {
                        // 删除关联关系
                        QueryWrapper<SeverePneumoniaPatientRelation> relationQuery = new QueryWrapper<>();
                        relationQuery.eq("diagnosis_id", diagnosisId);
                        relationQuery.eq("patient_id", patient.getPatientId());
                        SeverePneumoniaPatientRelation oldRelation = new SeverePneumoniaPatientRelation();
                        oldRelation.setIsDeleted(1);
                        oldRelation.setUpdatedAt(LocalDateTime.now());
                        oldRelation.setUpdatedBy("System");
                        relationMapper.update(oldRelation, relationQuery);
                        
                        // 删除诊断记录
                        SeverePneumoniaDiagnosis oldDiagnosis = new SeverePneumoniaDiagnosis();
                        oldDiagnosis.setIsDeleted(1);
                        oldDiagnosis.setUpdatedAt(LocalDateTime.now());
                        oldDiagnosis.setUpdatedBy("System");
                        QueryWrapper<SeverePneumoniaDiagnosis> diagnosisQuery = new QueryWrapper<>();
                        diagnosisQuery.eq("diagnosis_id", diagnosisId);
                        diagnosisMapper.update(oldDiagnosis, diagnosisQuery);
                    }
                }

                // 执行诊断
                SeverePneumoniaDiagnosis diagnosis = performDiagnosis(patient, "System");

                // 保存诊断结果
                diagnosisMapper.insert(diagnosis);

                // 创建并保存关联关系
                SeverePneumoniaPatientRelation relation = new SeverePneumoniaPatientRelation();
                relation.setDiagnosisId(diagnosis.getDiagnosisId());
                relation.setPatientId(patient.getPatientId());
                relation.setPatientNumber(patient.getPatientNumber());
                relation.setRelationType("DIAGNOSIS");
                relation.setRelationStatus("ACTIVE");
                relation.setCreatedBy("System");
                relation.setUpdatedBy("System");
                relation.setCreatedAt(LocalDateTime.now());
                relation.setUpdatedAt(LocalDateTime.now());
                relation.setIsDeleted(0);
                relationMapper.insert(relation);

                successCount++;
                if (isUpdate) {
                    updateCount++;
                }
                log.debug("患者{}诊断成功{}", patient.getPatientId(), isUpdate ? "（更新）" : "");

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
        result.setSkipCount(0);  // 不再跳过任何记录
        result.setErrors(errors);
        result.setMessage(String.format("批量诊断完成：总数%d，成功%d（其中更新%d），失败%d",
                totalCount, successCount, updateCount, failureCount));

        log.info("批量重症肺炎诊断完成，{}", result.getMessage());
        return result;
    }

    /**
     * 执行重症肺炎诊断
     * 诊断标准：
     * 主要标准（满足1项即为重症肺炎）：
     * 1. 需要有创机械通气
     * 2. 感染性休克需要血管活性药物
     * 
     * 次要标准（满足3项即为重症肺炎）：
     * 1. 呼吸频率≥30次/分
     * 2. PaO2/FiO2≤250mmHg
     * 3. 多肺叶浸润
     * 4. 意识障碍或定向障碍
     * 5. 血尿素氮≥7mmol/L
     * 6. 低血压需要液体复苏
     */
    private SeverePneumoniaDiagnosis performDiagnosis(Patient patient, String createdBy) {
        SeverePneumoniaDiagnosis diagnosis = new SeverePneumoniaDiagnosis();
        
        // 基本信息
        diagnosis.setDiagnosisTime(LocalDateTime.now());
        diagnosis.setDiagnosisMethod("自动诊断");
        diagnosis.setDataSource("患者基本信息和体检详情");
        diagnosis.setCreatedBy(createdBy);
        diagnosis.setUpdatedBy(createdBy);
        diagnosis.setCreatedAt(LocalDateTime.now());
        diagnosis.setUpdatedAt(LocalDateTime.now());
        diagnosis.setIsDeleted(0);

        int majorCriteriaCount = 0;
        int minorCriteriaCount = 0;
        List<String> diagnosisBasis = new ArrayList<>();

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        PhysicalExaminationDetail latestExam = examinations != null && !examinations.isEmpty() ? 
                examinations.get(0) : null;

        // ==================== 主要标准判断 ====================
        
        // 1. 机械通气判断（基于是否使用呼吸机）
        Boolean mechanicalVentilation = patient.getVentilatorUsed();
        diagnosis.setMechanicalVentilation(mechanicalVentilation != null && mechanicalVentilation);
        if (diagnosis.getMechanicalVentilation()) {
            majorCriteriaCount++;
            diagnosisBasis.add("使用有创机械通气");
            log.debug("患者{}使用有创机械通气", patient.getPatientId());
        }

        // 2. 血管活性药物判断（多巴胺、多巴酚丁胺、去甲肾上腺素等）
        boolean vasoactiveDrugs = Boolean.TRUE.equals(patient.getDopamineUsed()) ||
                                  Boolean.TRUE.equals(patient.getDobutamineUsed()) ||
                                  Boolean.TRUE.equals(patient.getNorepinephrineUsed()) ||
                                  Boolean.TRUE.equals(patient.getVasoactiveDrugsUsed());
        diagnosis.setVasoactiveDrugs(vasoactiveDrugs);
        if (vasoactiveDrugs) {
            majorCriteriaCount++;
            diagnosisBasis.add("需要血管活性药物");
            log.debug("患者{}需要血管活性药物", patient.getPatientId());
        }

        // ==================== 次要标准判断 ====================
        
        // 1. 呼吸频率≥30次/分
        if (latestExam != null && latestExam.getRespiration() != null) {
            Integer respiratoryRate = latestExam.getRespiration();
            diagnosis.setRespiratoryRate(respiratoryRate);
            diagnosis.setRespiratoryRateHigh(respiratoryRate >= 30);
            if (diagnosis.getRespiratoryRateHigh()) {
                minorCriteriaCount++;
                diagnosisBasis.add("呼吸频率≥30次/分");
                log.debug("患者{}呼吸频率{}次/分，≥30次/分", patient.getPatientId(), respiratoryRate);
            }
        } else {
            diagnosis.setRespiratoryRateHigh(false);
        }

        // 2. PaO2/FiO2≤250mmHg（使用氧合指数）
        if (patient.getArterialOxygenationIndex() != null) {
            BigDecimal oxyIndex = patient.getArterialOxygenationIndex();
            diagnosis.setOxygenationIndex(oxyIndex);
            diagnosis.setOxygenationIndexLow(oxyIndex.compareTo(new BigDecimal("250")) <= 0);
            if (diagnosis.getOxygenationIndexLow()) {
                minorCriteriaCount++;
                diagnosisBasis.add("PaO2/FiO2≤250mmHg");
                log.debug("患者{}氧合指数{}mmHg，≤250mmHg", patient.getPatientId(), oxyIndex);
            }
        } else {
            diagnosis.setOxygenationIndexLow(false);
        }

        // 3. 多肺叶浸润（数据不可用，直接记为0）
        boolean multilobarInfiltrates = false;
        diagnosis.setMultilobarInfiltrates(multilobarInfiltrates);
        diagnosis.setInfiltratesDescription("数据不可用");
        log.debug("患者{}多肺叶浸润数据不可用，评分: 0", patient.getPatientId());

        // 4. 意识障碍或定向障碍（从主诉和现病史分析）
        boolean consciousnessDisorder = false;
        String consciousnessDesc = "";
        if (patient.getChiefComplaint() != null) {
            String complaint = patient.getChiefComplaint().toLowerCase();
            if (complaint.contains("意识") && (complaint.contains("障碍") || complaint.contains("模糊") || 
                complaint.contains("不清") || complaint.contains("淡漠"))) {
                consciousnessDisorder = true;
                consciousnessDesc = "意识障碍";
            } else if (complaint.contains("定向") && complaint.contains("障碍")) {
                consciousnessDisorder = true;
                consciousnessDesc = "定向障碍";
            } else if (complaint.contains("嗜睡") || complaint.contains("昏迷") || 
                      complaint.contains("谵妄") || complaint.contains("精神")) {
                consciousnessDisorder = true;
                consciousnessDesc = "意识状态异常";
            }
        }
        if (!consciousnessDisorder && patient.getPresentIllness() != null) {
            String illness = patient.getPresentIllness().toLowerCase();
            if (illness.contains("意识") || illness.contains("定向") || 
                illness.contains("嗜睡") || illness.contains("昏迷")) {
                consciousnessDisorder = true;
                consciousnessDesc = "意识/定向障碍";
            }
        }
        diagnosis.setConsciousnessDisorder(consciousnessDisorder);
        diagnosis.setConsciousnessDescription(consciousnessDesc);
        if (consciousnessDisorder) {
            minorCriteriaCount++;
            diagnosisBasis.add("意识障碍或定向障碍");
            log.debug("患者{}存在意识或定向障碍", patient.getPatientId());
        }

        // 5. 血尿素氮≥7mmol/L
        if (patient.getBloodUreaNitrogen() != null) {
            BigDecimal ureaNitrogen = patient.getBloodUreaNitrogen();
            diagnosis.setUreaNitrogen(ureaNitrogen);
            diagnosis.setUreaNitrogenHigh(ureaNitrogen.compareTo(new BigDecimal("7")) >= 0);
            if (diagnosis.getUreaNitrogenHigh()) {
                minorCriteriaCount++;
                diagnosisBasis.add("血尿素氮≥7mmol/L");
                log.debug("患者{}血尿素氮{}mmol/L，≥7mmol/L", patient.getPatientId(), ureaNitrogen);
            }
        } else {
            diagnosis.setUreaNitrogenHigh(false);
        }

        // 6. 低血压需要液体复苏（从血压值和体检信息判断）
        boolean hypotension = false;
        String bloodPressure = "";
        if (latestExam != null && latestExam.getSystolicBp() != null && latestExam.getDiastolicBp() != null) {
            Integer systolic = latestExam.getSystolicBp();
            Integer diastolic = latestExam.getDiastolicBp();
            bloodPressure = systolic + "/" + diastolic;
            diagnosis.setBloodPressure(bloodPressure);
            // 收缩压<90mmHg或舒张压<60mmHg视为低血压
            if (systolic < 90 || diastolic < 60) {
                hypotension = true;
                minorCriteriaCount++;
                diagnosisBasis.add("低血压需要液体复苏");
                log.debug("患者{}血压{}，存在低血压", patient.getPatientId(), bloodPressure);
            }
        }
        diagnosis.setHypotension(hypotension);

        // 设置诊断结果
        diagnosis.setMajorCriteriaCount(majorCriteriaCount);
        diagnosis.setMinorCriteriaCount(minorCriteriaCount);

        // 判断是否为重症肺炎
        boolean isSeverePneumonia = (majorCriteriaCount >= 1) || (minorCriteriaCount >= 3);
        diagnosis.setIsSeverePneumonia(isSeverePneumonia);

        // 设置诊断依据
        if (diagnosisBasis.isEmpty()) {
            diagnosis.setDiagnosisBasis("未满足任何诊断标准");
        } else {
            diagnosis.setDiagnosisBasis(String.join("；", diagnosisBasis));
        }

        // 设置诊断结论
        if (isSeverePneumonia) {
            diagnosis.setDiagnosisConclusion("重症肺炎");
            diagnosis.setRecommendedAction("建议立即收入ICU治疗，给予密切监护，必要时机械通气支持，积极抗感染治疗");
        } else {
            diagnosis.setDiagnosisConclusion("非重症肺炎");
            diagnosis.setRecommendedAction("建议继续观察，密切监测生命体征，及时复查相关指标");
        }

        log.debug("患者{}重症肺炎诊断完成：主要标准{}项，次要标准{}项，诊断结果：{}", 
                patient.getPatientId(), majorCriteriaCount, minorCriteriaCount,
                isSeverePneumonia ? "重症肺炎" : "非重症肺炎");

        return diagnosis;
    }

    /**
     * 分页查询重症肺炎诊断结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<SeverePneumoniaPageVO> querySeverePneumoniaPage(SeverePneumoniaQueryDTO queryDTO) {
        log.info("开始分页查询重症肺炎诊断结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<SeverePneumoniaPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<SeverePneumoniaPageVO> result = diagnosisMapper.selectSeverePneumoniaPage(
            page,
            queryDTO.getIsSeverePneumonia(),
            queryDTO.getMajorCriteriaCount(),
            queryDTO.getMinorCriteriaCount(),
            queryDTO.getGender(),
            queryDTO.getMinAge(),
            queryDTO.getMaxAge()
        );
        
        log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return result;
    }
}
