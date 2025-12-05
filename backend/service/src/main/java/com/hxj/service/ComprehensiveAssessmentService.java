package com.hxj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.ComprehensiveAssessmentQueryDTO;
import com.hxj.common.dto.statistics.AgeSegmentStatisticsDTO;
import com.hxj.common.dto.statistics.DiseaseStatisticsDTO;
import com.hxj.common.dto.statistics.GenderStatisticsDTO;
import com.hxj.common.dto.statistics.MonthlyStatisticsDTO;
import com.hxj.common.mapper.ComprehensiveAssessmentMapper;
import com.hxj.common.mapper.ComprehensiveAssessmentOptimizedMapper;
import com.hxj.common.vo.ComprehensiveAssessmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 综合评估服务
 * 提供多表连接的综合查询功能
 */
@Slf4j
@Service
public class ComprehensiveAssessmentService {
    
    @Autowired
    private ComprehensiveAssessmentMapper comprehensiveAssessmentMapper;
    
    @Autowired
    private ComprehensiveAssessmentOptimizedMapper optimizedMapper;
    
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
                queryDTO.getMaxSofaScore(),
                queryDTO.getRespiratorySyndromeSeverityLevel(),
                queryDTO.getMinRespiratorySyndromeScore(),
                queryDTO.getMaxRespiratorySyndromeScore()
            );
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 处理每条记录，计算综合风险等级
            Boolean logicalOperator = queryDTO.getLogicalOperator();
            if (logicalOperator == null) {
                logicalOperator = true; // 默认为AND逻辑
            }
            
            for (ComprehensiveAssessmentVO record : result.getRecords()) {
                // 计算社区获得性肺炎风险
                String capRisk = calculateCommunityAcquiredPneumoniaRisk(record, logicalOperator);
                record.setCommunityAcquiredPneumoniaRisk(capRisk);
                
                // 计算脓毒症风险
                String sepsisRisk = calculateSepsisRisk(record, logicalOperator);
                record.setSepsisRisk(sepsisRisk);
            }
            
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
                null, null, null, null, // SOFA相关
                null, null, null // 呼吸道症候群相关
            );
            
            if (result.getRecords().isEmpty()) {
                log.info("未找到患者综合评估结果，患者ID: {}", patientId);
                return null;
            }
            
            ComprehensiveAssessmentVO assessment = result.getRecords().get(0);
            
            // 计算综合风险等级（默认使用AND逻辑）
            String capRisk = calculateCommunityAcquiredPneumoniaRisk(assessment, true);
            assessment.setCommunityAcquiredPneumoniaRisk(capRisk);
            
            String sepsisRisk = calculateSepsisRisk(assessment, true);
            assessment.setSepsisRisk(sepsisRisk);
            
            log.info("成功获取患者综合评估结果，患者ID: {}, 患者编号: {}", 
                    patientId, assessment.getPatientNumber());
            
            return assessment;
        } catch (Exception e) {
            log.error("获取患者综合评估结果失败，患者ID: {}, 错误: ", patientId, e);
            throw e;
        }
    }
    
    /**
     * 计算社区获得性肺炎风险等级（默认使用OR逻辑）
     * 
     * @param assessment 综合评估结果
     * @return 风险等级："高风险"或"低风险"
     */
    private String calculateCommunityPneumoniaRisk(ComprehensiveAssessmentVO assessment) {
        return calculateCommunityAcquiredPneumoniaRisk(assessment, false);
    }
    
    /**
     * 计算社区获得性肺炎风险等级
     * 
     * @param assessment 综合评估结果
     * @param useAndLogic 是否使用AND逻辑（true: AND, false: OR）
     * @return 风险等级："高风险"或"低风险"
     */
    private String calculateCommunityAcquiredPneumoniaRisk(ComprehensiveAssessmentVO assessment, boolean useAndLogic) {
        // 检查四个条件
        boolean curbHighRisk = "高风险".equals(assessment.getCurbRiskLevel());
        boolean psiHighRisk = "V级".equals(assessment.getPsiRiskClass());
        boolean cpisHighRisk = "高风险".equals(assessment.getCpisRiskLevel());
        boolean severePneumonia = Boolean.TRUE.equals(assessment.getIsSeverePneumonia());
        
        boolean isHighRisk;
        if (useAndLogic) {
            // AND逻辑：需要同时满足所有条件
            isHighRisk = curbHighRisk && psiHighRisk && cpisHighRisk && severePneumonia;
        } else {
            // OR逻辑：满足任一条件即可
            isHighRisk = curbHighRisk || psiHighRisk || cpisHighRisk || severePneumonia;
        }
        
        if (isHighRisk) {
            log.debug("患者{}社区获得性肺炎评估为高风险，CURB:{}, PSI:{}, CPIS:{}, 重症肺炎:{}, 逻辑:{}",
                    assessment.getPatientNumber(), curbHighRisk, psiHighRisk, cpisHighRisk, severePneumonia,
                    useAndLogic ? "AND" : "OR");
        }
        
        return isHighRisk ? "高风险" : "低风险";
    }
    
    /**
     * 计算脓毒症风险等级（默认使用OR逻辑）
     * 
     * @param assessment 综合评估结果
     * @return 风险等级："高风险"或"低风险"
     */
    private String calculateSepsisRisk(ComprehensiveAssessmentVO assessment) {
        return calculateSepsisRisk(assessment, false);
    }
    
    /**
     * 计算脓毒症风险等级
     * 
     * @param assessment 综合评估结果
     * @param useAndLogic 是否使用AND逻辑（true: AND, false: OR）
     * @return 风险等级："高风险"或"低风险"
     */
    private String calculateSepsisRisk(ComprehensiveAssessmentVO assessment, boolean useAndLogic) {
        // 检查两个条件
        boolean qsofaHighRisk = "高风险".equals(assessment.getQsofaRiskLevel());
        // SOFA中度或更高程度都算高风险
        boolean sofaHighRisk = false;
        String sofaLevel = assessment.getSofaSeverityLevel();
        if (sofaLevel != null) {
            sofaHighRisk = "中度".equals(sofaLevel) || "重度".equals(sofaLevel) || "极重度".equals(sofaLevel);
        }
        
        boolean isHighRisk;
        if (useAndLogic) {
            // AND逻辑：需要同时满足所有条件
            isHighRisk = qsofaHighRisk && sofaHighRisk;
        } else {
            // OR逻辑：满足任一条件即可
            isHighRisk = qsofaHighRisk || sofaHighRisk;
        }
        
        if (isHighRisk) {
            log.debug("患者{}脓毒症评估为高风险，qSOFA:{}, SOFA:{}, 逻辑:{}",
                    assessment.getPatientNumber(), qsofaHighRisk, sofaLevel,
                    useAndLogic ? "AND" : "OR");
        }
        
        return isHighRisk ? "高风险" : "低风险";
    }
    
    /**
     * 按年龄分段统计综合评估数据
     * 
     * @return 年龄分段统计结果列表
     */
    public List<AgeSegmentStatisticsDTO> getAgeSegmentStatistics() {
        log.info("开始执行年龄分段统计");
        long startTime = System.currentTimeMillis();
        
        // 定义年龄段
        Map<String, int[]> ageRanges = new LinkedHashMap<>();
        ageRanges.put("0-17", new int[]{0, 17});
        ageRanges.put("18-29", new int[]{18, 29});
        ageRanges.put("30-44", new int[]{30, 44});
        ageRanges.put("45-59", new int[]{45, 59});
        ageRanges.put("60-74", new int[]{60, 74});
        ageRanges.put("75+", new int[]{75, 999});
        
        List<AgeSegmentStatisticsDTO> statisticsList = new ArrayList<>();
        
        for (Map.Entry<String, int[]> entry : ageRanges.entrySet()) {
            String ageRange = entry.getKey();
            int[] range = entry.getValue();
            
            AgeSegmentStatisticsDTO statistics = new AgeSegmentStatisticsDTO();
            statistics.setAgeRange(ageRange);
            statistics.initializeDistributions();
            
            // 创建查询条件
            ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
            queryDTO.setMinAge(range[0]);
            queryDTO.setMaxAge(range[1] == 999 ? null : range[1]);
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10000); // 设置较大的页面大小以获取所有数据
            
            try {
                // 执行查询
                IPage<ComprehensiveAssessmentVO> page = queryComprehensivePage(queryDTO);
                List<ComprehensiveAssessmentVO> records = page.getRecords();
                
                // 统计数据
                for (ComprehensiveAssessmentVO record : records) {
                    statistics.setTotalPatients(statistics.getTotalPatients() + 1);
                    
                    // 统计COVID-19严重程度
                    String covid19Severity = record.getCovid19SeverityLevel();
                    if (covid19Severity != null) {
                        statistics.getCovid19SeverityDistribution().merge(covid19Severity, 1, Integer::sum);
                    } else {
                        statistics.getCovid19SeverityDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计COVID-19危重程度
                    String covid19Critical = record.getCovid19CriticalSeverityLevel();
                    if (covid19Critical != null) {
                        statistics.getCovid19CriticalDistribution().merge(covid19Critical, 1, Integer::sum);
                    } else {
                        statistics.getCovid19CriticalDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计呼吸道症候群严重程度（需要转换英文到中文）
                    String respiratorySyndrome = record.getRespiratorySyndromeSeverityLevel();
                    if (respiratorySyndrome != null) {
                        String chineseLevel = convertRespiratorySyndromeLevel(respiratorySyndrome);
                        statistics.getRespiratorySyndromeDistribution().merge(chineseLevel, 1, Integer::sum);
                    } else {
                        statistics.getRespiratorySyndromeDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计社区获得性肺炎风险
                    String communityPneumoniaRisk = record.getCommunityAcquiredPneumoniaRisk();
                    if (communityPneumoniaRisk != null) {
                        statistics.getCommunityPneumoniaRiskDistribution().merge(communityPneumoniaRisk, 1, Integer::sum);
                    } else {
                        statistics.getCommunityPneumoniaRiskDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计脓毒症风险
                    String sepsisRisk = record.getSepsisRisk();
                    if (sepsisRisk != null) {
                        statistics.getSepsisRiskDistribution().merge(sepsisRisk, 1, Integer::sum);
                    } else {
                        statistics.getSepsisRiskDistribution().merge("无数据", 1, Integer::sum);
                    }
                }
                
                log.info("年龄段{}统计完成，总人数：{}", ageRange, statistics.getTotalPatients());
                
            } catch (Exception e) {
                log.error("年龄段{}统计失败", ageRange, e);
            }
            
            statisticsList.add(statistics);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("年龄分段统计完成，耗时：{}ms", endTime - startTime);
        
        return statisticsList;
    }
    
    /**
     * 转换呼吸道症候群严重程度等级（英文转中文）
     */
    private String convertRespiratorySyndromeLevel(String englishLevel) {
        if (englishLevel == null) {
            return "无数据";
        }
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
                return englishLevel; // 如果不是预期的值，返回原值
        }
    }
    
    /**
     * 转换呼吸道症候群严重程度（别名方法）
     * 
     * @param englishSeverity 英文严重程度
     * @return 中文严重程度
     */
    private String convertRespiratorySyndromeSeverity(String englishSeverity) {
        return convertRespiratorySyndromeLevel(englishSeverity);
    }
    
    /**
     * 按性别统计综合评估数据
     * 
     * @return 性别分类统计结果列表
     */
    public List<GenderStatisticsDTO> getGenderStatistics() {
        log.info("开始执行性别分类统计");
        long startTime = System.currentTimeMillis();
        
        List<GenderStatisticsDTO> statisticsList = new ArrayList<>();
        
        // 统计男性和女性
        String[] genders = {"男", "女"};
        
        for (String gender : genders) {
            GenderStatisticsDTO statistics = new GenderStatisticsDTO();
            statistics.setGender(gender);
            statistics.initializeDistributions();
            
            // 创建查询条件
            ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
            queryDTO.setGender(gender);
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10000); // 设置较大的页面大小以获取所有数据
            
            try {
                // 执行查询
                IPage<ComprehensiveAssessmentVO> page = queryComprehensivePage(queryDTO);
                List<ComprehensiveAssessmentVO> records = page.getRecords();
                
                int totalAge = 0;
                
                // 统计数据
                for (ComprehensiveAssessmentVO record : records) {
                    statistics.setTotalPatients(statistics.getTotalPatients() + 1);
                    
                    // 统计年龄信息
                    if (record.getAge() != null) {
                        totalAge += record.getAge();
                        if (record.getAge() < statistics.getMinAge()) {
                            statistics.setMinAge(record.getAge());
                        }
                        if (record.getAge() > statistics.getMaxAge()) {
                            statistics.setMaxAge(record.getAge());
                        }
                        
                        // 统计年龄段分布
                        String ageGroup = getAgeGroup(record.getAge());
                        statistics.getAgeGroupDistribution().merge(ageGroup, 1, Integer::sum);
                    }
                    
                    // 统计COVID-19严重程度
                    String covid19Severity = record.getCovid19SeverityLevel();
                    if (covid19Severity != null) {
                        statistics.getCovid19SeverityDistribution().merge(covid19Severity, 1, Integer::sum);
                    } else {
                        statistics.getCovid19SeverityDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计COVID-19危重程度
                    String covid19Critical = record.getCovid19CriticalSeverityLevel();
                    if (covid19Critical != null) {
                        statistics.getCovid19CriticalDistribution().merge(covid19Critical, 1, Integer::sum);
                    } else {
                        statistics.getCovid19CriticalDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计呼吸道症候群严重程度（需要转换英文到中文）
                    String respiratorySyndrome = record.getRespiratorySyndromeSeverityLevel();
                    if (respiratorySyndrome != null) {
                        String chineseLevel = convertRespiratorySyndromeLevel(respiratorySyndrome);
                        statistics.getRespiratorySyndromeDistribution().merge(chineseLevel, 1, Integer::sum);
                    } else {
                        statistics.getRespiratorySyndromeDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计社区获得性肺炎风险
                    String communityPneumoniaRisk = record.getCommunityAcquiredPneumoniaRisk();
                    if (communityPneumoniaRisk != null) {
                        statistics.getCommunityPneumoniaRiskDistribution().merge(communityPneumoniaRisk, 1, Integer::sum);
                    } else {
                        statistics.getCommunityPneumoniaRiskDistribution().merge("无数据", 1, Integer::sum);
                    }
                    
                    // 统计脓毒症风险
                    String sepsisRisk = record.getSepsisRisk();
                    if (sepsisRisk != null) {
                        statistics.getSepsisRiskDistribution().merge(sepsisRisk, 1, Integer::sum);
                    } else {
                        statistics.getSepsisRiskDistribution().merge("无数据", 1, Integer::sum);
                    }
                }
                
                // 计算平均年龄
                if (statistics.getTotalPatients() > 0) {
                    statistics.setAverageAge((double) totalAge / statistics.getTotalPatients());
                }
                
                // 处理没有患者的情况
                if (statistics.getTotalPatients() == 0) {
                    statistics.setMinAge(null);
                    statistics.setMaxAge(null);
                } else if (statistics.getMinAge() == Integer.MAX_VALUE) {
                    statistics.setMinAge(null);
                }
                
                log.info("性别{}统计完成，总人数：{}, 平均年龄：{}", 
                    gender, statistics.getTotalPatients(), 
                    statistics.getAverageAge() != null ? String.format("%.1f", statistics.getAverageAge()) : "N/A");
                
            } catch (Exception e) {
                log.error("性别{}统计失败", gender, e);
            }
            
            statisticsList.add(statistics);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("性别分类统计完成，耗时：{}ms", endTime - startTime);
        
        return statisticsList;
    }
    
    /**
     * 根据年龄获取年龄段
     */
    private String getAgeGroup(Integer age) {
        if (age == null) {
            return "未知";
        }
        if (age <= 17) {
            return "0-17";
        } else if (age <= 29) {
            return "18-29";
        } else if (age <= 44) {
            return "30-44";
        } else if (age <= 59) {
            return "45-59";
        } else if (age <= 74) {
            return "60-74";
        } else {
            return "75+";
        }
    }
    
    /**
     * 按月份统计综合评估数据
     * 基于入院日期(admission_date)统计每月各种疾病的患者数量
     * 
     * @return 月度统计结果列表
     */
    public List<MonthlyStatisticsDTO> getMonthlyStatistics() {
        log.info("开始执行月度统计");
        long startTime = System.currentTimeMillis();
        
        // 使用TreeMap保证月份有序
        Map<String, MonthlyStatisticsDTO> monthlyStatsMap = new TreeMap<>();
        
        try {
            // 查询所有数据（可优化为按月分批查询）
            ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(100000); // 获取所有数据，实际生产环境应考虑分批处理
            
            IPage<ComprehensiveAssessmentVO> page = queryComprehensivePage(queryDTO);
            List<ComprehensiveAssessmentVO> allRecords = page.getRecords();
            
            // 按月份分组处理数据
            for (ComprehensiveAssessmentVO record : allRecords) {
                // 获取入院日期并格式化为年月
                String monthKey = null;
                if (record.getAdmissionDate() != null) {
                    try {
                        // 假设admissionDate是String格式 "yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-dd"
                        String dateStr = record.getAdmissionDate();
                        if (dateStr.length() >= 7) {
                            monthKey = dateStr.substring(0, 7); // 提取 "yyyy-MM"
                        }
                    } catch (Exception e) {
                        log.warn("解析入院日期失败: {}", record.getAdmissionDate());
                        monthKey = "未知月份";
                    }
                } else {
                    monthKey = "未知月份";
                }
                
                // 获取或创建该月份的统计对象
                MonthlyStatisticsDTO monthStats = monthlyStatsMap.computeIfAbsent(monthKey, k -> {
                    MonthlyStatisticsDTO stats = new MonthlyStatisticsDTO();
                    stats.setMonth(k);
                    stats.initializeDistributions();
                    return stats;
                });
                
                // 更新统计数据
                monthStats.setTotalPatients(monthStats.getTotalPatients() + 1);
                monthStats.setNewAdmissions(monthStats.getNewAdmissions() + 1);
                
                // 统计性别
                String gender = record.getGender();
                if (gender != null) {
                    monthStats.getGenderDistribution().merge(gender, 1, Integer::sum);
                } else {
                    monthStats.getGenderDistribution().merge("未知", 1, Integer::sum);
                }
                
                // 统计年龄段
                if (record.getAge() != null) {
                    String ageGroup = getAgeGroup(record.getAge());
                    monthStats.getAgeGroupDistribution().merge(ageGroup, 1, Integer::sum);
                }
                
                // 统计COVID-19严重程度
                String covid19Severity = record.getCovid19SeverityLevel();
                if (covid19Severity != null) {
                    monthStats.getCovid19SeverityDistribution().merge(covid19Severity, 1, Integer::sum);
                } else {
                    monthStats.getCovid19SeverityDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计COVID-19危重程度
                String covid19Critical = record.getCovid19CriticalSeverityLevel();
                if (covid19Critical != null) {
                    monthStats.getCovid19CriticalDistribution().merge(covid19Critical, 1, Integer::sum);
                } else {
                    monthStats.getCovid19CriticalDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计呼吸道症候群严重程度
                String respiratorySyndrome = record.getRespiratorySyndromeSeverityLevel();
                if (respiratorySyndrome != null) {
                    String chineseLevel = convertRespiratorySyndromeLevel(respiratorySyndrome);
                    monthStats.getRespiratorySyndromeDistribution().merge(chineseLevel, 1, Integer::sum);
                } else {
                    monthStats.getRespiratorySyndromeDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计社区获得性肺炎风险
                String communityPneumoniaRisk = record.getCommunityAcquiredPneumoniaRisk();
                if (communityPneumoniaRisk != null) {
                    monthStats.getCommunityPneumoniaRiskDistribution().merge(communityPneumoniaRisk, 1, Integer::sum);
                } else {
                    monthStats.getCommunityPneumoniaRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计脓毒症风险
                String sepsisRisk = record.getSepsisRisk();
                if (sepsisRisk != null) {
                    monthStats.getSepsisRiskDistribution().merge(sepsisRisk, 1, Integer::sum);
                } else {
                    monthStats.getSepsisRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计重症肺炎诊断（基于isSeverePneumonia字段）
                Boolean isSeverePneumonia = record.getIsSeverePneumonia();
                if (isSeverePneumonia != null) {
                    String diagnosis = isSeverePneumonia ? "重症肺炎" : "非重症肺炎";
                    monthStats.getSeverePneumoniaDiagnosisDistribution().merge(diagnosis, 1, Integer::sum);
                } else {
                    monthStats.getSeverePneumoniaDiagnosisDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计CURB-65风险
                String curbRisk = record.getCurbRiskLevel();
                if (curbRisk != null) {
                    monthStats.getCurbRiskDistribution().merge(curbRisk, 1, Integer::sum);
                } else {
                    monthStats.getCurbRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计PSI风险
                String psiRisk = record.getPsiRiskClass();
                if (psiRisk != null) {
                    monthStats.getPsiRiskDistribution().merge(psiRisk, 1, Integer::sum);
                } else {
                    monthStats.getPsiRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计CPIS风险
                String cpisRisk = record.getCpisRiskLevel();
                if (cpisRisk != null) {
                    monthStats.getCpisRiskDistribution().merge(cpisRisk, 1, Integer::sum);
                } else {
                    monthStats.getCpisRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计qSOFA风险
                String qsofaRisk = record.getQsofaRiskLevel();
                if (qsofaRisk != null) {
                    monthStats.getQsofaRiskDistribution().merge(qsofaRisk, 1, Integer::sum);
                } else {
                    monthStats.getQsofaRiskDistribution().merge("无数据", 1, Integer::sum);
                }
                
                // 统计SOFA严重程度
                String sofaSeverity = record.getSofaSeverityLevel();
                if (sofaSeverity != null) {
                    monthStats.getSofaSeverityDistribution().merge(sofaSeverity, 1, Integer::sum);
                } else {
                    monthStats.getSofaSeverityDistribution().merge("无数据", 1, Integer::sum);
                }
            }
            
            // 转换为列表并返回
            List<MonthlyStatisticsDTO> result = new ArrayList<>(monthlyStatsMap.values());
            
            long endTime = System.currentTimeMillis();
            log.info("月度统计完成，统计月份数：{}，总耗时：{}ms", result.size(), endTime - startTime);
            
            // 输出每个月份的统计概要
            for (MonthlyStatisticsDTO monthStats : result) {
                log.info("月份 {} - 总患者数：{}，新入院：{}", 
                    monthStats.getMonth(), 
                    monthStats.getTotalPatients(), 
                    monthStats.getNewAdmissions());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("月度统计失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 优化版综合评估分页查询
     * 使用数据库视图和优化的查询策略，确保响应时间在2秒内
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public IPage<ComprehensiveAssessmentVO> queryComprehensivePageOptimized(ComprehensiveAssessmentQueryDTO queryDTO) {
        long startTime = System.currentTimeMillis();
        log.info("开始优化版综合评估分页查询，查询条件: {}", queryDTO);
        
        // 构建分页对象
        Page<ComprehensiveAssessmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        try {
            // 使用优化的查询方法，只传递基本的查询参数
            IPage<ComprehensiveAssessmentVO> result = optimizedMapper.selectComprehensivePageOptimized(
                page,
                queryDTO.getPatientNumber(),
                queryDTO.getGender(), 
                queryDTO.getMinAge(),
                queryDTO.getMaxAge(),
                queryDTO.getHasAssessment()
            );
            
            // 后处理：计算综合风险评级
            for (ComprehensiveAssessmentVO vo : result.getRecords()) {
                // 计算社区获得性肺炎综合风险
                String communityPneumoniaRisk = calculateCommunityPneumoniaRisk(vo);
                vo.setCommunityAcquiredPneumoniaRisk(communityPneumoniaRisk);
                
                // 计算脓毒症综合风险
                String sepsisRisk = calculateSepsisRisk(vo);
                vo.setSepsisRisk(sepsisRisk);
            }
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 性能监控
            if (executionTime > 2000) {
                log.warn("优化版查询耗时超过2秒: {}ms, 总记录数: {}, 当前页记录数: {}", 
                    executionTime, result.getTotal(), result.getRecords().size());
            } else {
                log.info("优化版查询成功，耗时: {}ms, 总记录数: {}, 当前页记录数: {}", 
                    executionTime, result.getTotal(), result.getRecords().size());
            }
            
            return result;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("优化版综合评估查询失败，耗时: {}ms", endTime - startTime, e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用缓存的综合评估分页查询
     * 对于频繁查询的条件，使用缓存提升性能
     * 
     * @param queryDTO 查询条件
     * @param useCache 是否使用缓存
     * @return 分页结果
     */
    public IPage<ComprehensiveAssessmentVO> queryComprehensivePageWithCache(
            ComprehensiveAssessmentQueryDTO queryDTO, 
            boolean useCache) {
        
        if (!useCache) {
            return queryComprehensivePageOptimized(queryDTO);
        }
        
        // 生成缓存键
        String cacheKey = generateCacheKey(queryDTO);
        
        // TODO: 实现缓存逻辑（可以使用Redis或本地缓存）
        // 这里先直接调用优化版查询
        return queryComprehensivePageOptimized(queryDTO);
    }
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(ComprehensiveAssessmentQueryDTO queryDTO) {
        return String.format("comprehensive:page:%d:%d:%s:%s:%s:%s",
            queryDTO.getPageNum(),
            queryDTO.getPageSize(),
            queryDTO.getPatientNumber() != null ? queryDTO.getPatientNumber() : "null",
            queryDTO.getGender() != null ? queryDTO.getGender() : "null",
            queryDTO.getMinAge() != null ? queryDTO.getMinAge() : "null",
            queryDTO.getMaxAge() != null ? queryDTO.getMaxAge() : "null"
        );
    }
    
    /**
     * 获取疾病分类统计
     * 统计各种疾病的患者数量
     * 
     * @return 疾病统计结果
     */
    public DiseaseStatisticsDTO getDiseaseStatistics() {
        log.info("开始执行疾病分类统计");
        long startTime = System.currentTimeMillis();
        
        DiseaseStatisticsDTO statistics = new DiseaseStatisticsDTO();
        statistics.initializeStatistics();
        
        // 设置统计时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        statistics.setStatisticsTime(sdf.format(new Date()));
        
        try {
            // 分页查询所有患者数据
            int pageSize = 10000; // 大批量查询
            int pageNum = 1;
            boolean hasMore = true;
            
            int totalProcessed = 0;
            
            while (hasMore) {
                ComprehensiveAssessmentQueryDTO queryDTO = new ComprehensiveAssessmentQueryDTO();
                queryDTO.setPageNum(pageNum);
                queryDTO.setPageSize(pageSize);
                
                // 使用优化版查询以提高性能
                IPage<ComprehensiveAssessmentVO> page = queryComprehensivePage(queryDTO);
                
                if (page != null && page.getRecords() != null && !page.getRecords().isEmpty()) {
                    for (ComprehensiveAssessmentVO assessment : page.getRecords()) {
                        totalProcessed++;
                        
                        // 统计社区获得性肺炎风险
                        String communityPneumoniaRisk = assessment.getCommunityAcquiredPneumoniaRisk();
                        if (communityPneumoniaRisk != null && !communityPneumoniaRisk.isEmpty()) {
                            statistics.getCommunityPneumoniaRiskStats().merge(communityPneumoniaRisk, 1, Integer::sum);
                            if ("高风险".equals(communityPneumoniaRisk)) {
                                statistics.setCommunityPneumoniaHighRiskCount(
                                    statistics.getCommunityPneumoniaHighRiskCount() + 1);
                            } else if ("低风险".equals(communityPneumoniaRisk)) {
                                statistics.setCommunityPneumoniaLowRiskCount(
                                    statistics.getCommunityPneumoniaLowRiskCount() + 1);
                            }
                        } else {
                            statistics.getCommunityPneumoniaRiskStats().merge("无数据", 1, Integer::sum);
                        }
                        
                        // 统计脓毒症风险
                        String sepsisRisk = assessment.getSepsisRisk();
                        if (sepsisRisk != null && !sepsisRisk.isEmpty()) {
                            statistics.getSepsisRiskStats().merge(sepsisRisk, 1, Integer::sum);
                            if ("高风险".equals(sepsisRisk)) {
                                statistics.setSepsisHighRiskCount(statistics.getSepsisHighRiskCount() + 1);
                            } else if ("低风险".equals(sepsisRisk)) {
                                statistics.setSepsisLowRiskCount(statistics.getSepsisLowRiskCount() + 1);
                            }
                        } else {
                            statistics.getSepsisRiskStats().merge("无数据", 1, Integer::sum);
                        }
                        
                        // 统计COVID-19重型
                        String covid19Severity = assessment.getCovid19SeverityLevel();
                        Boolean isSevereType = assessment.getCovid19IsSevereType();
                        if (covid19Severity != null || isSevereType != null) {
                            if (Boolean.TRUE.equals(isSevereType) || "重型".equals(covid19Severity)) {
                                statistics.getCovid19Stats().merge("重型", 1, Integer::sum);
                                statistics.setCovid19SevereCount(statistics.getCovid19SevereCount() + 1);
                            } else {
                                statistics.getCovid19Stats().merge("非重型", 1, Integer::sum);
                                statistics.setCovid19NonSevereCount(statistics.getCovid19NonSevereCount() + 1);
                            }
                        } else {
                            statistics.getCovid19Stats().merge("未评估", 1, Integer::sum);
                        }
                        
                        // 统计COVID-19危重型
                        String covid19CriticalSeverity = assessment.getCovid19CriticalSeverityLevel();
                        Boolean isCriticalType = assessment.getCovid19IsCriticalType();
                        if (covid19CriticalSeverity != null || isCriticalType != null) {
                            if (Boolean.TRUE.equals(isCriticalType) || "危重型".equals(covid19CriticalSeverity)) {
                                statistics.getCovid19CriticalStats().merge("危重型", 1, Integer::sum);
                                statistics.setCovid19CriticalCount(statistics.getCovid19CriticalCount() + 1);
                            } else {
                                statistics.getCovid19CriticalStats().merge("非危重型", 1, Integer::sum);
                                statistics.setCovid19NonCriticalCount(statistics.getCovid19NonCriticalCount() + 1);
                            }
                        } else {
                            statistics.getCovid19CriticalStats().merge("未评估", 1, Integer::sum);
                        }
                        
                        // 统计呼吸道症候群
                        String respiratorySyndromeSeverity = assessment.getRespiratorySyndromeSeverityLevel();
                        if (respiratorySyndromeSeverity != null && !respiratorySyndromeSeverity.isEmpty()) {
                            statistics.getRespiratorySyndromeStats().merge("已评估", 1, Integer::sum);
                            
                            // 转换英文等级为中文
                            String chineseSeverity = convertRespiratorySyndromeSeverity(respiratorySyndromeSeverity);
                            statistics.getRespiratorySyndromeLevelStats().merge(chineseSeverity, 1, Integer::sum);
                            statistics.setAssessedPatients(statistics.getAssessedPatients() + 1);
                        } else {
                            statistics.getRespiratorySyndromeStats().merge("未评估", 1, Integer::sum);
                            statistics.getRespiratorySyndromeLevelStats().merge("无数据", 1, Integer::sum);
                            statistics.setUnassessedPatients(statistics.getUnassessedPatients() + 1);
                        }
                    }
                    
                    // 检查是否还有更多数据
                    hasMore = page.getCurrent() < page.getPages();
                    pageNum++;
                } else {
                    hasMore = false;
                }
                
                // 防止无限循环
                if (pageNum > 100) {
                    log.warn("疾病统计查询页数超过100页，停止查询");
                    break;
                }
            }
            
            statistics.setTotalPatients(totalProcessed);
            
            long endTime = System.currentTimeMillis();
            log.info("疾病分类统计完成，处理患者数: {}, 耗时: {}ms", 
                totalProcessed, (endTime - startTime));
            
            // 输出统计摘要
            log.info("社区获得性肺炎: 高风险={}, 低风险={}", 
                statistics.getCommunityPneumoniaHighRiskCount(),
                statistics.getCommunityPneumoniaLowRiskCount());
            log.info("脓毒症: 高风险={}, 低风险={}", 
                statistics.getSepsisHighRiskCount(),
                statistics.getSepsisLowRiskCount());
            log.info("COVID-19: 重型={}, 非重型={}", 
                statistics.getCovid19SevereCount(),
                statistics.getCovid19NonSevereCount());
            log.info("COVID-19: 危重型={}, 非危重型={}", 
                statistics.getCovid19CriticalCount(),
                statistics.getCovid19NonCriticalCount());
            log.info("呼吸道症候群: 已评估={}, 未评估={}", 
                statistics.getAssessedPatients(),
                statistics.getUnassessedPatients());
            
            return statistics;
            
        } catch (Exception e) {
            log.error("疾病分类统计失败: ", e);
            throw new RuntimeException("疾病分类统计失败: " + e.getMessage(), e);
        }
    }
}
