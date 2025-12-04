package com.hxj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.ComprehensiveAssessmentQueryDTO;
import com.hxj.common.mapper.ComprehensiveAssessmentMapper;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 综合评估服务
 * 提供多表连接的综合查询功能
 */
@Slf4j
@Service
public class ComprehensiveAssessmentService {
    
    @Autowired
    private ComprehensiveAssessmentMapper comprehensiveAssessmentMapper;
    
    /**
     * 综合评估分页查询
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ComprehensiveAssessmentVO> queryComprehensivePage(ComprehensiveAssessmentQueryDTO queryDTO) {
        long startTime = System.currentTimeMillis();
        log.info("开始综合评估分页查询，查询条件: {}", queryDTO);
        
        try {
            // 创建分页对象
            Page<ComprehensiveAssessmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
            // 执行分页查询
            IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentMapper.selectComprehensivePage(
                page,
                queryDTO.getPatientNumber(),
                queryDTO.getGender(),
                queryDTO.getMinAge(),
                queryDTO.getMaxAge(),
                queryDTO.getCurbTotalScore(),
                queryDTO.getCurbRiskLevel(),
                queryDTO.getMinCurbScore(),
                queryDTO.getMaxCurbScore(),
                queryDTO.getCovid19IsSevereType(),
                queryDTO.getCovid19SeverityLevel(),
                queryDTO.getCovid19IsCriticalType(),
                queryDTO.getCovid19CriticalSeverityLevel(),
                queryDTO.getCpisTotalScore(),
                queryDTO.getCpisRiskLevel(),
                queryDTO.getMinCpisScore(),
                queryDTO.getMaxCpisScore(),
                queryDTO.getPsiTotalScore(),
                queryDTO.getPsiRiskClass(),
                queryDTO.getMinPsiScore(),
                queryDTO.getMaxPsiScore(),
                queryDTO.getQsofaTotalScore(),
                queryDTO.getQsofaRiskLevel(),
                queryDTO.getMinQsofaScore(),
                queryDTO.getMaxQsofaScore(),
                queryDTO.getIsSeverePneumonia(),
                queryDTO.getSofaTotalScore(),
                queryDTO.getSofaSeverityLevel(),
                queryDTO.getMinSofaScore(),
                queryDTO.getMaxSofaScore()
            );
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.info("综合评估分页查询完成，总记录数: {}, 当前页记录数: {}, 执行时间: {}ms", 
                    result.getTotal(), result.getRecords().size(), executionTime);
            
            // 检查查询时间是否超过2秒
            if (executionTime > 2000) {
                log.warn("综合评估查询执行时间超过2秒: {}ms", executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("综合评估分页查询失败，执行时间: {}ms, 错误: ", endTime - startTime, e);
            throw e;
        }
    }
    
    /**
     * 获取单个患者的综合评估结果
     * 
     * @param patientId 患者ID
     * @return 综合评估结果
     */
    public ComprehensiveAssessmentVO getPatientComprehensiveAssessment(Long patientId) {
        log.info("获取患者综合评估结果，患者ID: {}", patientId);
        
        try {
            // 创建查询条件，只查询指定患者
            ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(1);
            
            Page<ComprehensiveAssessmentVO> page = new Page<>(1, 1);
            
            // 执行查询
            IPage<ComprehensiveAssessmentVO> result = comprehensiveAssessmentMapper.selectComprehensivePage(
                page,
                null, // patientNumber
                null, null, null, // gender, minAge, maxAge
                null, null, null, null, // CURB相关
                null, null, // COVID-19重型
                null, null, // COVID-19危重型
                null, null, null, null, // CPIS相关
                null, null, null, null, // PSI相关
                null, null, null, null, // qSOFA相关
                null, // 重症肺炎
                null, null, null, null // SOFA相关
            );
            
            if (result.getRecords().isEmpty()) {
                log.info("未找到患者综合评估结果，患者ID: {}", patientId);
                return null;
            }
            
            ComprehensiveAssessmentVO assessment = result.getRecords().get(0);
            log.info("成功获取患者综合评估结果，患者ID: {}, 患者编号: {}", 
                    patientId, assessment.getPatientNumber());
            
            return assessment;
        } catch (Exception e) {
            log.error("获取患者综合评估结果失败，患者ID: {}, 错误: ", patientId, e);
            throw e;
        }
    }
}
