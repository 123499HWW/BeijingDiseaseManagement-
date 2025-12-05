package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxj.common.entity.Patient;
import com.hxj.common.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 患者编号生成服务
 * 负责生成和管理患者编号
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientNumberGeneratorService {

    private final PatientMapper patientMapper;
    
    private static final String PATIENT_NUMBER_PREFIX = "P";
    private static final int PATIENT_NUMBER_LENGTH = 4;
    private static final Pattern PATIENT_NUMBER_PATTERN = Pattern.compile("^P\\d{4}$");

    /**
     * 生成下一个患者编号
     * 
     * @return 格式化的患者编号，如P0001
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized String generateNextPatientNumber() {
        try {
            // 查询当前最大的患者编号
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Patient::getPatientNumber)
                       .isNotNull(Patient::getPatientNumber)
                       .eq(Patient::getIsDeleted, 0)
                       .orderByDesc(Patient::getPatientNumber)
                       .last("LIMIT 1");
            
            Patient lastPatient = patientMapper.selectOne(queryWrapper);
            
            int nextNumber = 1;
            
            if (lastPatient != null && StringUtils.hasText(lastPatient.getPatientNumber())) {
                String lastNumber = lastPatient.getPatientNumber();
                
                // 验证编号格式
                if (PATIENT_NUMBER_PATTERN.matcher(lastNumber).matches()) {
                    // 提取数字部分并加1
                    String numberPart = lastNumber.substring(1);
                    nextNumber = Integer.parseInt(numberPart) + 1;
                } else {
                    log.warn("发现格式不正确的患者编号: {}, 将从1开始重新生成", lastNumber);
                    nextNumber = getMaxValidPatientNumber() + 1;
                }
            }
            
            String newPatientNumber = formatPatientNumber(nextNumber);
            
            // 检查编号是否已存在（防止并发问题）
            while (isPatientNumberExists(newPatientNumber)) {
                nextNumber++;
                newPatientNumber = formatPatientNumber(nextNumber);
            }
            
            log.debug("生成新的患者编号: {}", newPatientNumber);
            return newPatientNumber;
            
        } catch (Exception e) {
            log.error("生成患者编号失败", e);
            throw new RuntimeException("生成患者编号失败", e);
        }
    }

    /**
     * 验证患者编号格式是否正确
     * 
     * @param patientNumber 患者编号
     * @return 是否格式正确
     */
    public boolean isValidPatientNumber(String patientNumber) {
        if (!StringUtils.hasText(patientNumber)) {
            return false;
        }
        return PATIENT_NUMBER_PATTERN.matcher(patientNumber).matches();
    }

    /**
     * 检查患者编号是否已存在
     * 
     * @param patientNumber 患者编号
     * @return 是否已存在
     */
    public boolean isPatientNumberExists(String patientNumber) {
        if (!StringUtils.hasText(patientNumber)) {
            return false;
        }
        
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getPatientNumber, patientNumber)
                   .eq(Patient::getIsDeleted, 0);
        
        return patientMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 为现有患者批量生成编号
     * 
     * @param updatedBy 更新人
     * @return 生成结果统计
     */
    @Transactional(rollbackFor = Exception.class)
    public GenerationResult generateNumbersForExistingPatients(String updatedBy) {
        log.info("开始为现有患者批量生成编号，操作人: {}", updatedBy);
        
        GenerationResult result = new GenerationResult();
        
        try {
            // 查询所有没有患者编号的患者
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.isNull(Patient::getPatientNumber)
                       .or()
                       .eq(Patient::getPatientNumber, "")
                       .eq(Patient::getIsDeleted, 0)
                       .orderByAsc(Patient::getPatientId);
            
            var patients = patientMapper.selectList(queryWrapper);
            result.setTotalCount(patients.size());
            
            log.info("找到需要生成编号的患者数: {}", patients.size());
            
            int counter = getMaxValidPatientNumber() + 1;
            
            for (Patient patient : patients) {
                try {
                    String patientNumber = formatPatientNumber(counter);
                    
                    // 确保编号不重复
                    while (isPatientNumberExists(patientNumber)) {
                        counter++;
                        patientNumber = formatPatientNumber(counter);
                    }
                    
                    // 更新患者编号
                    patient.setPatientNumber(patientNumber);
                    patient.setUpdatedBy(updatedBy);
                    patientMapper.updateById(patient);
                    
                    result.incrementSuccessCount();
                    counter++;
                    
                } catch (Exception e) {
                    log.error("为患者{}生成编号失败", patient.getPatientId(), e);
                    result.incrementFailureCount();
                    result.addErrorMessage(String.format("患者ID %d 生成编号失败: %s", 
                                                        patient.getPatientId(), e.getMessage()));
                }
            }
            
            log.info("患者编号批量生成完成，成功: {}, 失败: {}", 
                    result.getSuccessCount(), result.getFailureCount());
            
        } catch (Exception e) {
            log.error("批量生成患者编号过程中发生异常", e);
            throw new RuntimeException("批量生成患者编号失败", e);
        }
        
        return result;
    }

    /**
     * 格式化患者编号
     * 
     * @param number 数字
     * @return 格式化的患者编号
     */
    private String formatPatientNumber(int number) {
        return String.format("%s%0" + PATIENT_NUMBER_LENGTH + "d", PATIENT_NUMBER_PREFIX, number);
    }

    /**
     * 获取当前最大的有效患者编号数字部分
     * 
     * @return 最大编号数字
     */
    private int getMaxValidPatientNumber() {
        try {
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Patient::getPatientNumber)
                       .isNotNull(Patient::getPatientNumber)
                       .eq(Patient::getIsDeleted, 0);
            
            var patients = patientMapper.selectList(queryWrapper);
            
            int maxNumber = 0;
            for (Patient patient : patients) {
                String patientNumber = patient.getPatientNumber();
                if (StringUtils.hasText(patientNumber) && PATIENT_NUMBER_PATTERN.matcher(patientNumber).matches()) {
                    int number = Integer.parseInt(patientNumber.substring(1));
                    maxNumber = Math.max(maxNumber, number);
                }
            }
            
            return maxNumber;
            
        } catch (Exception e) {
            log.error("获取最大患者编号失败", e);
            return 0;
        }
    }

    /**
     * 生成结果统计类
     */
    public static class GenerationResult {
        private int totalCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private StringBuilder errorMessages = new StringBuilder();
        
        public void incrementSuccessCount() {
            this.successCount++;
        }
        
        public void incrementFailureCount() {
            this.failureCount++;
        }
        
        public void addErrorMessage(String message) {
            if (errorMessages.length() > 0) {
                errorMessages.append("\n");
            }
            errorMessages.append(message);
        }
        
        // Getters and Setters
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public String getErrorMessages() { return errorMessages.toString(); }
        
        @Override
        public String toString() {
            return String.format("患者编号生成结果: 总数=%d, 成功=%d, 失败=%d", 
                               totalCount, successCount, failureCount);
        }
    }
}
