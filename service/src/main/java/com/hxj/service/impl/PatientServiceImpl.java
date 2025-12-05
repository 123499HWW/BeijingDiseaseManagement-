package com.hxj.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.patient.PatientCreateRequest;
import com.hxj.common.dto.patient.PatientExcelImportDTO;
import com.hxj.common.dto.patient.PatientImportResultDTO;
import com.hxj.common.dto.patient.PatientInfoResponse;
import com.hxj.common.dto.patient.PatientQueryRequest;
import com.hxj.common.dto.patient.PatientUpdateRequest;
import com.hxj.common.entity.Patient;
import com.hxj.common.enums.ResponseCodeEnum;
import com.hxj.common.exception.BizException;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.result.PageResult;
import com.hxj.service.PatientService;
import com.hxj.service.converter.StringConverter;
import com.hxj.service.listener.PatientExcelListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 患者信息服务实现类
 */
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public Long createPatient(PatientCreateRequest request, String createdBy) {
        // 创建患者实体
        Patient patient = new Patient();
        BeanUtils.copyProperties(request, patient);
        
        // 设置创建信息
        patient.setCreatedAt(LocalDateTime.now());
        patient.setCreatedBy(createdBy);
        patient.setUpdatedAt(LocalDateTime.now());
        patient.setUpdatedBy(createdBy);
        patient.setIsDeleted(0);
        
        // 设置默认值
        if (patient.getChestCtOrdered() == null) {
            patient.setChestCtOrdered(false);
        }
        if (patient.getDopamineUsed() == null) {
            patient.setDopamineUsed(false);
        }
        if (patient.getDobutamineUsed() == null) {
            patient.setDobutamineUsed(false);
        }
        if (patient.getNorepinephrineUsed() == null) {
            patient.setNorepinephrineUsed(false);
        }
        if (patient.getVasoactiveDrugsUsed() == null) {
            patient.setVasoactiveDrugsUsed(false);
        }
        if (patient.getSpecialAntibioticsUsed() == null) {
            patient.setSpecialAntibioticsUsed(false);
        }
        if (patient.getVentilatorUsed() == null) {
            patient.setVentilatorUsed(false);
        }
        if (patient.getIcuAdmission() == null) {
            patient.setIcuAdmission(false);
        }
        
        // 插入数据库
        patientMapper.insert(patient);
        
        return patient.getPatientId();
    }

    @Override
    public PatientInfoResponse getPatientById(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "患者信息不存在");
        }
        
        return convertToPatientInfoResponse(patient);
    }

    @Override
    public Long updatePatient(PatientUpdateRequest request, String updatedBy) {
        // 检查患者是否存在
        Patient existingPatient = patientMapper.selectById(request.getPatientId());
        if (existingPatient == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "患者信息不存在");
        }
        
        // 构建更新条件
        LambdaUpdateWrapper<Patient> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Patient::getPatientId, request.getPatientId());
        
        // 只更新非空字段
        if (request.getVisitCount() != null) {
            updateWrapper.set(Patient::getVisitCount, request.getVisitCount());
        }
        if (request.getAdmissionDate() != null) {
            updateWrapper.set(Patient::getAdmissionDate, request.getAdmissionDate());
        }
        if (request.getGender() != null) {
            updateWrapper.set(Patient::getGender, request.getGender());
        }
        if (request.getAge() != null) {
            updateWrapper.set(Patient::getAge, request.getAge());
        }
        if (StringUtils.hasText(request.getChiefComplaint())) {
            updateWrapper.set(Patient::getChiefComplaint, request.getChiefComplaint());
        }
        if (StringUtils.hasText(request.getPresentIllness())) {
            updateWrapper.set(Patient::getPresentIllness, request.getPresentIllness());
        }
        if (StringUtils.hasText(request.getPhysicalExamination())) {
            updateWrapper.set(Patient::getPhysicalExamination, request.getPhysicalExamination());
        }
        
        // 动脉血气指标
        if (request.getArterialPh() != null) {
            updateWrapper.set(Patient::getArterialPh, request.getArterialPh());
        }
        if (request.getArterialPo2() != null) {
            updateWrapper.set(Patient::getArterialPo2, request.getArterialPo2());
        }
        if (request.getArterialOxygenationIndex() != null) {
            updateWrapper.set(Patient::getArterialOxygenationIndex, request.getArterialOxygenationIndex());
        }
        if (request.getArterialPco2() != null) {
            updateWrapper.set(Patient::getArterialPco2, request.getArterialPco2());
        }
        
        // 血液检查指标
        if (request.getPlateletCount() != null) {
            updateWrapper.set(Patient::getPlateletCount, request.getPlateletCount());
        }
        if (request.getBloodUreaNitrogen() != null) {
            updateWrapper.set(Patient::getBloodUreaNitrogen, request.getBloodUreaNitrogen());
        }
        if (request.getSerumCreatinine() != null) {
            updateWrapper.set(Patient::getSerumCreatinine, request.getSerumCreatinine());
        }
        if (request.getTotalBilirubin() != null) {
            updateWrapper.set(Patient::getTotalBilirubin, request.getTotalBilirubin());
        }
        
        // 胸部CT相关
        if (request.getChestCtOrdered() != null) {
            updateWrapper.set(Patient::getChestCtOrdered, request.getChestCtOrdered());
        }
        if (StringUtils.hasText(request.getChestCtReport())) {
            updateWrapper.set(Patient::getChestCtReport, request.getChestCtReport());
        }
        
        // 药物使用情况
        if (request.getDopamineUsed() != null) {
            updateWrapper.set(Patient::getDopamineUsed, request.getDopamineUsed());
        }
        if (request.getDobutamineUsed() != null) {
            updateWrapper.set(Patient::getDobutamineUsed, request.getDobutamineUsed());
        }
        if (request.getNorepinephrineUsed() != null) {
            updateWrapper.set(Patient::getNorepinephrineUsed, request.getNorepinephrineUsed());
        }
        if (request.getVasoactiveDrugsUsed() != null) {
            updateWrapper.set(Patient::getVasoactiveDrugsUsed, request.getVasoactiveDrugsUsed());
        }
        if (request.getSpecialAntibioticsUsed() != null) {
            updateWrapper.set(Patient::getSpecialAntibioticsUsed, request.getSpecialAntibioticsUsed());
        }
        if (StringUtils.hasText(request.getAntibioticTypes())) {
            updateWrapper.set(Patient::getAntibioticTypes, request.getAntibioticTypes());
        }
        
        // 治疗设备使用
        if (request.getVentilatorUsed() != null) {
            updateWrapper.set(Patient::getVentilatorUsed, request.getVentilatorUsed());
        }
        if (request.getIcuAdmission() != null) {
            updateWrapper.set(Patient::getIcuAdmission, request.getIcuAdmission());
        }
        
        // 备注
        if (StringUtils.hasText(request.getRemark())) {
            updateWrapper.set(Patient::getRemark, request.getRemark());
        }
        
        // 设置更新信息
        updateWrapper.set(Patient::getUpdatedAt, LocalDateTime.now());
        updateWrapper.set(Patient::getUpdatedBy, updatedBy);
        
        // 执行更新
        patientMapper.update(null, updateWrapper);

        return request.getPatientId();
    }

    @Override
    public void deletePatient(Long patientId, String deletedBy) {
        // 检查患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "患者信息不存在");
        }
        
        // 逻辑删除
        LambdaUpdateWrapper<Patient> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Patient::getPatientId, patientId)
                    .set(Patient::getIsDeleted, 1)
                    .set(Patient::getUpdatedAt, LocalDateTime.now())
                    .set(Patient::getUpdatedBy, deletedBy);
        
        patientMapper.update(null, updateWrapper);
    }

    @Override
    public void batchDeletePatients(Long[] patientIds, String deletedBy) {
        if (patientIds == null || patientIds.length == 0) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR.getErrorCode(), "患者ID列表不能为空");
        }
        
        for (Long patientId : patientIds) {
            // 检查患者是否存在
            Patient patient = patientMapper.selectById(patientId);
            if (patient == null) {
                throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "患者信息不存在: ID=" + patientId);
            }
            
            // 逻辑删除
            LambdaUpdateWrapper<Patient> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Patient::getPatientId, patientId)
                        .set(Patient::getIsDeleted, 1)
                        .set(Patient::getUpdatedAt, LocalDateTime.now())
                        .set(Patient::getUpdatedBy, deletedBy);
            
            patientMapper.update(null, updateWrapper);
        }
    }

    @Override
    public PageResult<PatientInfoResponse> queryPatients(PatientQueryRequest request) {
        // 设置分页参数默认值
        if (request.getPageNum() == null || request.getPageNum() < 1) {
            request.setPageNum(1);
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10);
        }
        
        // 构建分页对象
        Page<Patient> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        
        // 患者ID精确查询
        if (request.getPatientId() != null) {
            queryWrapper.eq(Patient::getPatientId, request.getPatientId());
        }
        
        // 患者编号精确查询
        if (StringUtils.hasText(request.getPatientNumber())) {
            queryWrapper.eq(Patient::getPatientNumber, request.getPatientNumber());
        }
        
        // 性别精确查询
        if (request.getGender() != null) {
            queryWrapper.eq(Patient::getGender, request.getGender());
        }
        
        // 年龄范围查询
        if (request.getMinAge() != null) {
            queryWrapper.ge(Patient::getAge, request.getMinAge());
        }
        if (request.getMaxAge() != null) {
            queryWrapper.le(Patient::getAge, request.getMaxAge());
        }
        
        // 住院日期范围查询
        // 将LocalDateTime转换为LocalDate进行比较，以确保类型匹配
        if (request.getAdmissionDateStart() != null) {
            queryWrapper.ge(Patient::getAdmissionDate, request.getAdmissionDateStart().toLocalDate());
        }
        if (request.getAdmissionDateEnd() != null) {
            queryWrapper.le(Patient::getAdmissionDate, request.getAdmissionDateEnd().toLocalDate());
        }
        
        // 主诉关键词模糊查询
        if (StringUtils.hasText(request.getChiefComplaintKeyword())) {
            queryWrapper.like(Patient::getChiefComplaint, request.getChiefComplaintKeyword());
        }
        
        // ICU入住查询
        if (request.getIcuAdmission() != null) {
            queryWrapper.eq(Patient::getIcuAdmission, request.getIcuAdmission());
        }
        
        // 呼吸机使用查询
        if (request.getVentilatorUsed() != null) {
            queryWrapper.eq(Patient::getVentilatorUsed, request.getVentilatorUsed());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Patient::getCreatedAt);
        
        // 执行分页查询
        IPage<Patient> patientPage = patientMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        List<PatientInfoResponse> responseList = patientPage.getRecords().stream()
                .map(this::convertToPatientInfoResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(
            responseList, 
            patientPage.getTotal(), 
            patientPage.getCurrent(), 
            patientPage.getSize()
        );
    }

    /**
     * 转换Patient实体为PatientInfoResponse
     */
    private PatientInfoResponse convertToPatientInfoResponse(Patient patient) {
        PatientInfoResponse response = new PatientInfoResponse();
        BeanUtils.copyProperties(patient, response);
        return response;
    }
    
    @Override
    public PatientImportResultDTO importPatientsFromExcel(MultipartFile file, String operatorUsername) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR.getErrorCode(), "上传文件不能为空");
        }
        
        // 检查文件格式
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BizException(ResponseCodeEnum.PARAM_ERROR.getErrorCode(), "文件格式不正确，请上传Excel文件(.xlsx或.xls)");
        }
        
        try {
            // 创建监听器
            PatientExcelListener listener = new PatientExcelListener(patientMapper, operatorUsername);
            
            // 读取Excel文件
            EasyExcel.read(file.getInputStream(), PatientExcelImportDTO.class, listener)
                    .registerConverter(new StringConverter())  // 注册自定义转换器
                    .sheet()
                    .headRowNumber(1)  // 指定表头行号
                    .doRead();
            
            // 返回导入结果
            return new PatientImportResultDTO(
                    listener.getSuccessCount(),
                    listener.getErrorCount(),
                    listener.getErrorMessages()
            );
            
        } catch (IOException e) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "读取Excel文件失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "导入数据失败: " + e.getMessage());
        }
    }
}
