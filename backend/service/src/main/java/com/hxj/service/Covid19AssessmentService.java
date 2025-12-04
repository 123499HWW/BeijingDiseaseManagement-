package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.BatchAssessmentResult;
import com.hxj.common.dto.Covid19AssessmentQueryDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import com.hxj.common.vo.Covid19AssessmentPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * COVID-19重型诊断服务
 */
@Slf4j
@Service
public class Covid19AssessmentService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PhysicalExaminationDetailMapper physicalExaminationDetailMapper;

    @Autowired
    private Covid19AssessmentMapper assessmentMapper;

    @Autowired
    private Covid19PatientRelationMapper relationMapper;

    /**
     * 为单个患者执行评估（标准接口）
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Covid19Assessment performAssessment(Long patientId) {
        return assessSinglePatient(patientId);
    }
    
    /**
     * 批量评估患者（标准接口）
     * @param patientIds 患者ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量执行COVID-19重型评估，患者数量: {}", patientIds.size());
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Long patientId : patientIds) {
            try {
                performAssessment(patientId);
                successCount++;
            } catch (Exception e) {
                log.error("COVID-19重型评估失败，patientId: {}", patientId, e);
                failureCount++;
            }
        }
        
        log.info("COVID-19重型批量评估完成，成功: {}, 失败: {}", successCount, failureCount);
    }
    
    /**
     * 评估单个患者
     * @param patientId 患者ID
     * @return 评估结果
     */
    @Transactional
    public Covid19Assessment assessSinglePatient(Long patientId) {
        log.info("开始对患者{}进行COVID-19重型诊断", patientId);

        // 查询患者信息
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.warn("患者{}不存在", patientId);
            throw new RuntimeException("患者不存在");
        }

        // 执行评估
        Covid19Assessment assessment = performAssessment(patient, "System");

        // 保存评估结果
        assessmentMapper.insert(assessment);

        // 创建并保存关联关系
        Covid19PatientRelation relation = new Covid19PatientRelation();
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

        log.info("患者{}COVID-19重型诊断完成，是否为重型：{}", 
                patientId, assessment.getIsSevereType() ? "是" : "否");

        return assessment;
    }

    /**
     * 批量评估所有患者
     * @return 批量评估结果
     */
    @Transactional
    public BatchAssessmentResult assessAllPatients() {
        log.info("开始批量COVID-19重型诊断");

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
                    log.debug("患者{}已有COVID-19诊断记录，跳过", patient.getPatientId());
                    skipCount++;
                    continue;
                }

                // 执行评估
                Covid19Assessment assessment = performAssessment(patient, "System");

                // 保存评估结果
                assessmentMapper.insert(assessment);

                // 创建并保存关联关系
                Covid19PatientRelation relation = new Covid19PatientRelation();
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
                log.debug("患者{}COVID-19诊断成功", patient.getPatientId());

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

        log.info("批量COVID-19诊断完成，{}", result.getMessage());
        return result;
    }

    /**
     * 执行COVID-19重型诊断
     * 
     * 重型诊断标准（满足以下任何一条）：
     * 1. 呼吸频率≥30次/分
     * 2. 氧饱和度≤93%
     * 3. 氧合指数≤300mmHg
     * 4. 肺部影像学显示24-48小时内病灶明显进展>50%
     */
    private Covid19Assessment performAssessment(Patient patient, String createdBy) {
        Covid19Assessment assessment = new Covid19Assessment();
        
        // 基本信息
        assessment.setAssessmentTime(LocalDateTime.now());
        assessment.setAssessmentMethod("自动评估");
        assessment.setDataSource("患者基本信息和体检详情");
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

        // ==================== 重型诊断标准判断 ====================
        
        // 1. 呼吸频率≥30次/分
        if (latestExam != null && latestExam.getRespiration() != null) {
            Integer respiratoryRate = latestExam.getRespiration();
            assessment.setRespiratoryRate(respiratoryRate);
            assessment.setRespiratoryRateHigh(respiratoryRate >= 30);
            if (assessment.getRespiratoryRateHigh()) {
                criteriaMetCount++;
                diagnosisBasis.add("呼吸频率≥30次/分");
                log.debug("患者{}呼吸频率{}次/分，满足重型标准", patient.getPatientId(), respiratoryRate);
            }
        } else {
            assessment.setRespiratoryRateHigh(false);
            log.debug("患者{}呼吸频率数据不可用", patient.getPatientId());
        }

        // 2. 氧饱和度≤93%（数据不可用，直接设为0）
        assessment.setOxygenSaturationLow(false);
        log.debug("患者{}氧饱和度数据不可用，设为0", patient.getPatientId());

        // 3. 氧合指数≤300mmHg
        if (patient.getArterialOxygenationIndex() != null) {
            BigDecimal oxyIndex = patient.getArterialOxygenationIndex();
            assessment.setOxygenationIndex(oxyIndex);
            assessment.setOxygenationIndexLow(oxyIndex.compareTo(new BigDecimal("300")) <= 0);
            if (assessment.getOxygenationIndexLow()) {
                criteriaMetCount++;
                diagnosisBasis.add("氧合指数≤300mmHg");
                log.debug("患者{}氧合指数{}mmHg，满足重型标准", patient.getPatientId(), oxyIndex);
            }
        } else {
            assessment.setOxygenationIndexLow(false);
            log.debug("患者{}氧合指数数据不可用", patient.getPatientId());
        }

        // 4. 肺部病灶进展>50%（需要影像学对比，暂时通过CT报告文本分析）
        boolean lungProgression = false;
        String lungDescription = "";
        if (latestExam != null && latestExam.getCtImagingFindings() != null) {
            String ctFindings = latestExam.getCtImagingFindings();
            // 查找进展相关关键词
            if (ctFindings.contains("进展") || ctFindings.contains("进行性") || 
                ctFindings.contains("明显加重") || ctFindings.contains("快速发展")) {
                lungProgression = true;
                lungDescription = "影像学提示病灶进展";
                criteriaMetCount++;
                diagnosisBasis.add("肺部病灶明显进展");
                log.debug("患者{}肺部影像学提示病灶进展", patient.getPatientId());
            }
        } else {
            log.debug("患者{}肺部影像学数据不可用", patient.getPatientId());
        }
        assessment.setLungLesionProgression(lungProgression);
        assessment.setLungLesionDescription(lungDescription);

        // 设置诊断结果
        assessment.setCriteriaMetCount(criteriaMetCount);
        assessment.setIsSevereType(criteriaMetCount > 0);

        // 判断严重程度
        if (criteriaMetCount >= 3) {
            assessment.setSeverityLevel("重型（高危）");
            assessment.setAssessmentConclusion(String.format("满足%d项COVID-19重型诊断标准，诊断为重型高危患者", 
                    criteriaMetCount));
            assessment.setRecommendedAction("建议立即收入ICU或重症监护病房，给予高流量吸氧或无创/有创机械通气，" +
                    "密切监测生命体征，考虑使用抗病毒药物、糖皮质激素等综合治疗");
        } else if (criteriaMetCount >= 2) {
            assessment.setSeverityLevel("重型（中危）");
            assessment.setAssessmentConclusion(String.format("满足%d项COVID-19重型诊断标准，诊断为重型中危患者", 
                    criteriaMetCount));
            assessment.setRecommendedAction("建议转入重症监护病房，给予持续氧疗，密切监测氧饱和度和呼吸功能，" +
                    "准备升级呼吸支持措施，及时调整治疗方案");
        } else if (criteriaMetCount == 1) {
            assessment.setSeverityLevel("重型");
            assessment.setAssessmentConclusion("满足1项COVID-19重型诊断标准，诊断为重型");
            assessment.setRecommendedAction("需要密切监测病情变化，给予氧疗支持，监测氧饱和度、呼吸频率等指标，" +
                    "防止病情进展，必要时升级治疗措施");
        } else {
            assessment.setSeverityLevel("非重型");
            assessment.setAssessmentConclusion("未满足COVID-19重型诊断标准，暂不诊断为重型");
            assessment.setRecommendedAction("继续常规治疗和监测，定期复查血氧饱和度、胸部影像学等，" +
                    "观察病情变化，预防向重型发展");
        }

        // 设置诊断依据
        if (!diagnosisBasis.isEmpty()) {
            assessment.setDiagnosisBasis(String.join("；", diagnosisBasis));
        } else {
            assessment.setDiagnosisBasis("未满足任何重型诊断标准");
        }

        log.debug("患者{}COVID-19诊断完成：满足{}项标准，{}型", 
                patient.getPatientId(), criteriaMetCount, 
                assessment.getIsSevereType() ? "重" : "非重");

        return assessment;
    }

    /**
     * 分页查询COVID-19重型诊断结果
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<Covid19AssessmentPageVO> queryCovid19Page(Covid19AssessmentQueryDTO queryDTO) {
        log.info("开始分页查询COVID-19重型诊断结果，查询条件: {}", queryDTO);
        
        // 创建分页对象
        Page<Covid19AssessmentPageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        IPage<Covid19AssessmentPageVO> result = assessmentMapper.selectCovid19Page(
            page,
            queryDTO.getIsSevereType(),
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
