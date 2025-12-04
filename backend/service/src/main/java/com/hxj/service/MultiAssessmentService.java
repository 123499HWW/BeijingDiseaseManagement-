package com.hxj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.MultiAssessmentQueryDTO;
import com.hxj.common.mapper.MultiAssessmentMapper;
import com.hxj.common.vo.MultiAssessmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 多评估表联合查询服务
 * 
 * @author HXJ
 * @date 2024-12-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiAssessmentService {
    
    private final MultiAssessmentMapper multiAssessmentMapper;
    
    /**
     * 分页查询多评估表联合数据
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<MultiAssessmentVO> queryMultiAssessmentPage(MultiAssessmentQueryDTO queryDTO) {
        log.info("开始多评估表联合查询，查询条件：{}", queryDTO);
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建分页对象
            Page<MultiAssessmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
            // 执行联表查询
            IPage<MultiAssessmentVO> result = multiAssessmentMapper.selectMultiAssessmentPage(
                    page,
                    queryDTO.getPatientNumber(),
                    queryDTO.getPatientName(),
                    queryDTO.getGender(),
                    queryDTO.getMinAge(),
                    queryDTO.getMaxAge(),
                    queryDTO.getHasCurb(),
                    queryDTO.getHasPsi(),
                    queryDTO.getHasCpis(),
                    queryDTO.getHasDiagnosis()
            );
            
            // 后处理：计算风险等级等
            if (result != null && result.getRecords() != null) {
                for (MultiAssessmentVO vo : result.getRecords()) {
                    // 计算社区获得性肺炎风险
                    vo.setCommunityAcquiredPneumoniaRisk(calculateCommunityPneumoniaRisk(vo));
                    
                    // 计算呼吸机相关肺炎风险
                    vo.setVentilatorPneumoniaRisk(calculateVentilatorPneumoniaRisk(vo));
                    
                    // 计算综合风险评级
                    vo.setOverallRiskLevel(calculateOverallRisk(vo));
                }
            }
            
            long endTime = System.currentTimeMillis();
            log.info("多评估表联合查询完成，共{}条记录，耗时：{}ms", 
                    result.getTotal(), endTime - startTime);
            
            return result;
            
        } catch (Exception e) {
            log.error("多评估表联合查询失败", e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据患者ID查询评估详情
     * 
     * @param patientId 患者ID
     * @return 评估详情
     */
    public MultiAssessmentVO getAssessmentDetail(Long patientId) {
        log.info("查询患者{}的评估详情", patientId);
        
        try {
            MultiAssessmentVO vo = multiAssessmentMapper.selectByPatientId(patientId);
            
            if (vo != null) {
                // 计算风险等级
                vo.setCommunityAcquiredPneumoniaRisk(calculateCommunityPneumoniaRisk(vo));
                vo.setVentilatorPneumoniaRisk(calculateVentilatorPneumoniaRisk(vo));
                vo.setOverallRiskLevel(calculateOverallRisk(vo));
            }
            
            return vo;
            
        } catch (Exception e) {
            log.error("查询患者评估详情失败，patientId: {}", patientId, e);
            throw new RuntimeException("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 计算社区获得性肺炎风险（基于CURB和PSI）
     */
    private String calculateCommunityPneumoniaRisk(MultiAssessmentVO vo) {
        // 优先使用PSI评分
        if (vo.getPsiRiskLevel() != null) {
            String risk = vo.getPsiRiskLevel();
            if (risk.contains("V") || risk.contains("IV")) {
                return "高风险";
            } else if (risk.contains("III") || risk.contains("II")) {
                return "中风险";
            } else {
                return "低风险";
            }
        }
        
        // 其次使用CURB评分
        if (vo.getCurbRiskLevel() != null) {
            String risk = vo.getCurbRiskLevel();
            if (risk.contains("高")) {
                return "高风险";
            } else if (risk.contains("中")) {
                return "中风险";
            } else {
                return "低风险";
            }
        }
        
        // 如果都没有，根据分数判断
        if (vo.getCurbTotalScore() != null) {
            if (vo.getCurbTotalScore() >= 3) {
                return "高风险";
            } else if (vo.getCurbTotalScore() >= 2) {
                return "中风险";
            } else {
                return "低风险";
            }
        }
        
        if (vo.getPsiTotalScore() != null) {
            if (vo.getPsiTotalScore() > 130) {
                return "高风险";
            } else if (vo.getPsiTotalScore() > 90) {
                return "中风险";
            } else {
                return "低风险";
            }
        }
        
        return "未评估";
    }
    
    /**
     * 计算呼吸机相关肺炎风险（基于CPIS）
     */
    private String calculateVentilatorPneumoniaRisk(MultiAssessmentVO vo) {
        if (vo.getCpisRiskLevel() != null) {
            return vo.getCpisRiskLevel();
        }
        
        if (vo.getCpisTotalScore() != null) {
            if (vo.getCpisTotalScore() > 6) {
                return "高风险";
            } else {
                return "低风险";
            }
        }
        
        return "未评估";
    }
    
    /**
     * 计算综合风险评级
     */
    private String calculateOverallRisk(MultiAssessmentVO vo) {
        int highRiskCount = 0;
        int mediumRiskCount = 0;
        int assessmentCount = 0;
        
        // 检查社区获得性肺炎风险
        String capRisk = vo.getCommunityAcquiredPneumoniaRisk();
        if (capRisk != null && !capRisk.equals("未评估")) {
            assessmentCount++;
            if (capRisk.contains("高")) {
                highRiskCount++;
            } else if (capRisk.contains("中")) {
                mediumRiskCount++;
            }
        }
        
        // 检查呼吸机相关肺炎风险
        String vapRisk = vo.getVentilatorPneumoniaRisk();
        if (vapRisk != null && !vapRisk.equals("未评估")) {
            assessmentCount++;
            if (vapRisk.contains("高")) {
                highRiskCount++;
            }
        }
        
        // 检查重症肺炎
        if (vo.getIsSeverePneumonia() != null && vo.getIsSeverePneumonia() == 1) {
            highRiskCount++;
            assessmentCount++;
        }
        
        // 综合判断
        if (assessmentCount == 0) {
            return "未评估";
        }
        
        if (highRiskCount > 0) {
            return "高风险";
        } else if (mediumRiskCount > 0) {
            return "中风险";
        } else {
            return "低风险";
        }
    }
}
