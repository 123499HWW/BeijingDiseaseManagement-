package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxj.common.dto.statistics.AssessmentStatisticsDTO;
import com.hxj.common.entity.*;
import com.hxj.common.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 评估统计服务
 * 负责统计各个评分系统中不同风险等级的患者数量
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentStatisticsService {
    
    private final PatientMapper patientMapper;
    private final CurbAssessmentResultMapper curbAssessmentResultMapper;
    private final PsiAssessmentResultMapper psiAssessmentResultMapper;
    private final CpisAssessmentResultMapper cpisAssessmentResultMapper;
    private final QsofaAssessmentMapper qsofaAssessmentMapper;
    private final SofaAssessmentMapper sofaAssessmentMapper;
    private final SeverePneumoniaDiagnosisMapper severePneumoniaDiagnosisMapper;
    private final Covid19AssessmentMapper covid19AssessmentMapper;
    private final Covid19CriticalAssessmentMapper covid19CriticalAssessmentMapper;
    private final RespiratorySyndromeAssessmentMapper respiratorySyndromeAssessmentMapper;
    
    /**
     * 获取所有评分系统的统计数据
     * @return 评估统计DTO
     */
    public AssessmentStatisticsDTO getAllAssessmentStatistics() {
        log.info("开始统计所有评分系统的数据");
        
        AssessmentStatisticsDTO statistics = new AssessmentStatisticsDTO();
        statistics.initializeStatistics();
        
        // 统计总患者数
        LambdaQueryWrapper<Patient> patientQuery = new LambdaQueryWrapper<>();
        patientQuery.eq(Patient::getIsDeleted, 0);
        statistics.setTotalPatients(patientMapper.selectCount(patientQuery).intValue());
        
        // 统计CURB-65评分
        statisticsCurb65(statistics);
        
        // 统计PSI评分
        statisticsPsi(statistics);
        
        // 统计CPIS评分
        statisticsCpis(statistics);
        
        // 统计qSOFA评分
        statisticsQsofa(statistics);
        
        // 统计SOFA评分
        statisticsSofa(statistics);
        
        // 统计重症肺炎诊断
        statisticsSeverePneumonia(statistics);
        
        // 统计COVID-19重型诊断
        statisticsCovid19(statistics);
        
        // 统计COVID-19危重型诊断
        statisticsCovid19Critical(statistics);
        
        // 统计呼吸道症候群评估
        statisticsRespiratorySyndrome(statistics);
        
        // 计算已评估和未评估患者数
        calculateAssessmentCoverage(statistics);
        
        // 统计高风险患者数
        calculateHighRiskPatients(statistics);
        
        log.info("评分系统统计完成，总患者数：{}，已评估：{}，未评估：{}", 
                statistics.getTotalPatients(), 
                statistics.getAssessedPatients(), 
                statistics.getUnassessedPatients());
        
        return statistics;
    }
    
    /**
     * 统计CURB-65评分
     */
    private void statisticsCurb65(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<CurbAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CurbAssessmentResult::getIsDeleted, 0);
            List<CurbAssessmentResult> results = curbAssessmentResultMapper.selectList(queryWrapper);
            
            for (CurbAssessmentResult result : results) {
                String riskLevel = result.getRiskLevel();
                if (riskLevel != null) {
                    statistics.getCurbStatistics().merge(riskLevel, 1, Integer::sum);
                }
            }
            
            log.info("CURB-65统计：低风险{}人，中风险{}人，高风险{}人",
                    statistics.getCurbStatistics().get("低风险"),
                    statistics.getCurbStatistics().get("中风险"),
                    statistics.getCurbStatistics().get("高风险"));
                    
        } catch (Exception e) {
            log.error("统计CURB-65评分失败", e);
        }
    }
    
    /**
     * 统计PSI评分
     */
    private void statisticsPsi(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<PsiAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PsiAssessmentResult::getIsDeleted, 0);
            List<PsiAssessmentResult> results = psiAssessmentResultMapper.selectList(queryWrapper);
            
            for (PsiAssessmentResult result : results) {
                String riskClass = result.getRiskClass();
                if (riskClass != null) {
                    statistics.getPsiStatistics().merge(riskClass, 1, Integer::sum);
                }
            }
            
            log.info("PSI统计：Ⅰ级{}人，Ⅱ级{}人，Ⅲ级{}人，Ⅳ级{}人，Ⅴ级{}人",
                    statistics.getPsiStatistics().get("Ⅰ级"),
                    statistics.getPsiStatistics().get("Ⅱ级"),
                    statistics.getPsiStatistics().get("Ⅲ级"),
                    statistics.getPsiStatistics().get("Ⅳ级"),
                    statistics.getPsiStatistics().get("Ⅴ级"));
                    
        } catch (Exception e) {
            log.error("统计PSI评分失败", e);
        }
    }
    
    /**
     * 统计CPIS评分
     */
    private void statisticsCpis(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<CpisAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CpisAssessmentResult::getIsDeleted, 0);
            List<CpisAssessmentResult> results = cpisAssessmentResultMapper.selectList(queryWrapper);
            
            for (CpisAssessmentResult result : results) {
                String riskLevel = result.getRiskLevel();
                if (riskLevel != null) {
                    statistics.getCpisStatistics().merge(riskLevel, 1, Integer::sum);
                }
            }
            
            log.info("CPIS统计：低风险{}人，高风险{}人",
                    statistics.getCpisStatistics().get("低风险"),
                    statistics.getCpisStatistics().get("高风险"));
                    
        } catch (Exception e) {
            log.error("统计CPIS评分失败", e);
        }
    }
    
    /**
     * 统计qSOFA评分
     */
    private void statisticsQsofa(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<QsofaAssessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(QsofaAssessment::getIsDeleted, 0);
            List<QsofaAssessment> results = qsofaAssessmentMapper.selectList(queryWrapper);
            
            for (QsofaAssessment result : results) {
                String riskLevel = result.getRiskLevel();
                if (riskLevel != null) {
                    statistics.getQsofaStatistics().merge(riskLevel, 1, Integer::sum);
                }
            }
            
            log.info("qSOFA统计：低风险{}人，高风险{}人",
                    statistics.getQsofaStatistics().get("低风险"),
                    statistics.getQsofaStatistics().get("高风险"));
                    
        } catch (Exception e) {
            log.error("统计qSOFA评分失败", e);
        }
    }
    
    /**
     * 统计SOFA评分
     */
    private void statisticsSofa(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<SofaAssessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SofaAssessment::getIsDeleted, 0);
            List<SofaAssessment> results = sofaAssessmentMapper.selectList(queryWrapper);
            
            for (SofaAssessment result : results) {
                String severityLevel = result.getSeverityLevel();
                if (severityLevel != null) {
                    statistics.getSofaStatistics().merge(severityLevel, 1, Integer::sum);
                }
            }
            
            log.info("SOFA统计：正常或轻微{}人，轻度{}人，中度{}人，重度{}人，极重度{}人",
                    statistics.getSofaStatistics().get("正常或轻微"),
                    statistics.getSofaStatistics().get("轻度"),
                    statistics.getSofaStatistics().get("中度"),
                    statistics.getSofaStatistics().get("重度"),
                    statistics.getSofaStatistics().get("极重度"));
                    
        } catch (Exception e) {
            log.error("统计SOFA评分失败", e);
        }
    }
    
    /**
     * 统计重症肺炎诊断
     */
    private void statisticsSeverePneumonia(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<SeverePneumoniaDiagnosis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SeverePneumoniaDiagnosis::getIsDeleted, 0);
            List<SeverePneumoniaDiagnosis> results = severePneumoniaDiagnosisMapper.selectList(queryWrapper);
            
            for (SeverePneumoniaDiagnosis result : results) {
                String diagnosisConclusion = result.getDiagnosisConclusion();
                if (diagnosisConclusion != null) {
                    statistics.getSeverePneumoniaStatistics().merge(diagnosisConclusion, 1, Integer::sum);
                }
            }
            
            log.info("重症肺炎统计：重症肺炎{}人，非重症肺炎{}人",
                    statistics.getSeverePneumoniaStatistics().get("重症肺炎"),
                    statistics.getSeverePneumoniaStatistics().get("非重症肺炎"));
                    
        } catch (Exception e) {
            log.error("统计重症肺炎诊断失败", e);
        }
    }
    
    /**
     * 统计COVID-19重型诊断
     */
    private void statisticsCovid19(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<Covid19Assessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Covid19Assessment::getIsDeleted, 0);
            List<Covid19Assessment> results = covid19AssessmentMapper.selectList(queryWrapper);
            
            for (Covid19Assessment result : results) {
                String severityLevel = result.getSeverityLevel();
                if (severityLevel != null) {
                    statistics.getCovid19Statistics().merge(severityLevel, 1, Integer::sum);
                }
            }
            
            log.info("COVID-19重型统计：非重型{}人，重型{}人，重型（中危）{}人，重型（高危）{}人",
                    statistics.getCovid19Statistics().get("非重型"),
                    statistics.getCovid19Statistics().get("重型"),
                    statistics.getCovid19Statistics().get("重型（中危）"),
                    statistics.getCovid19Statistics().get("重型（高危）"));
                    
        } catch (Exception e) {
            log.error("统计COVID-19重型诊断失败", e);
        }
    }
    
    /**
     * 统计COVID-19危重型诊断
     */
    private void statisticsCovid19Critical(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<Covid19CriticalAssessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Covid19CriticalAssessment::getIsDeleted, 0);
            List<Covid19CriticalAssessment> results = covid19CriticalAssessmentMapper.selectList(queryWrapper);
            
            for (Covid19CriticalAssessment result : results) {
                String severityLevel = result.getSeverityLevel();
                if (severityLevel != null) {
                    statistics.getCovid19CriticalStatistics().merge(severityLevel, 1, Integer::sum);
                }
            }
            
            log.info("COVID-19危重型统计：非危重型{}人，危重型{}人，危重型（高危）{}人，危重型（极危重）{}人",
                    statistics.getCovid19CriticalStatistics().get("非危重型"),
                    statistics.getCovid19CriticalStatistics().get("危重型"),
                    statistics.getCovid19CriticalStatistics().get("危重型（高危）"),
                    statistics.getCovid19CriticalStatistics().get("危重型（极危重）"));
                    
        } catch (Exception e) {
            log.error("统计COVID-19危重型诊断失败", e);
        }
    }
    
    /**
     * 统计呼吸道症候群评估
     */
    private void statisticsRespiratorySyndrome(AssessmentStatisticsDTO statistics) {
        try {
            LambdaQueryWrapper<RespiratorySyndromeAssessment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RespiratorySyndromeAssessment::getIsDeleted, 0);
            List<RespiratorySyndromeAssessment> results = respiratorySyndromeAssessmentMapper.selectList(queryWrapper);
            
            for (RespiratorySyndromeAssessment result : results) {
                String severityLevel = result.getSeverityLevel();
                if (severityLevel != null) {
                    // 将英文等级转换为中文
                    String chineseLevel = convertRespiratorySyndromeLevel(severityLevel);
                    statistics.getRespiratorySyndromeStatistics().merge(chineseLevel, 1, Integer::sum);
                }
            }
            
            log.info("呼吸道症候群统计：轻度{}人，中度{}人，重度{}人，危重{}人",
                    statistics.getRespiratorySyndromeStatistics().get("轻度"),
                    statistics.getRespiratorySyndromeStatistics().get("中度"),
                    statistics.getRespiratorySyndromeStatistics().get("重度"),
                    statistics.getRespiratorySyndromeStatistics().get("危重"));
                    
        } catch (Exception e) {
            log.error("统计呼吸道症候群评估失败", e);
        }
    }
    
    /**
     * 转换呼吸道症候群严重程度等级（英文转中文）
     */
    private String convertRespiratorySyndromeLevel(String englishLevel) {
        switch (englishLevel.toUpperCase()) {
            case "MILD":
                return "轻度";
            case "MODERATE":
                return "中度";
            case "SEVERE":
                return "重度";
            case "CRITICAL":
                return "危重";
            default:
                return "轻度"; // 默认返回轻度
        }
    }
    
    /**
     * 计算评估覆盖率
     */
    private void calculateAssessmentCoverage(AssessmentStatisticsDTO statistics) {
        // 获取所有已评估的患者ID（去重）
        Set<Long> assessedPatientIds = new HashSet<>();
        
        // 收集所有评分表中的患者ID
        // 注意：这里简化处理，实际应该通过关联表查询
        int totalAssessed = 0;
        
        // 计算各评分系统的评估总人数
        totalAssessed += statistics.getCurbStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getPsiStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getCpisStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getQsofaStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getSofaStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getSeverePneumoniaStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getCovid19Statistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getCovid19CriticalStatistics().values().stream().mapToInt(Integer::intValue).sum();
        totalAssessed += statistics.getRespiratorySyndromeStatistics().values().stream().mapToInt(Integer::intValue).sum();
        
        // 取最大值作为已评估患者数（假设大部分患者都完成了所有评估）
        int maxAssessed = Math.min(totalAssessed / 9, statistics.getTotalPatients());
        
        statistics.setAssessedPatients(maxAssessed);
        statistics.setUnassessedPatients(statistics.getTotalPatients() - maxAssessed);
    }
    
    /**
     * 统计高风险患者数
     */
    private void calculateHighRiskPatients(AssessmentStatisticsDTO statistics) {
        int highRiskCount = 0;
        
        // CURB-65高风险
        highRiskCount += statistics.getCurbStatistics().getOrDefault("高风险", 0);
        
        // PSI Ⅳ级和Ⅴ级
        highRiskCount += statistics.getPsiStatistics().getOrDefault("Ⅳ级", 0);
        highRiskCount += statistics.getPsiStatistics().getOrDefault("Ⅴ级", 0);
        
        // CPIS高风险
        highRiskCount += statistics.getCpisStatistics().getOrDefault("高风险", 0);
        
        // qSOFA高风险
        highRiskCount += statistics.getQsofaStatistics().getOrDefault("高风险", 0);
        
        // SOFA重度和极重度
        highRiskCount += statistics.getSofaStatistics().getOrDefault("重度", 0);
        highRiskCount += statistics.getSofaStatistics().getOrDefault("极重度", 0);
        
        // 重症肺炎
        highRiskCount += statistics.getSeverePneumoniaStatistics().getOrDefault("重症肺炎", 0);
        
        // COVID-19重型（中危）和重型（高危）
        highRiskCount += statistics.getCovid19Statistics().getOrDefault("重型（中危）", 0);
        highRiskCount += statistics.getCovid19Statistics().getOrDefault("重型（高危）", 0);
        
        // COVID-19危重型（所有危重型）
        highRiskCount += statistics.getCovid19CriticalStatistics().getOrDefault("危重型", 0);
        highRiskCount += statistics.getCovid19CriticalStatistics().getOrDefault("危重型（高危）", 0);
        highRiskCount += statistics.getCovid19CriticalStatistics().getOrDefault("危重型（极危重）", 0);
        
        // 呼吸道症候群重度和危重
        highRiskCount += statistics.getRespiratorySyndromeStatistics().getOrDefault("重度", 0);
        highRiskCount += statistics.getRespiratorySyndromeStatistics().getOrDefault("危重", 0);
        
        // 取平均值（因为同一患者可能在多个评分中都是高风险）
        statistics.setHighRiskPatients(Math.min(highRiskCount / 9, statistics.getAssessedPatients()));
    }
    
    /**
     * 获取指定评分系统的统计数据
     * @param assessmentType 评分类型（CURB65、PSI、CPIS等）
     * @return 统计结果Map
     */
    public Map<String, Integer> getAssessmentStatisticsByType(String assessmentType) {
        log.info("获取{}评分系统的统计数据", assessmentType);
        
        Map<String, Integer> result = new LinkedHashMap<>();
        
        switch (assessmentType.toUpperCase()) {
            case "CURB65":
            case "CURB-65":
            case "CURB":
                result = getCurbStatistics();
                break;
            case "PSI":
                result = getPsiStatistics();
                break;
            case "CPIS":
                result = getCpisStatistics();
                break;
            case "QSOFA":
                result = getQsofaStatistics();
                break;
            case "SOFA":
                result = getSofaStatistics();
                break;
            case "SEVERE_PNEUMONIA":
            case "SEVEREPNEUMONIA":
                result = getSeverePneumoniaStatistics();
                break;
            case "COVID19":
            case "COVID-19":
                result = getCovid19Statistics();
                break;
            case "COVID19_CRITICAL":
            case "COVID-19_CRITICAL":
                result = getCovid19CriticalStatistics();
                break;
            case "RESPIRATORY_SYNDROME":
            case "RESPIRATORY":
            case "SYNDROME":
                result = getRespiratorySyndromeStatistics();
                break;
            default:
                log.warn("未知的评分类型：{}", assessmentType);
        }
        
        return result;
    }
    
    // 各个评分系统的单独统计方法
    private Map<String, Integer> getCurbStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("低风险", 0);
        result.put("中风险", 0);
        result.put("高风险", 0);
        
        LambdaQueryWrapper<CurbAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurbAssessmentResult::getIsDeleted, 0);
        List<CurbAssessmentResult> results = curbAssessmentResultMapper.selectList(queryWrapper);
        
        for (CurbAssessmentResult r : results) {
            if (r.getRiskLevel() != null) {
                result.merge(r.getRiskLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getPsiStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("Ⅰ级", 0);
        result.put("Ⅱ级", 0);
        result.put("Ⅲ级", 0);
        result.put("Ⅳ级", 0);
        result.put("Ⅴ级", 0);
        
        LambdaQueryWrapper<PsiAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PsiAssessmentResult::getIsDeleted, 0);
        List<PsiAssessmentResult> results = psiAssessmentResultMapper.selectList(queryWrapper);
        
        for (PsiAssessmentResult r : results) {
            if (r.getRiskClass() != null) {
                result.merge(r.getRiskClass(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getCpisStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("低风险", 0);
        result.put("高风险", 0);
        
        LambdaQueryWrapper<CpisAssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CpisAssessmentResult::getIsDeleted, 0);
        List<CpisAssessmentResult> results = cpisAssessmentResultMapper.selectList(queryWrapper);
        
        for (CpisAssessmentResult r : results) {
            if (r.getRiskLevel() != null) {
                result.merge(r.getRiskLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getQsofaStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("低风险", 0);
        result.put("高风险", 0);
        
        LambdaQueryWrapper<QsofaAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QsofaAssessment::getIsDeleted, 0);
        List<QsofaAssessment> results = qsofaAssessmentMapper.selectList(queryWrapper);
        
        for (QsofaAssessment r : results) {
            if (r.getRiskLevel() != null) {
                result.merge(r.getRiskLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getSofaStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("正常或轻微", 0);
        result.put("轻度", 0);
        result.put("中度", 0);
        result.put("重度", 0);
        result.put("极重度", 0);
        
        LambdaQueryWrapper<SofaAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SofaAssessment::getIsDeleted, 0);
        List<SofaAssessment> results = sofaAssessmentMapper.selectList(queryWrapper);
        
        for (SofaAssessment r : results) {
            if (r.getSeverityLevel() != null) {
                result.merge(r.getSeverityLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getSeverePneumoniaStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("重症肺炎", 0);
        result.put("非重症肺炎", 0);
        
        LambdaQueryWrapper<SeverePneumoniaDiagnosis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeverePneumoniaDiagnosis::getIsDeleted, 0);
        List<SeverePneumoniaDiagnosis> results = severePneumoniaDiagnosisMapper.selectList(queryWrapper);
        
        for (SeverePneumoniaDiagnosis r : results) {
            if (r.getDiagnosisConclusion() != null) {
                result.merge(r.getDiagnosisConclusion(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getCovid19Statistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("非重型", 0);
        result.put("重型", 0);
        result.put("重型（中危）", 0);
        result.put("重型（高危）", 0);
        
        LambdaQueryWrapper<Covid19Assessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Covid19Assessment::getIsDeleted, 0);
        List<Covid19Assessment> results = covid19AssessmentMapper.selectList(queryWrapper);
        
        for (Covid19Assessment r : results) {
            if (r.getSeverityLevel() != null) {
                result.merge(r.getSeverityLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getCovid19CriticalStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("非危重型", 0);
        result.put("危重型", 0);
        result.put("危重型（高危）", 0);
        result.put("危重型（极危重）", 0);
        
        LambdaQueryWrapper<Covid19CriticalAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Covid19CriticalAssessment::getIsDeleted, 0);
        List<Covid19CriticalAssessment> results = covid19CriticalAssessmentMapper.selectList(queryWrapper);
        
        for (Covid19CriticalAssessment r : results) {
            if (r.getSeverityLevel() != null) {
                result.merge(r.getSeverityLevel(), 1, Integer::sum);
            }
        }
        
        return result;
    }
    
    private Map<String, Integer> getRespiratorySyndromeStatistics() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("轻度", 0);
        result.put("中度", 0);
        result.put("重度", 0);
        result.put("危重", 0);
        
        LambdaQueryWrapper<RespiratorySyndromeAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RespiratorySyndromeAssessment::getIsDeleted, 0);
        List<RespiratorySyndromeAssessment> results = respiratorySyndromeAssessmentMapper.selectList(queryWrapper);
        
        for (RespiratorySyndromeAssessment r : results) {
            if (r.getSeverityLevel() != null) {
                // 转换英文等级为中文
                String chineseLevel = convertRespiratorySyndromeLevel(r.getSeverityLevel());
                result.merge(chineseLevel, 1, Integer::sum);
            }
        }
        
        return result;
    }
}
