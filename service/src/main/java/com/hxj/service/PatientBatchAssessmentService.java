package com.hxj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 患者批量评估服务
 * 提供批量执行所有评估的功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientBatchAssessmentService {
    
    private final CurbAssessmentService curbAssessmentService;
    private final PsiAssessmentService psiAssessmentService;
    private final CpisAssessmentService cpisAssessmentService;
    private final QsofaAssessmentService qsofaAssessmentService;
    private final SofaAssessmentService sofaAssessmentService;
    private final Covid19AssessmentService covid19AssessmentService;
    private final Covid19CriticalAssessmentService covid19CriticalAssessmentService;
    private final SeverePneumoniaDiagnosisService severePneumoniaDiagnosisService;
    
    // 创建固定大小的线程池用于并行处理
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    /**
     * 批量执行所有评估（串行执行各评估，批量处理患者）
     * @param patientIds 患者ID列表
     * @return 评估结果统计
     */
    @Transactional
    public BatchAssessmentResult performBatchAssessment(List<Long> patientIds) {
        log.info("开始批量评估，患者数量: {}", patientIds.size());
        
        BatchAssessmentResult result = new BatchAssessmentResult();
        result.setTotalPatients(patientIds.size());
        
        // 按评估类型依次执行批量评估
        performBatchCurbAssessment(patientIds, result);
        performBatchPsiAssessment(patientIds, result);
        performBatchCpisAssessment(patientIds, result);
        performBatchQsofaAssessment(patientIds, result);
        performBatchSofaAssessment(patientIds, result);
        performBatchCovid19Assessment(patientIds, result);
        performBatchCovid19CriticalAssessment(patientIds, result);
        performBatchSeverePneumoniaDiagnosis(patientIds, result);
        
        log.info("批量评估完成，结果: {}", result);
        return result;
    }
    
    /**
     * 批量执行所有评估（并行执行）
     * @param patientIds 患者ID列表
     * @return 评估结果统计
     */
    public BatchAssessmentResult performParallelBatchAssessment(List<Long> patientIds) {
        log.info("开始并行批量评估，患者数量: {}", patientIds.size());
        
        BatchAssessmentResult result = new BatchAssessmentResult();
        result.setTotalPatients(patientIds.size());
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // 创建并行任务
        futures.add(CompletableFuture.runAsync(() -> performBatchCurbAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchPsiAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchCpisAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchQsofaAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchSofaAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchCovid19Assessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchCovid19CriticalAssessment(patientIds, result), executorService));
        futures.add(CompletableFuture.runAsync(() -> performBatchSeverePneumoniaDiagnosis(patientIds, result), executorService));
        
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        
        try {
            allFutures.get(30, TimeUnit.MINUTES); // 最多等待30分钟
        } catch (Exception e) {
            log.error("并行批量评估执行超时或失败", e);
        }
        
        log.info("并行批量评估完成，结果: {}", result);
        return result;
    }
    
    // CURB-65批量评估
    private void performBatchCurbAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始CURB-65批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            curbAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("CURB-65批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("CURB-65批量评估失败", e);
            // 如果批量失败，尝试单个处理
            for (Long patientId : patientIds) {
                try {
                    curbAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("CURB-65评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("CURB-65", success.get(), failure.get());
    }
    
    // PSI批量评估
    private void performBatchPsiAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始PSI批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            psiAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("PSI批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("PSI批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    psiAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("PSI评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("PSI", success.get(), failure.get());
    }
    
    // CPIS批量评估
    private void performBatchCpisAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始CPIS批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            cpisAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("CPIS批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("CPIS批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    cpisAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("CPIS评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("CPIS", success.get(), failure.get());
    }
    
    // qSOFA批量评估
    private void performBatchQsofaAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始qSOFA批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            qsofaAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("qSOFA批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("qSOFA批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    qsofaAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("qSOFA评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("qSOFA", success.get(), failure.get());
    }
    
    // SOFA批量评估
    private void performBatchSofaAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始SOFA批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            sofaAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("SOFA批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("SOFA批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    sofaAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("SOFA评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("SOFA", success.get(), failure.get());
    }
    
    // COVID-19重型批量评估
    private void performBatchCovid19Assessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始COVID-19重型批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            covid19AssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("COVID-19重型批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("COVID-19重型批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    covid19AssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("COVID-19重型评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("COVID-19重型", success.get(), failure.get());
    }
    
    // COVID-19危重型批量评估
    private void performBatchCovid19CriticalAssessment(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始COVID-19危重型批量评估");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            covid19CriticalAssessmentService.performBatchAssessment(patientIds);
            success.set(patientIds.size());
            log.info("COVID-19危重型批量评估成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("COVID-19危重型批量评估失败", e);
            for (Long patientId : patientIds) {
                try {
                    covid19CriticalAssessmentService.performAssessment(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("COVID-19危重型评估失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("COVID-19危重型", success.get(), failure.get());
    }
    
    // 重症肺炎批量诊断
    private void performBatchSeverePneumoniaDiagnosis(List<Long> patientIds, BatchAssessmentResult result) {
        log.debug("开始重症肺炎批量诊断");
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        
        try {
            severePneumoniaDiagnosisService.performBatchDiagnosis(patientIds);
            success.set(patientIds.size());
            log.info("重症肺炎批量诊断成功，患者数量: {}", patientIds.size());
        } catch (Exception e) {
            log.error("重症肺炎批量诊断失败", e);
            for (Long patientId : patientIds) {
                try {
                    severePneumoniaDiagnosisService.performDiagnosis(patientId);
                    success.incrementAndGet();
                } catch (Exception ex) {
                    log.error("重症肺炎诊断失败，患者ID: {}", patientId, ex);
                    failure.incrementAndGet();
                }
            }
        }
        
        result.addAssessmentResult("重症肺炎", success.get(), failure.get());
    }
    
    /**
     * 批量评估结果
     */
    public static class BatchAssessmentResult {
        private int totalPatients;
        private Map<String, AssessmentStat> assessmentStats = new HashMap<>();
        
        public void addAssessmentResult(String assessmentType, int success, int failure) {
            assessmentStats.put(assessmentType, new AssessmentStat(success, failure));
        }
        
        public int getTotalPatients() {
            return totalPatients;
        }
        
        public void setTotalPatients(int totalPatients) {
            this.totalPatients = totalPatients;
        }
        
        public Map<String, AssessmentStat> getAssessmentStats() {
            return assessmentStats;
        }
        
        public int getTotalSuccess() {
            return assessmentStats.values().stream()
                    .mapToInt(AssessmentStat::getSuccess)
                    .sum();
        }
        
        public int getTotalFailure() {
            return assessmentStats.values().stream()
                    .mapToInt(AssessmentStat::getFailure)
                    .sum();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("BatchAssessmentResult{totalPatients=").append(totalPatients);
            sb.append(", assessments=[");
            assessmentStats.forEach((type, stat) -> 
                sb.append(type).append("(成功:").append(stat.success)
                  .append(",失败:").append(stat.failure).append(") "));
            sb.append("]}");
            return sb.toString();
        }
    }
    
    /**
     * 评估统计
     */
    public static class AssessmentStat {
        private final int success;
        private final int failure;
        
        public AssessmentStat(int success, int failure) {
            this.success = success;
            this.failure = failure;
        }
        
        public int getSuccess() {
            return success;
        }
        
        public int getFailure() {
            return failure;
        }
    }
}
