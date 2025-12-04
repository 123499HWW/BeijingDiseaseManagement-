package com.hxj.web.controller;

import com.hxj.common.result.Result;
import com.hxj.service.PatientNumberGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 患者编号管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/patient/number")
@RequiredArgsConstructor
public class PatientNumberController {

    private final PatientNumberGeneratorService patientNumberGeneratorService;

    /**
     * 生成下一个患者编号
     */
    @GetMapping("/generate")
    public Result<String> generateNextPatientNumber() {
        
        log.info("请求生成下一个患者编号");
        
        try {
            String patientNumber = patientNumberGeneratorService.generateNextPatientNumber();
            return Result.success(patientNumber);
            
        } catch (Exception e) {
            log.error("生成患者编号失败", e);
            return Result.error("生成患者编号失败: " + e.getMessage());
        }
    }

    /**
     * 验证患者编号格式
     */
    @GetMapping("/validate/{patientNumber}")
    public Result<Boolean> validatePatientNumber(@PathVariable String patientNumber) {
        
        log.info("验证患者编号格式: {}", patientNumber);
        
        try {
            boolean isValid = patientNumberGeneratorService.isValidPatientNumber(patientNumber);
            return Result.success(isValid);
            
        } catch (Exception e) {
            log.error("验证患者编号格式失败: {}", patientNumber, e);
            return Result.error("验证失败: " + e.getMessage());
        }
    }

    /**
     * 检查患者编号是否已存在
     */
    @GetMapping("/exists/{patientNumber}")
    public Result<Boolean> checkPatientNumberExists(@PathVariable String patientNumber) {
        
        log.info("检查患者编号是否存在: {}", patientNumber);
        
        try {
            boolean exists = patientNumberGeneratorService.isPatientNumberExists(patientNumber);
            return Result.success(exists);
            
        } catch (Exception e) {
            log.error("检查患者编号是否存在失败: {}", patientNumber, e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 为现有患者批量生成编号
     */
    @PostMapping("/generate-batch")
    public Result<PatientNumberGeneratorService.GenerationResult> generateNumbersForExistingPatients(
            @RequestParam(defaultValue = "SYSTEM") String updatedBy) {
        
        log.info("开始为现有患者批量生成编号，操作人: {}", updatedBy);
        
        try {
            PatientNumberGeneratorService.GenerationResult result = 
                patientNumberGeneratorService.generateNumbersForExistingPatients(updatedBy);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量生成患者编号失败", e);
            return Result.error("批量生成失败: " + e.getMessage());
        }
    }
}
