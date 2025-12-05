package com.hxj.service;

import com.hxj.common.dto.patient.PatientCreateRequest;
import com.hxj.common.dto.patient.PatientImportResultDTO;
import com.hxj.common.dto.patient.PatientInfoResponse;
import com.hxj.common.dto.patient.PatientQueryRequest;
import com.hxj.common.dto.patient.PatientUpdateRequest;
import com.hxj.common.result.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 患者信息服务接口
 */
public interface PatientService {

    /**
     * 创建患者信息
     * 
     * @param request 患者创建请求
     * @param createdBy 创建者用户名
     * @return 患者ID
     */
    Long createPatient(PatientCreateRequest request, String createdBy);

    /**
     * 根据ID获取患者信息
     * 
     * @param patientId 患者ID
     * @return 患者信息响应
     */
    PatientInfoResponse getPatientById(Long patientId);

    /**
     * 更新患者信息
     * 
     * @param request 患者更新请求
     * @param updatedBy 更新者用户名
     */
    Long updatePatient(PatientUpdateRequest request, String updatedBy);

    /**
     * 删除患者信息（逻辑删除）
     * 
     * @param patientId 患者ID
     * @param deletedBy 删除者用户名
     */
    void deletePatient(Long patientId, String deletedBy);

    /**
     * 分页查询患者信息
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<PatientInfoResponse> queryPatients(PatientQueryRequest request);

    /**
     * 批量删除患者信息（逻辑删除）
     * 
     * @param patientIds 患者ID数组
     * @param deletedBy 删除者用户名
     */
    void batchDeletePatients(Long[] patientIds, String deletedBy);

    /**
     * 从Excel文件批量导入患者信息
     * 
     * @param file Excel文件
     * @param operatorUsername 操作者用户名
     * @return 导入结果
     */
    PatientImportResultDTO importPatientsFromExcel(MultipartFile file, String operatorUsername);
}
