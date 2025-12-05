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

import java.util.ArrayList;
import java.util.List;

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
            // 如果指定了风险筛选条件或重症肺炎筛选，需要先查询所有数据，在内存中过滤
            if ((queryDTO.getCommunityAcquiredPneumoniaRisk() != null && 
                !queryDTO.getCommunityAcquiredPneumoniaRisk().trim().isEmpty()) ||
                queryDTO.getIsSeverePneumonia() != null) {
                
                // 查询所有数据（设置大页面）
                Page<MultiAssessmentVO> tempPage = new Page<>(1, 10000);
                IPage<MultiAssessmentVO> tempResult = multiAssessmentMapper.selectMultiAssessmentPage(tempPage);
                
                // 计算风险并过滤
                java.util.List<MultiAssessmentVO> filteredList = new java.util.ArrayList<>();
                if (tempResult != null && tempResult.getRecords() != null) {
                    for (MultiAssessmentVO vo : tempResult.getRecords()) {
                        // 计算社区获得性肺炎风险
                        vo.setCommunityAcquiredPneumoniaRisk(calculateCommunityAcquiredPneumoniaRisk(vo));
                        
                        // 判断是否符合过滤条件
                        boolean shouldInclude = true;
                        
                        // 检查社区获得性肺炎风险条件
                        if (queryDTO.getCommunityAcquiredPneumoniaRisk() != null && 
                            !queryDTO.getCommunityAcquiredPneumoniaRisk().trim().isEmpty()) {
                            if (!queryDTO.getCommunityAcquiredPneumoniaRisk().equals(vo.getCommunityAcquiredPneumoniaRisk())) {
                                shouldInclude = false;
                            }
                        }
                        
                        // 检查是否重症肺炎条件
                        if (queryDTO.getIsSeverePneumonia() != null) {
                            if (!queryDTO.getIsSeverePneumonia().equals(vo.getIsSeverePneumonia())) {
                                shouldInclude = false;
                            }
                        }
                        
                        // 如果符合条件则添加到结果列表
                        if (shouldInclude) {
                            // 计算其他风险等级
                            vo.setVentilatorPneumoniaRisk(calculateVentilatorPneumoniaRisk(vo));
                            vo.setOverallRiskLevel(calculateOverallRisk(vo));
                            filteredList.add(vo);
                        }
                    }
                }
                
                // 手动分页
                int total = filteredList.size();
                int pageNum = queryDTO.getPageNum();
                int pageSize = queryDTO.getPageSize();
                int fromIndex = (pageNum - 1) * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, total);
                
                Page<MultiAssessmentVO> resultPage = new Page<>(pageNum, pageSize);
                resultPage.setTotal(total);
                if (fromIndex < total) {
                    resultPage.setRecords(filteredList.subList(fromIndex, toIndex));
                } else {
                    resultPage.setRecords(new java.util.ArrayList<>());
                }
                
                long endTime = System.currentTimeMillis();
                log.info("多评估表联合查询完成（带过滤），共{}条记录，耗时：{}ms", 
                        resultPage.getTotal(), endTime - startTime);
                
                return resultPage;
                
            } else {
                // 没有风险筛选条件，正常分页查询
                Page<MultiAssessmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
                IPage<MultiAssessmentVO> result = multiAssessmentMapper.selectMultiAssessmentPage(page);
                
                // 后处理：计算风险等级等
                if (result != null && result.getRecords() != null) {
                    for (MultiAssessmentVO vo : result.getRecords()) {
                        // 计算社区获得性肺炎风险
                        vo.setCommunityAcquiredPneumoniaRisk(calculateCommunityAcquiredPneumoniaRisk(vo));
                        
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
            }
            
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
                vo.setCommunityAcquiredPneumoniaRisk(calculateCommunityAcquiredPneumoniaRisk(vo));
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
     * 计算社区获得性肺炎风险
     */
    private String calculateCommunityAcquiredPneumoniaRisk(MultiAssessmentVO vo) {
        // 优先使用PSI评分
        if (vo.getPsiRiskLevel() != null) {
            String risk = vo.getPsiRiskLevel();
            if (risk.contains("V") || risk.contains("IV")) {
                return "高风险";
            } else {
                // I级、II级、III级都归为低风险
                return "低风险";
            }
        }
        
        // 其次使用CURB评分
        if (vo.getCurbRiskLevel() != null) {
            String risk = vo.getCurbRiskLevel();
            if (risk.contains("高")) {
                return "高风险";
            } else {
                // 中风险和低风险都归为低风险
                return "低风险";
            }
        }
        
        // 如果都没有，根据分数判断
        if (vo.getCurbTotalScore() != null) {
            if (vo.getCurbTotalScore() >= 3) {
                return "高风险";
            } else {
                // 分数小于3都归为低风险
                return "低风险";
            }
        }
        
        if (vo.getPsiTotalScore() != null) {
            if (vo.getPsiTotalScore() > 130) {
                return "高风险";
            } else {
                // 分数<=130都归为低风险
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
        int assessmentCount = 0;
        
        // 检查社区获得性肺炎风险
        String capRisk = vo.getCommunityAcquiredPneumoniaRisk();
        if (capRisk != null && !capRisk.equals("未评估")) {
            assessmentCount++;
            if (capRisk.contains("高")) {
                highRiskCount++;
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
        } else {
            return "低风险";
        }
    }
}
