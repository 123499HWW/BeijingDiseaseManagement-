package com.hxj.web.controller;

import com.hxj.common.dto.patient.PatientCreateRequest;
import com.hxj.common.dto.patient.PatientImportResultDTO;
import com.hxj.common.dto.patient.PatientInfoResponse;
import com.hxj.common.dto.patient.PatientQueryRequest;
import com.hxj.common.dto.patient.PatientUpdateRequest;
import com.hxj.common.exception.BizException;
import com.hxj.common.result.PageResult;
import com.hxj.common.result.Result;
import com.hxj.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 患者信息管理控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 创建患者信息
     */
    @PostMapping("/create")
    public Result<Long> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            Long patientId = patientService.createPatient(request, operatorUsername);
            return Result.success("患者信息创建成功", patientId);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取患者信息
     */
    @GetMapping("/{patientId}")
    public Result<PatientInfoResponse> getPatientById(@PathVariable Long patientId) {
        try {
            PatientInfoResponse patient = patientService.getPatientById(patientId);
            return Result.success(patient);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新患者信息
     * 只更新传入的非空字段
     */
    @PostMapping("/update")
    public Result<Long> updatePatient(@Valid @RequestBody PatientUpdateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            patientService.updatePatient(request, operatorUsername);
            return Result.success("患者信息更新成功", request.getPatientId());
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除患者信息（逻辑删除）
     */
    @DeleteMapping("/delete/{patientId}")
    public Result<Void> deletePatient(@PathVariable Long patientId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            patientService.deletePatient(patientId, operatorUsername);
            return Result.success("患者信息删除成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询患者信息
     * 支持多种条件查询，当不传入任何查询条件时，返回所有患者信息
     */
    @PostMapping("/list")
    public Result<PageResult<PatientInfoResponse>> queryPatients(@RequestBody(required = false) PatientQueryRequest request) {
        try {
            // 如果请求体为空，创建一个默认的查询请求
            if (request == null) {
                request = new PatientQueryRequest();
            }
            
            PageResult<PatientInfoResponse> result = patientService.queryPatients(request);
            return Result.success(result);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error("查询患者信息失败: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }

    /**
     * 批量删除患者信息（逻辑删除）
     */
    @DeleteMapping("/batch-delete")
    public Result<Void> batchDeletePatients(@RequestBody Long[] patientIds) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            for (Long patientId : patientIds) {
                patientService.deletePatient(patientId, operatorUsername);
            }
            
            return Result.success("批量删除成功", null);
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从Excel文件批量导入患者信息
     */
    @PostMapping("/import")
    public Result<PatientImportResultDTO> importPatients(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String operatorUsername = authentication.getName();
            
            PatientImportResultDTO result = patientService.importPatientsFromExcel(file, operatorUsername);
            
            if (result.isSuccess()) {
                return Result.success("导入成功，共导入" + result.getSuccessCount() + "条数据", result);
            } else {
                return Result.success("导入完成，成功" + result.getSuccessCount() + "条，失败" + result.getErrorCount() + "条", result);
            }
        } catch (BizException e) {
            return Result.error(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
