package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.PsiAssessmentQueryDTO;
import com.hxj.common.entity.Patient;
import com.hxj.common.entity.PsiAssessmentResult;
import com.hxj.common.entity.PsiPatientRelation;
import com.hxj.common.entity.PhysicalExaminationDetail;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.mapper.PsiAssessmentResultMapper;
import com.hxj.common.mapper.PsiPatientRelationMapper;
import com.hxj.common.mapper.PhysicalExaminationDetailMapper;
import com.hxj.common.vo.PsiAssessmentPageVO;
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
 * PSI评分服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PsiAssessmentService {

    private final PsiAssessmentResultMapper psiAssessmentResultMapper;
    private final PsiPatientRelationMapper psiPatientRelationMapper;
    private final PatientMapper patientMapper;
    private final PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PsiAssessmentResult performAssessment(Long patientId) {
        return assessSinglePatient(patientId, "system");
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行PSI评估，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("PSI评估失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("PSI批量评估完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 为单个患者执行PSI评分
     */
    @Transactional
    public PsiAssessmentResult assessSinglePatient(Long patientId, String createdBy) {
        log.info("开始为患者{}执行PSI评分", patientId);

        // 获取患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new RuntimeException("患者不存在: " + patientId);
        }

        // 检查是否已经有评分结果
        List<Long> existingPsiIds = psiPatientRelationMapper.selectPsiIdsByPatientId(patientId);
        if (!existingPsiIds.isEmpty()) {
            log.info("患者{}已有PSI评分结果，返回最新结果", patientId);
            return psiAssessmentResultMapper.selectByPsiId(existingPsiIds.get(0));
        }

        // 执行PSI评分计算
        PsiAssessmentResult result = calculatePsiScore(patient, createdBy);

        // 保存评分结果
        psiAssessmentResultMapper.insert(result);

        // 创建患者关联关系
        PsiPatientRelation relation = new PsiPatientRelation();
        relation.setPsiId(result.getPsiId());
        relation.setPatientId(patientId);
        relation.setPatientNumber(patient.getPatientNumber());
        relation.setRelationType("ASSESSMENT");
        relation.setRelationStatus("ACTIVE");
        relation.setCreatedBy(createdBy);
        relation.setUpdatedBy(createdBy);
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        relation.setIsDeleted(0); // 设置默认值
        psiPatientRelationMapper.insert(relation);

        log.info("患者{}的PSI评分完成，PSI ID: {}, 总分: {}, 风险等级: {}", 
                patientId, result.getPsiId(), result.getTotalScore(), result.getRiskClass());

        return result;
    }

    /**
     * 为所有患者执行PSI评分
     */
    @Transactional
    public AssessmentResult assessAllPatients(String createdBy) {
        log.info("开始为所有患者执行PSI评分，操作人: {}", createdBy);

        AssessmentResult assessmentResult = new AssessmentResult();
        List<PsiAssessmentResult> results = new ArrayList<>();
        List<PsiPatientRelation> relations = new ArrayList<>();

        // 查询所有患者
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getIsDeleted, 0);
        List<Patient> patients = patientMapper.selectList(queryWrapper);

        assessmentResult.setTotalCount(patients.size());

        for (Patient patient : patients) {
            try {
                // 检查是否已经有评分结果
                List<Long> existingPsiIds = psiPatientRelationMapper.selectPsiIdsByPatientId(patient.getPatientId());
                if (!existingPsiIds.isEmpty()) {
                    log.debug("患者{}已有PSI评分结果，跳过", patient.getPatientId());
                    assessmentResult.setSkipCount(assessmentResult.getSkipCount() + 1);
                    continue;
                }

                // 执行PSI评分
                PsiAssessmentResult result = calculatePsiScore(patient, createdBy);
                
                // 先插入PSI评分结果以获取自动生成的ID
                psiAssessmentResultMapper.insert(result);
                results.add(result);

                // 创建关联关系（此时result.getPsiId()已经有自动生成的值了）
                PsiPatientRelation relation = new PsiPatientRelation();
                relation.setPsiId(result.getPsiId());
                relation.setPatientId(patient.getPatientId());
                relation.setPatientNumber(patient.getPatientNumber());
                relation.setRelationType("ASSESSMENT");
                relation.setRelationStatus("ACTIVE");
                relation.setCreatedBy(createdBy);
                relation.setUpdatedBy(createdBy);
                relation.setCreatedAt(LocalDateTime.now());
                relation.setUpdatedAt(LocalDateTime.now());
                relation.setIsDeleted(0); // 设置默认值
                relations.add(relation);

                assessmentResult.setSuccessCount(assessmentResult.getSuccessCount() + 1);

            } catch (Exception e) {
                log.error("患者{}的PSI评分失败: {}", patient.getPatientId(), e.getMessage(), e);
                assessmentResult.setFailureCount(assessmentResult.getFailureCount() + 1);
            }
        }

        // 批量保存关联关系（PSI评分结果已经在循环中单独插入了）
        if (!relations.isEmpty()) {
            psiPatientRelationMapper.batchInsert(relations);
        }

        log.info("PSI评分完成，总数: {}, 成功: {}, 失败: {}, 跳过: {}",
                assessmentResult.getTotalCount(), assessmentResult.getSuccessCount(),
                assessmentResult.getFailureCount(), assessmentResult.getSkipCount());

        return assessmentResult;
    }

    /**
     * 计算PSI评分
     */
    private PsiAssessmentResult calculatePsiScore(Patient patient, String createdBy) {
        PsiAssessmentResult result = new PsiAssessmentResult();
        
        // 基本信息（不再设置患者ID和编号，通过关联表管理）
        result.setAssessmentTime(LocalDateTime.now());
        result.setAssessmentMethod("自动评分");
        result.setDataSource("患者基本信息");
        result.setCreatedBy(createdBy);
        result.setUpdatedBy(createdBy);
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());
        result.setIsDeleted(0); // 设置默认值

        int totalScore = 0;

        // 1. 人口统计学因素
        // 性别评分（女性-10分）
        if (patient.getGender() != null && "FEMALE".equals(patient.getGender().name())) {
            result.setGenderScore(-10);
            totalScore -= 10;
        } else {
            result.setGenderScore(0);
        }

        // 年龄评分（男性=年龄，女性=年龄-10）
        if (patient.getAge() != null) {
            int ageScore = patient.getAge();
//            if (patient.getGender() != null && "FEMALE".equals(patient.getGender().name())) {
//                ageScore -= 10;
//            }
            result.setAgeScore(ageScore);
            totalScore += ageScore;
        }

        // 2. 基础疾病评分（这里需要根据实际的患者病史字段来判断）
        // 由于Patient实体中没有具体的疾病字段，这里先设置为0，实际使用时需要根据病史信息判断
        result.setTumorScore(0);
        result.setLiverDiseaseScore(0);
        result.setHeartFailureScore(0);
        result.setCerebrovascularDiseaseScore(0);
        result.setKidneyDiseaseScore(0);

        // 3. 体格检查评分
        // 获取患者体检详细信息
        List<PhysicalExaminationDetail> examinations = physicalExaminationDetailMapper.selectByPatientId(patient.getPatientId());
        log.debug("患者{}的体检详细信息数量: {}", patient.getPatientId(), examinations.size());
        
        // 精神状态改变评分（从现病史中查找"精神差"）
        int mentalStatusScore = 0;
        if (patient.getPresentIllness() != null && patient.getPresentIllness().contains("精神差")) {
            mentalStatusScore = 20;
            totalScore += 20;
            log.debug("患者{}现病史包含'精神差'，精神状态改变评分: +20", patient.getPatientId());
        }
        result.setMentalStatusChangeScore(mentalStatusScore);

        // 从体检详细信息中获取生命体征评分
        int heartRateScore = 0;
        int respiratoryRateScore = 0;
        int systolicBpScore = 0;
        int temperatureScore = 0;

        if (!examinations.isEmpty()) {
            // 取最新的体检记录
            PhysicalExaminationDetail latestExam = examinations.get(0);
            log.debug("患者{}最新体检记录 - 心率: {}, 呼吸: {}, 收缩压: {}, 体温: {}", 
                    patient.getPatientId(), latestExam.getPulse(), latestExam.getRespiration(), 
                    latestExam.getSystolicBp(), latestExam.getTemperature());
            
            // 心率>125次/分（+20分）
            if (latestExam.getPulse() != null && latestExam.getPulse() > 125) {
                heartRateScore = 20;
                totalScore += 20;
                log.debug("患者{}心率{}次/分 > 125，心率评分: +20", patient.getPatientId(), latestExam.getPulse());
            }
            
            // 呼吸频率>30次/分（+20分）
            if (latestExam.getRespiration() != null && latestExam.getRespiration() > 30) {
                respiratoryRateScore = 20;
                totalScore += 20;
                log.debug("患者{}呼吸频率{}次/分 > 30，呼吸频率评分: +20", patient.getPatientId(), latestExam.getRespiration());
            }
            
            // 收缩压<90mmHg（+20分）
            if (latestExam.getSystolicBp() != null && latestExam.getSystolicBp() < 90) {
                systolicBpScore = 20;
                totalScore += 20;
                log.debug("患者{}收缩压{}mmHg < 90，收缩压评分: +20", patient.getPatientId(), latestExam.getSystolicBp());
            }
            
            // 体温<35℃或>40℃（+15分）
            if (latestExam.getTemperature() != null) {
                BigDecimal temp = latestExam.getTemperature();
                if (temp.compareTo(new BigDecimal("35")) < 0 || temp.compareTo(new BigDecimal("40")) > 0) {
                    temperatureScore = 15;
                    totalScore += 15;
                    log.debug("患者{}体温{}℃异常（<35或>40），体温评分: +15", patient.getPatientId(), temp);
                }
            }
        } else {
            log.debug("患者{}没有体检详细信息记录", patient.getPatientId());
        }
        
        result.setHeartRateScore(heartRateScore);
        result.setRespiratoryRateScore(respiratoryRateScore);
        result.setSystolicBpScore(systolicBpScore);
        result.setTemperatureScore(temperatureScore);

        // 4. 实验室检查评分
        // 动脉血pH<7.35（+30分）
        if (patient.getArterialPh() != null && patient.getArterialPh().compareTo(new BigDecimal("7.35")) < 0) {
            result.setArterialPhScore(30);
            totalScore += 30;
        } else {
            result.setArterialPhScore(0);
        }

        // 血尿素氮>30mg/dl（+20分）
        if (patient.getBloodUreaNitrogen() != null && patient.getBloodUreaNitrogen().compareTo(new BigDecimal("30")) > 0) {
            result.setBloodUreaNitrogenScore(20);
            totalScore += 20;
        } else {
            result.setBloodUreaNitrogenScore(0);
        }

        // 血钠<130mmol/L（+20分）
        result.setSerumSodiumScore(0);

        // 血糖>14mmol/L（+10分）
        result.setBloodGlucoseScore(0);

        // 红细胞压积<30%（+10分）
        result.setHematocritScore(0);

        // PaO2<60mmHg（+10分）
        if (patient.getArterialPo2() != null && patient.getArterialPo2().compareTo(new BigDecimal("60")) < 0) {
            result.setPao2Score(10);
            totalScore += 10;
        } else {
            result.setPao2Score(0);
        }

        // 5. 影像学检查评分
        // 胸腔积液评分（从CT影像所见中查找）
        int pleuralEffusionScore = 0;
        if (!examinations.isEmpty()) {
            for (PhysicalExaminationDetail exam : examinations) {
                if (exam.getCtImagingFindings() != null) {
                    String ctFindings = exam.getCtImagingFindings();
                    log.debug("患者{}CT影像所见: {}", patient.getPatientId(), ctFindings);
                    
                    // 查找是否包含"胸腔积液"
                    if (ctFindings.contains("积液")) {
                        // 检查是否有否定词（未见、无、阴性等）
                        // 这些模式表示没有胸腔积液
                        boolean isNegative = ctFindings.contains("未见") && ctFindings.contains("胸腔积液") && 
                                           (ctFindings.indexOf("未见") < ctFindings.indexOf("胸腔积液"));
                        
                        // 也要检查其他常见的否定表述
                        if (!isNegative) {
                            isNegative = (ctFindings.contains("无胸腔积液") || 
                                        ctFindings.contains("胸腔积液阴性") ||
                                        ctFindings.contains("排除胸腔积液"));
                        }
                        
                        if (!isNegative) {
                            // 确认存在胸腔积液，加分
                            pleuralEffusionScore = 10;
                            totalScore += 10;
                            log.debug("患者{}CT影像所见存在胸腔积液，胸腔积液评分: +10", patient.getPatientId());
                            break; // 找到一个符合条件的就退出
                        } else {
                            log.debug("患者{}CT影像所见为阴性胸腔积液表述，胸腔积液评分: 0", patient.getPatientId());
                        }
                    }
                }
            }
        }
        result.setPleuralEffusionScore(pleuralEffusionScore);

        // 设置总分
        result.setTotalScore(totalScore);

        // 根据总分确定风险等级
        setRiskClassification(result, totalScore);

        return result;
    }

    /**
     * 根据PSI评分设置风险等级
     */
    private void setRiskClassification(PsiAssessmentResult result, int totalScore) {
        if (totalScore <= 50) {
            result.setRiskClass("I");
            result.setRiskDescription("极低风险");
            result.setRecommendedTreatment("门诊治疗");
            result.setMortalityRate(new BigDecimal("0.1"));
        } else if (totalScore <= 70) {
            result.setRiskClass("II");
            result.setRiskDescription("低风险");
            result.setRecommendedTreatment("门诊治疗或短期观察");
            result.setMortalityRate(new BigDecimal("0.6"));
        } else if (totalScore <= 90) {
            result.setRiskClass("III");
            result.setRiskDescription("中等风险");
            result.setRecommendedTreatment("短期住院或密切门诊随访");
            result.setMortalityRate(new BigDecimal("2.8"));
        } else if (totalScore <= 130) {
            result.setRiskClass("IV");
            result.setRiskDescription("高风险");
            result.setRecommendedTreatment("住院治疗");
            result.setMortalityRate(new BigDecimal("8.2"));
        } else {
            result.setRiskClass("V");
            result.setRiskDescription("极高风险");
            result.setRecommendedTreatment("住院治疗，考虑ICU");
            result.setMortalityRate(new BigDecimal("29.2"));
        }
    }

    /**
     * 分页查询PSI评分结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<PsiAssessmentPageVO> queryPsiAssessmentPage(PsiAssessmentQueryDTO queryDTO) {
        log.info("开始分页查询PSI评分结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<PsiAssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<PsiAssessmentPageVO> result = psiAssessmentResultMapper.selectPsiAssessmentPage(
            page,
            queryDTO.getTotalScore(),
            queryDTO.getRiskClass(),
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
            return String.format("PSI评分结果: 总数=%d, 成功=%d, 失败=%d, 跳过=%d", 
                    totalCount, successCount, failureCount, skipCount);
        }
    }
}
