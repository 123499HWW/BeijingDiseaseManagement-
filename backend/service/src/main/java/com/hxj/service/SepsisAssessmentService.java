package com.hxj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.SepsisAssessmentQueryDTO;
import com.hxj.common.mapper.SepsisAssessmentMapper;
import com.hxj.common.vo.SepsisAssessmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 脓毒症评估联合查询服务
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SepsisAssessmentService {
    
    private final SepsisAssessmentMapper sepsisAssessmentMapper;
    
    /**
     * 分页查询脓毒症评估联合数据
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<SepsisAssessmentVO> querySepsisAssessmentPage(SepsisAssessmentQueryDTO queryDTO) {
        log.info("开始脓毒症评估联合查询，查询条件：{}", queryDTO);
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建分页对象
            Page<SepsisAssessmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
            // 处理高风险筛选
            Integer minQsofaScore = queryDTO.getMinQsofaScore();
            Integer minSofaScore = queryDTO.getMinSofaScore();
            
            if (Boolean.TRUE.equals(queryDTO.getIsHighRisk())) {
                // 如果筛选高风险，设置最小分数
                minQsofaScore = minQsofaScore == null ? 2 : Math.max(minQsofaScore, 2);
                minSofaScore = minSofaScore == null ? 2 : Math.max(minSofaScore, 2);
            }
            
            // 执行联表查询
            IPage<SepsisAssessmentVO> result = sepsisAssessmentMapper.selectSepsisAssessmentPage(
                    page,
                    queryDTO.getPatientNumber(),
                    queryDTO.getPatientName(),
                    queryDTO.getGender(),
                    queryDTO.getMinAge(),
                    queryDTO.getMaxAge(),
                    queryDTO.getHasQsofa(),
                    queryDTO.getHasSofa(),
                    minQsofaScore,
                    minSofaScore
            );
            
            // 后处理：计算风险等级和其他指标
            if (result != null && result.getRecords() != null) {
                for (SepsisAssessmentVO vo : result.getRecords()) {
                    // 计算脓毒症筛查结果
                    vo.setSepsisScreeningResult(calculateSepsisScreening(vo));
                    
                    // 评估器官功能障碍
                    vo.setOrganDysfunctionAssessment(assessOrganDysfunction(vo));
                    
                    // 计算脓毒症风险等级
                    vo.setSepsisRiskLevel(calculateSepsisRisk(vo));
                    
                    // 预测死亡风险
                    vo.setMortalityRisk(predictMortalityRisk(vo));
                    
                    // 判断是否需要ICU
                    vo.setRequiresIcu(determineIcuNeed(vo));
                    
                    // 生成治疗建议
                    vo.setRecommendedTreatment(generateTreatmentRecommendation(vo));
                }
            }
            
            long endTime = System.currentTimeMillis();
            log.info("脓毒症评估联合查询完成，共{}条记录，耗时：{}ms", 
                    result.getTotal(), endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            log.error("脓毒症评估联合查询失败", e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据患者ID查询脓毒症评估详情
     * 
     * @param patientId 患者ID
     * @return 评估详情
     */
    public SepsisAssessmentVO getSepsisAssessmentDetail(Long patientId) {
        log.info("查询患者{}的脓毒症评估详情", patientId);
        
        try {
            SepsisAssessmentVO vo = sepsisAssessmentMapper.selectByPatientId(patientId);
            
            if (vo != null) {
                // 计算各项风险指标
                vo.setSepsisScreeningResult(calculateSepsisScreening(vo));
                vo.setOrganDysfunctionAssessment(assessOrganDysfunction(vo));
                vo.setSepsisRiskLevel(calculateSepsisRisk(vo));
                vo.setMortalityRisk(predictMortalityRisk(vo));
                vo.setRequiresIcu(determineIcuNeed(vo));
                vo.setRecommendedTreatment(generateTreatmentRecommendation(vo));
            }
            
            return vo;
            
        } catch (Exception e) {
            log.error("查询患者脓毒症评估详情失败，patientId: {}", patientId, e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询高风险脓毒症患者
     * 
     * @return 高风险患者列表
     */
    public List<SepsisAssessmentVO> getHighRiskPatients() {
        log.info("查询高风险脓毒症患者");
        
        try {
            List<SepsisAssessmentVO> patients = sepsisAssessmentMapper.selectHighRiskPatients();
            
            // 计算风险等级
            for (SepsisAssessmentVO vo : patients) {
                vo.setSepsisScreeningResult(calculateSepsisScreening(vo));
                vo.setSepsisRiskLevel(calculateSepsisRisk(vo));
                vo.setMortalityRisk(predictMortalityRisk(vo));
            }
            
            log.info("找到{}个高风险脓毒症患者", patients.size());
            return patients;
            
        } catch (Exception e) {
            log.error("查询高风险脓毒症患者失败", e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 计算脓毒症筛查结果（基于qSOFA）
     */
    private String calculateSepsisScreening(SepsisAssessmentVO vo) {
        if (vo.getQsofaTotalScore() == null) {
            return "未进行qSOFA评估";
        }
        
        if (vo.getQsofaTotalScore() >= 2) {
            return "阳性（疑似脓毒症）";
        } else {
            return "阴性（低风险）";
        }
    }
    
    /**
     * 评估器官功能障碍（基于SOFA）
     */
    private String assessOrganDysfunction(SepsisAssessmentVO vo) {
        if (vo.getSofaTotalScore() == null) {
            return "未进行SOFA评估";
        }
        
        int score = vo.getSofaTotalScore();
        
        if (score >= 15) {
            return "极重度多器官功能障碍";
        } else if (score >= 10) {
            return "重度多器官功能障碍";
        } else if (score >= 6) {
            return "中度器官功能障碍";
        } else if (score >= 2) {
            return "轻度器官功能障碍";
        } else {
            return "无明显器官功能障碍";
        }
    }
    
    /**
     * 计算脓毒症风险等级（综合评估）
     */
    private String calculateSepsisRisk(SepsisAssessmentVO vo) {
        Integer qsofaScore = vo.getQsofaTotalScore();
        Integer sofaScore = vo.getSofaTotalScore();
        
        // 如果两个评分都没有
        if (qsofaScore == null && sofaScore == null) {
            return "未评估";
        }
        
        // 综合判断风险等级
        boolean highRiskQsofa = qsofaScore != null && qsofaScore >= 2;
        boolean highRiskSofa = sofaScore != null && sofaScore >= 2;
        boolean criticalSofa = sofaScore != null && sofaScore >= 10;
        
        if (criticalSofa || (highRiskQsofa && sofaScore != null && sofaScore >= 6)) {
            return "极高风险";
        } else if (highRiskQsofa || highRiskSofa) {
            return "高风险";
        } else if ((qsofaScore != null && qsofaScore == 1) || 
                   (sofaScore != null && sofaScore == 1)) {
            return "中风险";
        } else {
            return "低风险";
        }
    }
    
    /**
     * 预测死亡风险（基于SOFA评分）
     */
    private String predictMortalityRisk(SepsisAssessmentVO vo) {
        if (vo.getSofaTotalScore() == null) {
            return "无法评估";
        }
        
        int score = vo.getSofaTotalScore();
        
        // 基于文献的死亡率预测
        if (score <= 1) {
            return "低（<10%）";
        } else if (score <= 3) {
            return "轻度升高（10-20%）";
        } else if (score <= 5) {
            return "中度（20-30%）";
        } else if (score <= 7) {
            return "高（30-45%）";
        } else if (score <= 9) {
            return "很高（45-60%）";
        } else if (score <= 12) {
            return "极高（60-80%）";
        } else {
            return "极高（>80%）";
        }
    }
    
    /**
     * 判断是否需要ICU
     */
    private Boolean determineIcuNeed(SepsisAssessmentVO vo) {
        // 已经在ICU
        if (vo.getIcuAdmission() != null && vo.getIcuAdmission() == 1) {
            return true;
        }
        
        // SOFA评分高
        if (vo.getSofaTotalScore() != null && vo.getSofaTotalScore() >= 6) {
            return true;
        }
        
        // qSOFA评分高且有其他危险因素
        if (vo.getQsofaTotalScore() != null && vo.getQsofaTotalScore() >= 2) {
            // 检查是否使用血管活性药物或机械通气
            if ((vo.getVasoactiveDrugsUsed() != null && vo.getVasoactiveDrugsUsed() == 1) ||
                (vo.getVentilatorUsed() != null && vo.getVentilatorUsed() == 1)) {
                return true;
            }
        }
        
        // 有严重的器官功能障碍指标
        if (vo.getSofaCardiovascularScore() != null && vo.getSofaCardiovascularScore() >= 3) {
            return true;
        }
        if (vo.getSofaRespirationScore() != null && vo.getSofaRespirationScore() >= 3) {
            return true;
        }
        if (vo.getSofaCnsScore() != null && vo.getSofaCnsScore() >= 3) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 生成治疗建议
     */
    private String generateTreatmentRecommendation(SepsisAssessmentVO vo) {
        String risk = vo.getSepsisRiskLevel();
        
        if ("极高风险".equals(risk)) {
            return "立即ICU监护，1小时内完成脓毒症集束化治疗，包括：液体复苏、血培养、广谱抗生素、血管活性药物支持";
        } else if ("高风险".equals(risk)) {
            return "密切监测，3小时内完成初始复苏，包括：液体复苏、血培养、乳酸测定、早期抗生素治疗";
        } else if ("中风险".equals(risk)) {
            return "加强监测，完善感染指标检查，6小时内启动抗感染治疗";
        } else if ("低风险".equals(risk)) {
            return "常规监测生命体征，必要时复查qSOFA评分";
        } else {
            return "建议完成qSOFA和SOFA评估";
        }
    }
}
