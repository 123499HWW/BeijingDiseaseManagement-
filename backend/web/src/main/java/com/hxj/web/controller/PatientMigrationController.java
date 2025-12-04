package com.hxj.web.controller;

import com.hxj.common.result.Result;
import com.hxj.service.PatientDataMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 患者数据迁移控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/migration")
@RequiredArgsConstructor
public class PatientMigrationController {

    private final PatientDataMigrationService migrationService;

    /**
     * 执行患者体检数据迁移
     */
    @PostMapping("/patient-examination")
    public Result<PatientDataMigrationService.MigrationResult> migratePatientExamination(
            @RequestParam(defaultValue = "SYSTEM") String createdBy) {
        
        log.info("开始执行患者体检数据迁移，操作人: {}", createdBy);
        
        try {
            PatientDataMigrationService.MigrationResult result = 
                migrationService.migrateAllPatientData(createdBy);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("患者体检数据迁移失败", e);
            return Result.error("迁移失败: " + e.getMessage());
        }
    }

    /**
     * 回滚指定患者的迁移数据
     */
    @DeleteMapping("/patient-examination/{patientId}")
    public Result<String> rollbackPatientMigration(@PathVariable Long patientId) {
        
        log.info("开始回滚患者迁移数据, patientId: {}", patientId);
        
        try {
            migrationService.rollbackPatientMigration(patientId);
            return Result.success("回滚成功");
            
        } catch (Exception e) {
            log.error("回滚患者迁移数据失败, patientId: {}", patientId, e);
            return Result.error("回滚失败: " + e.getMessage());
        }
    }

}
