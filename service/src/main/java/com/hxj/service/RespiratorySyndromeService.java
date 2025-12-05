package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.RespiratorySyndromeQueryDTO;
import com.hxj.common.entity.Patient;
import com.hxj.common.entity.PhysicalExaminationDetail;
import com.hxj.common.entity.RespiratorySyndromeAssessment;
import com.hxj.common.entity.SyndromePatientRelation;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.mapper.PhysicalExaminationDetailMapper;
import com.hxj.common.mapper.RespiratorySyndromeAssessmentMapper;
import com.hxj.common.mapper.SyndromePatientRelationMapper;
import com.hxj.common.vo.RespiratorySyndromePageVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 呼吸道症候群评估服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RespiratorySyndromeService {

    private final RespiratorySyndromeAssessmentMapper syndromeAssessmentMapper;
    private final SyndromePatientRelationMapper syndromePatientRelationMapper;
    private final PatientMapper patientMapper;
    private final PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    /**
     * 为单个患者执行呼吸道症候群评估
     */
    @Transactional
    public RespiratorySyndromeAssessment assessSinglePatient(Long patientId, String createdBy) {
        log.info("开始为患者{}执行呼吸道症候群评估", patientId);

        // 获取患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new RuntimeException("患者不存在: " + patientId);
        }

        // 检查是否已经有评估结果
        List<Long> existingSyndromeIds = syndromePatientRelationMapper.selectSyndromeIdsByPatientId(patientId);
        if (!existingSyndromeIds.isEmpty()) {
            log.info("患者{}已有呼吸道症候群评估结果，将重新评估并更新", patientId);
            
            // 删除旧的关联关系（逻辑删除）
            syndromePatientRelationMapper.deleteByPatientId(patientId);
            
            // 删除旧的评估结果（逻辑删除）
            for (Long syndromeId : existingSyndromeIds) {
                RespiratorySyndromeAssessment oldAssessment = syndromeAssessmentMapper.selectBySyndromeId(syndromeId);
                if (oldAssessment != null && oldAssessment.getIsDeleted() != 1) {
                    oldAssessment.setIsDeleted(1);
                    oldAssessment.setUpdatedAt(LocalDateTime.now());
                    oldAssessment.setUpdatedBy(createdBy);
                    syndromeAssessmentMapper.updateById(oldAssessment);
                }
            }
        }

        // 执行新的评估
        RespiratorySyndromeAssessment assessment = performAssessment(patient, createdBy);

        // 保存新的评估结果
        syndromeAssessmentMapper.insert(assessment);

        // 创建新的患者关联关系
        SyndromePatientRelation relation = new SyndromePatientRelation();
        relation.setSyndromeId(assessment.getSyndromeId());
        relation.setPatientId(patientId);
        relation.setPatientNumber(patient.getPatientNumber());
        relation.setDiagnosis(patient.getChiefComplaint() != null ? 
                patient.getChiefComplaint() : "呼吸道症候群评估");
        relation.setSyndromeType("RESPIRATORY"); // 呼吸道症候群
        relation.setRelationStatus("ACTIVE");
        relation.setCreatedBy(createdBy);
        relation.setUpdatedBy(createdBy);
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        relation.setIsDeleted(0);
        syndromePatientRelationMapper.insert(relation);

        log.info("患者{}的呼吸道症候群评估完成，症候群ID: {}, 严重程度: {}", 
                patientId, assessment.getSyndromeId(), assessment.getSeverityLevel());

        return assessment;
    }

    /**
     * 为所有患者执行呼吸道症候群评估
     */
    @Transactional
    public AssessmentResult assessAllPatients(String createdBy) {
        log.info("开始为所有患者执行呼吸道症候群评估，操作人: {}", createdBy);

        AssessmentResult result = new AssessmentResult();
        List<SyndromePatientRelation> relations = new ArrayList<>();

        // 查询所有患者
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getIsDeleted, 0);
        List<Patient> patients = patientMapper.selectList(queryWrapper);

        result.setTotalCount(patients.size());

        for (Patient patient : patients) {
            try {
                boolean isUpdate = false;
                
                // 检查是否已经有评估结果
                List<Long> existingSyndromeIds = syndromePatientRelationMapper.selectSyndromeIdsByPatientId(patient.getPatientId());
                if (!existingSyndromeIds.isEmpty()) {
                    log.debug("患者{}已有呼吸道症候群评估结果，将进行更新", patient.getPatientId());
                    isUpdate = true;
                    
                    // 逻辑删除旧的关联关系
                    syndromePatientRelationMapper.deleteByPatientId(patient.getPatientId());
                    
                    // 逻辑删除旧的评估结果
                    for (Long syndromeId : existingSyndromeIds) {
                        RespiratorySyndromeAssessment oldAssessment = syndromeAssessmentMapper.selectBySyndromeId(syndromeId);
                        if (oldAssessment != null && oldAssessment.getIsDeleted() != 1) {
                            oldAssessment.setIsDeleted(1);
                            oldAssessment.setUpdatedAt(LocalDateTime.now());
                            oldAssessment.setUpdatedBy(createdBy);
                            syndromeAssessmentMapper.updateById(oldAssessment);
                        }
                    }
                }

                // 执行评估
                RespiratorySyndromeAssessment assessment = performAssessment(patient, createdBy);
                
                // 插入新的评估结果
                syndromeAssessmentMapper.insert(assessment);

                // 创建关联关系
                SyndromePatientRelation relation = new SyndromePatientRelation();
                relation.setSyndromeId(assessment.getSyndromeId());
                relation.setPatientId(patient.getPatientId());
                relation.setPatientNumber(patient.getPatientNumber());
                relation.setDiagnosis(patient.getChiefComplaint() != null ? 
                        patient.getChiefComplaint() : "呼吸道症候群评估");
                relation.setSyndromeType("RESPIRATORY");
                relation.setRelationStatus("ACTIVE");
                relation.setCreatedBy(createdBy);
                relation.setUpdatedBy(createdBy);
                relation.setCreatedAt(LocalDateTime.now());
                relation.setUpdatedAt(LocalDateTime.now());
                relation.setIsDeleted(0);
                relations.add(relation);

                // 只有整个操作成功后才更新计数
                result.setSuccessCount(result.getSuccessCount() + 1);
                if (isUpdate) {
                    result.setUpdateCount(result.getUpdateCount() + 1);
                }

            } catch (Exception e) {
                log.error("患者{}的呼吸道症候群评估失败: {}", patient.getPatientId(), e.getMessage(), e);
                result.setFailureCount(result.getFailureCount() + 1);
            }
        }

        // 批量保存关联关系
        if (!relations.isEmpty()) {
            syndromePatientRelationMapper.batchInsert(relations);
        }

        log.info("呼吸道症候群评估完成，总数: {}, 成功: {}（新增: {}, 更新: {}）, 失败: {}",
                result.getTotalCount(), result.getSuccessCount(),
                result.getSuccessCount() - result.getUpdateCount(), result.getUpdateCount(),
                result.getFailureCount());

        return result;
    }

    /**
     * 执行患者呼吸道症候群评估
     */
    private RespiratorySyndromeAssessment performAssessment(Patient patient, String createdBy) {
        RespiratorySyndromeAssessment assessment = new RespiratorySyndromeAssessment();
        
        // 设置基本信息
        assessment.setAssessmentTime(LocalDateTime.now());
        assessment.setAssessmentMethod("AUTO");
        assessment.setAssessor(createdBy);
        assessment.setCreatedBy(createdBy);
        assessment.setUpdatedBy(createdBy);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        assessment.setIsDeleted(0);

        int severityScore = 0;
        int riskFactorsCount = 0;

        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());

        // ==================== 症状评估 ====================
        
        // 呼吸困难评估（从主诉中查找）
        if (patient.getChiefComplaint() != null && patient.getChiefComplaint().contains("呼吸困难")) {
            assessment.setHasDyspnea(1);
            riskFactorsCount++;
            severityScore += 1;
        } else {
            assessment.setHasDyspnea(0);
        }

        // 意识障碍评估（从体检详细信息的general_condition字段中查找）
        boolean hasConsciousnessDisorder = false;
        if (!examinations.isEmpty()) {
            for (PhysicalExaminationDetail exam : examinations) {
                if (exam.getGeneralCondition() != null && exam.getGeneralCondition().contains("意识障碍")) {
                    assessment.setHasConsciousnessDisorder(1);
                    riskFactorsCount++;
                    severityScore += 1;
                    hasConsciousnessDisorder = true;
                    
                }
            }
        }
        
        if (!hasConsciousnessDisorder) {
            assessment.setHasConsciousnessDisorder(0);
        }

        // ==================== 体征评估 ====================
        
        if (!examinations.isEmpty()) {
            PhysicalExaminationDetail latestExam = examinations.get(0);
            
            // 体温评估（从physical_examination_detail表的temperature字段查询）
            if (latestExam.getTemperature() != null) {
                BigDecimal temp = latestExam.getTemperature();
                assessment.setTemperature(temp);
                
                if (temp.compareTo(new BigDecimal("40.0")) > 0) {
                    assessment.setTemperatureLevel("高热");
                    riskFactorsCount++;
                    severityScore += 1;  // 体温>40℃，加1分
                } else if (temp.compareTo(new BigDecimal("38.5")) > 0) {
                    assessment.setTemperatureLevel("发热");
                    riskFactorsCount++;
                    severityScore += 1;  // 体温>38.5℃，加1分
                } else {
                    assessment.setTemperatureLevel("正常");
                }
            }

            // 心率评估
            if (latestExam.getPulse() != null) {
                assessment.setHeartRate(latestExam.getPulse());
                if (latestExam.getPulse() > 130) {
                    assessment.setIsTachycardia(1);
                    riskFactorsCount++;
                    severityScore += 1;
                } else {
                    assessment.setIsTachycardia(0);
                }
            }

            // 血压评估
            if (latestExam.getSystolicBp() != null && latestExam.getDiastolicBp() != null) {
                assessment.setSystolicBp(latestExam.getSystolicBp());
                assessment.setDiastolicBp(latestExam.getDiastolicBp());
                
                if (latestExam.getSystolicBp() < 90 || latestExam.getDiastolicBp() < 60) {
                    assessment.setIsHypotension(1);
                    riskFactorsCount++;
                    severityScore += 1;
                } else {
                    assessment.setIsHypotension(0);
                }
            }

            // 血氧饱和度评估
            if (latestExam.getSpo2() != null) {
                assessment.setOxygenSaturation(latestExam.getSpo2());
                if (latestExam.getSpo2().compareTo(new BigDecimal("93")) < 0) {
                    assessment.setIsHypoxemia(1);
                    riskFactorsCount++;
                    severityScore += 1;  // 血氧饱和度<93%，加1分
                } else {
                    assessment.setIsHypoxemia(0);
                }
            }

            // CT检查评估
            if (latestExam.getCtImagingFindings() != null) {
                String ctFindings = latestExam.getCtImagingFindings();
                assessment.setHasChestCt(1);
                assessment.setCtFindings(ctFindings);
                
                // 检查是否有肺炎等感染征象
                if (ctFindings.contains("肺炎") || ctFindings.contains("感染") || ctFindings.contains("炎症")) {
                    if (!ctFindings.contains("未见") && !ctFindings.contains("无")) {
                        assessment.setCtShowsInfection(1);
                        riskFactorsCount++;
                        severityScore += 1;  // CT显示感染征象，加1分
                    } else {
                        assessment.setCtShowsInfection(0);
                    }
                } else {
                    assessment.setCtShowsInfection(0);
                }

                // 检查是否多肺叶受累
                if (ctFindings.contains("多肺叶") || ctFindings.contains("双肺") || ctFindings.contains("全肺")) {
                    assessment.setCtMultiLobeInvolved(1);
                    riskFactorsCount++;
                    severityScore += 1;  // CT显示多肺叶受累，加1分
                } else {
                    assessment.setCtMultiLobeInvolved(0);
                }
            } else {
                assessment.setHasChestCt(0);
            }
        }

        // ==================== 检验指标评估 ====================
        
        // 动脉血气分析
        if (patient.getArterialPh() != null) {
            assessment.setArterialPh(patient.getArterialPh());
            if (patient.getArterialPh().compareTo(new BigDecimal("7.35")) < 0) {
                assessment.setIsAcidosis(1);
                riskFactorsCount++;
                severityScore += 1;  // pH<7.35，加1分
            } else {
                assessment.setIsAcidosis(0);
            }
        }

        if (patient.getArterialPo2() != null) {
            assessment.setPao2(patient.getArterialPo2());
            if (patient.getArterialPo2().compareTo(new BigDecimal("60")) < 0) {
                assessment.setIsHypoxemiaPao2(1);
                riskFactorsCount++;
                severityScore += 1;  // PaO2<60mmHg，加1分
            } else {
                assessment.setIsHypoxemiaPao2(0);
            }
        }
        
        // 氧合指数（PaO2/FiO2比值）
        if (patient.getArterialOxygenationIndex() != null) {
            assessment.setPao2Fio2Ratio(patient.getArterialOxygenationIndex());
            if (patient.getArterialOxygenationIndex().compareTo(new BigDecimal("300")) <= 0) {
                assessment.setIsOxygenationDisorder(1);
                riskFactorsCount++;
                severityScore += 1;  // 氧合指数≤300mmHg，提示氧合障碍，加1分
            } else {
                assessment.setIsOxygenationDisorder(0);
            }
        }

        // 血尿素氮
        if (patient.getBloodUreaNitrogen() != null) {
            assessment.setBloodUreaNitrogen(patient.getBloodUreaNitrogen());
            if (patient.getBloodUreaNitrogen().compareTo(new BigDecimal("7")) > 0) {
                assessment.setIsBunElevated(1);
                riskFactorsCount++;
                severityScore += 1;  // BUN>7mmol/L，加1分
            } else {
                assessment.setIsBunElevated(0);
            }
        }

        // ==================== 治疗指标评估 ====================
        
        // 血管活性药物评估（多巴胺、多巴酚丁胺、去甲肾上腺素）
        if (patient.getDopamineUsed() != null && patient.getDopamineUsed()) {
            assessment.setUsesDopamine(1);
            riskFactorsCount++;
            severityScore += 1;  // 使用多巴胺，加1分
        } else {
            assessment.setUsesDopamine(0);
        }
        
        if (patient.getDobutamineUsed() != null && patient.getDobutamineUsed()) {
            assessment.setUsesDobutamine(1);
            riskFactorsCount++;
            severityScore += 1;  // 使用多巴酚丁胺，加1分
        } else {
            assessment.setUsesDobutamine(0);
        }
        
        if (patient.getNorepinephrineUsed() != null && patient.getNorepinephrineUsed()) {
            assessment.setUsesNorepinephrine(1);
            riskFactorsCount++;
            severityScore += 1;  // 使用去甲肾上腺素，加1分
        } else {
            assessment.setUsesNorepinephrine(0);
        }
        
        // 特殊级/限制级抗生素评估
        if (patient.getSpecialAntibioticsUsed() != null && patient.getSpecialAntibioticsUsed()) {
            assessment.setUsesSpecialAntibiotics(1);
            if (patient.getAntibioticTypes() != null) {
                assessment.setAntibioticsList(patient.getAntibioticTypes());
            }
            riskFactorsCount++;
            severityScore += 1;  // 使用特殊级抗生素，加1分
        } else {
            assessment.setUsesSpecialAntibiotics(0);
        }
        
        // 呼吸机使用评估
        if (patient.getVentilatorUsed() != null && patient.getVentilatorUsed()) {
            assessment.setUsesVentilator(1);
            riskFactorsCount++;
            severityScore += 1;  // 使用呼吸机，加1分
        } else {
            assessment.setUsesVentilator(0);
        }
        
        // ==================== 治疗场所评估 ====================
        
        // ICU入住评估
        if (patient.getIcuAdmission() != null && patient.getIcuAdmission()) {
            assessment.setInIcu(1);
            riskFactorsCount++;
            severityScore += 1;  // 入住ICU，加1分
        } else {
            assessment.setInIcu(0);
        }
        
        // ==================== 设置评估结果 ====================
        
        assessment.setSeverityScore(severityScore);
        assessment.setRiskFactorsCount(riskFactorsCount);
        
        // 根据评分确定严重程度等级（调整为适应新评分体系）
        if (severityScore >= 8) {
            assessment.setSeverityLevel("危重");
            assessment.setAssessmentSummary("危重症患者，存在多个高危因素，需要立即收入ICU治疗");
        } else if (severityScore >= 6) {
            assessment.setSeverityLevel("重度");
            assessment.setAssessmentSummary("重症患者，需要密切监护和积极治疗");
        } else if (severityScore >= 3) {
            assessment.setSeverityLevel("中度");
            assessment.setAssessmentSummary("中度症状，需要住院治疗和监测");
        } else {
            assessment.setSeverityLevel("轻度");
            assessment.setAssessmentSummary("轻度症状，可考虑门诊治疗或观察");
        }

        return assessment;
    }

    /**
     * 获取患者的呼吸道症候群评估结果
     */
    public RespiratorySyndromeAssessment getPatientAssessment(Long patientId) {
        List<Long> syndromeIds = syndromePatientRelationMapper.selectSyndromeIdsByPatientId(patientId);
        if (syndromeIds.isEmpty()) {
            return null;
        }
        return syndromeAssessmentMapper.selectBySyndromeId(syndromeIds.get(0));
    }
    
    /**
     * 分页查询呼吸道症候群评估结果
     */
    public IPage<RespiratorySyndromePageVO> queryRespiratorySyndromePage(RespiratorySyndromeQueryDTO query) {
        log.info("分页查询呼吸道症候群评估，页码: {}, 页大小: {}", query.getPageNum(), query.getPageSize());
        
        // 参数校验
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getPageSize() > 100) {
            query.setPageSize(100);
        }
        
        // 创建分页对象
        Page<RespiratorySyndromePageVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        // 执行分页查询
        IPage<RespiratorySyndromePageVO> result = syndromeAssessmentMapper.selectRespiratorySyndromePage(page, query);
        
        log.info("呼吸道症候群评估分页查询完成，总记录数: {}, 当前页记录数: {}", 
                result.getTotal(), result.getRecords().size());
        
        return result;
    }

    /**
     * 根据syndrome_id查询评估详情
     */
    public RespiratorySyndromePageVO getSyndromeDetail(Long syndromeId) {
        log.info("查询呼吸道症候群评估详情，syndromeId: {}", syndromeId);
        
        // 直接使用新的Mapper方法查询
        RespiratorySyndromePageVO detail = syndromeAssessmentMapper.selectSyndromeDetailById(syndromeId);
        
        if (detail != null) {
            log.info("找到呼吸道症候群评估详情，患者编号：{}", detail.getPatientNumber());
        } else {
            log.warn("未找到syndrome_id={}的评估详情", syndromeId);
        }
        
        return detail;
    }
    
    /**
     * 查询高风险患者
     */
    public com.hxj.common.result.Result<?> getHighRiskPatients() {
        log.info("查询呼吸道症候群高风险患者");
        
        try {
            // 创建查询条件，查询CRITICAL和SEVERE级别的患者
            RespiratorySyndromeQueryDTO query = new RespiratorySyndromeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10000);  // 获取所有高风险患者
            
            // 查询所有数据
            IPage<RespiratorySyndromePageVO> allData = syndromeAssessmentMapper.selectRespiratorySyndromePage(
                new Page<>(query.getPageNum(), query.getPageSize()), query);
            
            // 过滤出高风险患者
            List<RespiratorySyndromePageVO> highRiskPatients = allData.getRecords().stream()
                    .filter(vo -> "CRITICAL".equals(vo.getSeverityLevel()) || "SEVERE".equals(vo.getSeverityLevel()))
                    .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", highRiskPatients.size());
            result.put("patients", highRiskPatients);
            
            return com.hxj.common.result.Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询高风险患者失败", e);
            return com.hxj.common.result.Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取统计信息
     */
    public com.hxj.common.result.Result<?> getSyndromeStatistics() {
        log.info("获取呼吸道症候群评估统计");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 查询所有患者数据
            RespiratorySyndromeQueryDTO query = new RespiratorySyndromeQueryDTO();
            query.setPageNum(1);
            query.setPageSize(10000);
            
            IPage<RespiratorySyndromePageVO> page = syndromeAssessmentMapper.selectRespiratorySyndromePage(
                new Page<>(query.getPageNum(), query.getPageSize()), query);
            
            List<RespiratorySyndromePageVO> allRecords = page.getRecords();
            
            // 统计指标
            int totalAssessments = allRecords.size();
            Map<String, Integer> severityDistribution = new HashMap<>();
            Map<String, Integer> symptomStatistics = new HashMap<>();
            Map<String, Integer> treatmentStatistics = new HashMap<>();
            
            // 初始化分布
            severityDistribution.put("CRITICAL", 0);
            severityDistribution.put("SEVERE", 0);
            severityDistribution.put("MODERATE", 0);
            severityDistribution.put("MILD", 0);
            
            int hasDyspneaCount = 0;
            int hasConsciousnessDisorderCount = 0;
            int usesVentilatorCount = 0;
            int inIcuCount = 0;
            int ctShowsInfectionCount = 0;
            
            for (RespiratorySyndromePageVO vo : allRecords) {
                // 统计严重程度分布
                String severity = vo.getSeverityLevel();
                if (severity != null) {
                    severityDistribution.put(severity, severityDistribution.getOrDefault(severity, 0) + 1);
                }
                
                // 统计症状
                if (vo.getHasDyspnea() != null && vo.getHasDyspnea() == 1) {
                    hasDyspneaCount++;
                }
                if (vo.getHasConsciousnessDisorder() != null && vo.getHasConsciousnessDisorder() == 1) {
                    hasConsciousnessDisorderCount++;
                }
                
                // 统计治疗
                if (vo.getUsesVentilator() != null && vo.getUsesVentilator() == 1) {
                    usesVentilatorCount++;
                }
                if (vo.getInIcu() != null && vo.getInIcu() == 1) {
                    inIcuCount++;
                }
                
                // 统计CT检查
                if (vo.getCtShowsInfection() != null && vo.getCtShowsInfection() == 1) {
                    ctShowsInfectionCount++;
                }
            }
            
            // 计算百分比
            symptomStatistics.put("呼吸困难", hasDyspneaCount);
            symptomStatistics.put("意识障碍", hasConsciousnessDisorderCount);
            
            treatmentStatistics.put("使用呼吸机", usesVentilatorCount);
            treatmentStatistics.put("入住ICU", inIcuCount);
            treatmentStatistics.put("CT显示感染", ctShowsInfectionCount);
            
            // 组装统计结果
            statistics.put("总评估数", totalAssessments);
            statistics.put("严重程度分布", severityDistribution);
            statistics.put("症状统计", symptomStatistics);
            statistics.put("治疗统计", treatmentStatistics);
            statistics.put("高风险患者数", severityDistribution.get("CRITICAL") + severityDistribution.get("SEVERE"));
            
            // 计算百分比
            if (totalAssessments > 0) {
                Map<String, String> percentages = new HashMap<>();
                percentages.put("呼吸困难率", String.format("%.1f%%", (double) hasDyspneaCount / totalAssessments * 100));
                percentages.put("意识障碍率", String.format("%.1f%%", (double) hasConsciousnessDisorderCount / totalAssessments * 100));
                percentages.put("呼吸机使用率", String.format("%.1f%%", (double) usesVentilatorCount / totalAssessments * 100));
                percentages.put("ICU入住率", String.format("%.1f%%", (double) inIcuCount / totalAssessments * 100));
                statistics.put("百分比统计", percentages);
            }
            
            log.info("呼吸道症候群评估统计完成：{}", statistics);
            return com.hxj.common.result.Result.success("统计成功", statistics);
            
        } catch (Exception e) {
            log.error("获取统计失败", e);
            return com.hxj.common.result.Result.error("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 评估结果统计
     */
    @Data
    public static class AssessmentResult {
        private int totalCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private int updateCount = 0;  // 更新的数量

        @Override
        public String toString() {
            return String.format("呼吸道症候群评估结果: 总数=%d, 成功=%d（新增=%d, 更新=%d）, 失败=%d", 
                    totalCount, successCount, successCount - updateCount, updateCount, failureCount);
        }
    }
}
