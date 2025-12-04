package com.hxj.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxj.common.dto.message.ImportTaskMessageDTO;
import com.hxj.common.dto.message.PatientDataMessageDTO;
import com.hxj.common.dto.patient.PatientExcelImportDTO;
import com.hxj.common.entity.ImportTask;
import com.hxj.common.entity.ImportTaskDetail;
import com.hxj.common.entity.Patient;
import com.hxj.common.enums.Gender;
import com.hxj.common.mapper.ImportTaskDetailMapper;
import com.hxj.common.mapper.ImportTaskMapper;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.service.message.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导入任务服务
 * 负责处理Excel导入任务的创建、执行和结果记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportTaskService {

    private final ImportTaskMapper importTaskMapper;
    private final ImportTaskDetailMapper importTaskDetailMapper;
    private final PatientMapper patientMapper;
    private final MessageProducerService messageProducerService;
    private final ObjectMapper objectMapper;

    /**
     * 创建导入任务
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportTask createImportTask(String importType, String filePath, String fileName, 
                                      Long fileSize, String createdBy) {
        ImportTask task = new ImportTask();
        task.setTaskNumber(ImportTask.generateTaskNumber());
        task.setTaskName("Excel数据导入 - " + fileName);
        task.setImportType(importType);
        task.setFileName(fileName);
        task.setFilePath(filePath);
        task.setFileSize(fileSize);
        task.setTaskStatus("PENDING");
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailureCount(0);
        task.setSkipCount(0);
        task.setCreatedBy(createdBy);
        task.setUpdatedBy(createdBy);
        task.setIsDeleted(0);
        
        // 手动设置时间字段（确保不为null）
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        importTaskMapper.insert(task);
        
        log.info("创建导入任务成功: taskId={}, taskNumber={}", task.getTaskId(), task.getTaskNumber());
        return task;
    }

    /**
     * 发送导入任务到消息队列
     */
    public void sendImportTaskToQueue(ImportTask task, String userId, String userName) {
        ImportTaskMessageDTO messageDTO = new ImportTaskMessageDTO(
            task.getTaskId(), task.getImportType(), task.getFilePath(), 
            task.getFileName(), userId, userName);
        messageDTO.setTaskNumber(task.getTaskNumber());
        messageDTO.setFileSize(task.getFileSize());
        
        messageProducerService.sendPatientImportMessage(messageDTO);
        
        log.info("导入任务已发送到消息队列: taskId={}", task.getTaskId());
    }

    /**
     * 执行导入任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeImportTask(ImportTaskMessageDTO messageDTO) {
        Long taskId = messageDTO.getTaskId();
        log.info("开始执行导入任务: taskId={}", taskId);

        ImportTask task = importTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("导入任务不存在: " + taskId);
        }

        // 更新任务状态为处理中
        task.setTaskStatus("PROCESSING");
        task.setStartTime(LocalDateTime.now());
        importTaskMapper.updateById(task);

        try {
            // 根据导入类型执行不同的导入逻辑
            log.info("准备执行导入任务，导入类型: {}", messageDTO.getImportType());
            switch (messageDTO.getImportType()) {
                case "PATIENT_DATA":
                case "PATIENT":  // 添加PATIENT类型支持
                    executePatientDataImport(task, messageDTO);
                    break;
                default:
                    throw new RuntimeException("不支持的导入类型: " + messageDTO.getImportType());
            }

            // 更新任务状态为完成
            task.setTaskStatus("COMPLETED");
            task.setEndTime(LocalDateTime.now());
            task.setDuration(task.getEndTime().toEpochSecond(java.time.ZoneOffset.UTC) - 
                           task.getStartTime().toEpochSecond(java.time.ZoneOffset.UTC));
            importTaskMapper.updateById(task);

            log.info("导入任务执行完成: taskId={}, 总数={}, 成功={}, 失败={}", 
                    taskId, task.getTotalCount(), task.getSuccessCount(), task.getFailureCount());

            // 发送完成通知
            messageProducerService.sendDataProcessCompleteNotification(
                messageDTO.getUserId(), messageDTO.getUserName(), 
                "Excel数据导入", task.getTotalCount(), task.getSuccessCount());

            // 如果是患者数据导入且有成功记录，自动触发数据迁移
            if (("PATIENT".equals(messageDTO.getImportType()) || "PATIENT_DATA".equals(messageDTO.getImportType())) 
                    && task.getSuccessCount() > 0) {
                log.info("患者数据导入完成，开始触发数据迁移: taskId={}, successCount={}", 
                        taskId, task.getSuccessCount());
                
                // 获取本次导入的患者ID列表
                List<Long> patientIds = getImportedPatientIds(taskId);
                
                // 创建包含患者ID列表的消息DTO
                PatientDataMessageDTO migrationMessage = new PatientDataMessageDTO(
                    messageDTO.getUserId(), messageDTO.getUserName(), 
                    "导入后自动迁移", task.getSuccessCount());
                migrationMessage.setTaskId(taskId); // 传递taskId，便于后续获取患者ID列表
                
                // 发送患者数据迁移消息到队列（迁移完成后会自动触发综合评估）
                messageProducerService.sendPatientMigrationMessage(migrationMessage);
                
                log.info("已发送数据迁移消息，包含患者数量: {}, taskId: {}", 
                        patientIds.size(), taskId);
            }

        } catch (Exception e) {
            log.error("导入任务执行失败: taskId={}", taskId, e);
            
            // 更新任务状态为失败
            task.setTaskStatus("FAILED");
            task.setEndTime(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            importTaskMapper.updateById(task);
            
            throw e;
        }
    }

    /**
     * 执行患者数据导入
     */
    private void executePatientDataImport(ImportTask task, ImportTaskMessageDTO messageDTO) {
        File file = new File(messageDTO.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("导入文件不存在: " + messageDTO.getFilePath());
        }

        PatientImportListener listener = new PatientImportListener(task, messageDTO.getUserId());
        
        EasyExcel.read(file, PatientExcelImportDTO.class, listener)
                .sheet()
                .doRead();

        // 更新任务统计信息
        task.setTotalCount(listener.getTotalCount());
        task.setSuccessCount(listener.getSuccessCount());
        task.setFailureCount(listener.getFailureCount());
        task.setSkipCount(listener.getSkipCount());
    }

    /**
     * 患者数据导入监听器
     */
    private class PatientImportListener extends AnalysisEventListener<PatientExcelImportDTO> {
        
        private final ImportTask task;
        private final String userId;
        private final List<PatientExcelImportDTO> cachedDataList = new ArrayList<>();
        private final int BATCH_COUNT = 100;
        
        private int totalCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private int skipCount = 0;

        public PatientImportListener(ImportTask task, String userId) {
            this.task = task;
            this.userId = userId;
        }

        @Override
        public void invoke(PatientExcelImportDTO data, AnalysisContext context) {
            totalCount++;
            data.setRowIndex(String.valueOf(context.readRowHolder().getRowIndex() + 1));
            cachedDataList.add(data);
            
            if (cachedDataList.size() >= BATCH_COUNT) {
                saveData();
                cachedDataList.clear();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            saveData();
            log.info("患者数据导入解析完成，总数: {}", totalCount);
        }

        private void saveData() {
            log.info("开始批量保存数据，本批次数量: {}", cachedDataList.size());
            
            for (PatientExcelImportDTO dto : cachedDataList) {
                ImportTaskDetail detail = new ImportTaskDetail();
                detail.setTaskId(task.getTaskId());
                detail.setRowNumber(Integer.parseInt(dto.getRowIndex()));
                detail.setProcessTime(LocalDateTime.now());
                detail.setCreatedBy(userId);
                detail.setUpdatedBy(userId);
                detail.setIsDeleted(0);
                
                // 手动设置时间字段
                LocalDateTime now = LocalDateTime.now();
                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);

                try {
                    log.debug("处理第{}行数据: {}", dto.getRowIndex(), dto.getPatientNumber());
                    
                    // 保存原始数据
                    detail.setOriginalData(objectMapper.writeValueAsString(dto));
                    
                    // 验证和转换数据
                    Patient patient = convertToPatient(dto);
                    
                    // 手动设置Patient的时间字段
                    patient.setCreatedAt(now);
                    patient.setUpdatedAt(now);
                    patient.setCreatedBy(userId);
                    patient.setUpdatedBy(userId);
                    patient.setIsDeleted(0);
                    
                    // 保存患者数据
                    patientMapper.insert(patient);
                    
                    // 记录成功
                    detail.setProcessStatus("SUCCESS");
                    detail.setProcessedDataId(patient.getPatientId());
                    successCount++;
                    
                    log.debug("第{}行数据保存成功，患者ID: {}", dto.getRowIndex(), patient.getPatientId());
                    
                } catch (Exception e) {
                    log.error("导入第{}行数据失败: {}, 错误详情: ", dto.getRowIndex(), e.getMessage(), e);
                    
                    // 记录失败
                    detail.setProcessStatus("FAILED");
                    detail.setErrorMessage(e.getMessage());
                    failureCount++;
                }
                
                try {
                    importTaskDetailMapper.insert(detail);
                } catch (Exception e) {
                    log.error("保存导入详情失败: {}", e.getMessage(), e);
                }
            }
            
            log.info("批量保存完成，成功: {}, 失败: {}", successCount, failureCount);
        }

        private Patient convertToPatient(PatientExcelImportDTO dto) {
            Patient patient = new Patient();
            
            // 患者编号
            if (StringUtils.hasText(dto.getPatientNumber())) {
                patient.setPatientNumber(dto.getPatientNumber());
            }
            
            // 就诊次数
            if (StringUtils.hasText(dto.getVisitCount())) {
                patient.setVisitCount(Integer.parseInt(dto.getVisitCount()));
            }
            
            // 住院日期
            if (StringUtils.hasText(dto.getAdmissionDate())) {
                patient.setAdmissionDate(LocalDate.parse(dto.getAdmissionDate(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            
            // 性别
            if (StringUtils.hasText(dto.getGender())) {
                patient.setGender("男".equals(dto.getGender()) ? Gender.MALE : Gender.FEMALE);
            }
            
            // 年龄
            if (StringUtils.hasText(dto.getAge())) {
                patient.setAge(Integer.parseInt(dto.getAge()));
            }
            
            // 主诉
            patient.setChiefComplaint(dto.getChiefComplaint());
            
            // 现病史
            patient.setPresentIllness(dto.getPresentIllness());
            
            // 查体
            patient.setPhysicalExamination(dto.getPhysicalExamination());
            
            // ==================== 动脉血气指标 ====================
            if (StringUtils.hasText(dto.getArterialBloodGasPh())) {
                patient.setArterialPh(new BigDecimal(dto.getArterialBloodGasPh()));
            }
            if (StringUtils.hasText(dto.getArterialBloodGasPo2())) {
                patient.setArterialPo2(new BigDecimal(dto.getArterialBloodGasPo2()));
            }
            if (StringUtils.hasText(dto.getArterialBloodGasOxygenationIndex())) {
                patient.setArterialOxygenationIndex(new BigDecimal(dto.getArterialBloodGasOxygenationIndex()));
            }
            if (StringUtils.hasText(dto.getArterialBloodGasPco2())) {
                patient.setArterialPco2(new BigDecimal(dto.getArterialBloodGasPco2()));
            }
            
            // ==================== 血液检查指标 ====================
            if (StringUtils.hasText(dto.getPlateletCount())) {
                patient.setPlateletCount(new BigDecimal(dto.getPlateletCount()));
            }
            if (StringUtils.hasText(dto.getBloodUreaNitrogen())) {
                patient.setBloodUreaNitrogen(new BigDecimal(dto.getBloodUreaNitrogen()));
            }
            if (StringUtils.hasText(dto.getSerumCreatinine())) {
                patient.setSerumCreatinine(new BigDecimal(dto.getSerumCreatinine()));
            }
            if (StringUtils.hasText(dto.getTotalBilirubin())) {
                patient.setTotalBilirubin(new BigDecimal(dto.getTotalBilirubin()));
            }
            
            // ==================== 胸部CT相关 ====================
            if (StringUtils.hasText(dto.getChestCtOrdered())) {
                patient.setChestCtOrdered("是".equals(dto.getChestCtOrdered()));
            }
            patient.setChestCtReport(dto.getChestCtReport());
            
            // ==================== 药物使用情况 ====================
            if (StringUtils.hasText(dto.getDopamineUsed())) {
                patient.setDopamineUsed("是".equals(dto.getDopamineUsed()));
            }
            if (StringUtils.hasText(dto.getDobutamineUsed())) {
                patient.setDobutamineUsed("是".equals(dto.getDobutamineUsed()));
            }
            if (StringUtils.hasText(dto.getNorepinephrineUsed())) {
                patient.setNorepinephrineUsed("是".equals(dto.getNorepinephrineUsed()));
            }
            if (StringUtils.hasText(dto.getVasoactiveDrugUsed())) {
                patient.setVasoactiveDrugsUsed("是".equals(dto.getVasoactiveDrugUsed()));
            }
            if (StringUtils.hasText(dto.getSpecialAntibioticUsed())) {
                patient.setSpecialAntibioticsUsed("是".equals(dto.getSpecialAntibioticUsed()));
            }
            patient.setAntibioticTypes(dto.getAntibioticType());
            
            // ==================== 治疗设备使用 ====================
            if (StringUtils.hasText(dto.getVentilatorUsed())) {
                patient.setVentilatorUsed("是".equals(dto.getVentilatorUsed()));
            }
            if (StringUtils.hasText(dto.getIcuAdmission())) {
                patient.setIcuAdmission("是".equals(dto.getIcuAdmission()));
            }
            
            // 设置通用字段
            patient.setCreatedBy(userId);
            patient.setUpdatedBy(userId);
            patient.setIsDeleted(0);
            
            return patient;
        }

        // Getters
        public int getTotalCount() { return totalCount; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getSkipCount() { return skipCount; }
    }

    /**
     * 查询导入任务
     */
    public ImportTask getImportTask(Long taskId) {
        return importTaskMapper.selectById(taskId);
    }

    /**
     * 查询导入任务列表
     */
    public List<ImportTask> getImportTaskList(String createdBy, String taskStatus) {
        LambdaQueryWrapper<ImportTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportTask::getIsDeleted, 0);
        
        if (StringUtils.hasText(createdBy)) {
            queryWrapper.eq(ImportTask::getCreatedBy, createdBy);
        }
        
        if (StringUtils.hasText(taskStatus)) {
            queryWrapper.eq(ImportTask::getTaskStatus, taskStatus);
        }
        
        queryWrapper.orderByDesc(ImportTask::getCreatedAt);
        
        return importTaskMapper.selectList(queryWrapper);
    }

    /**
     * 查询导入任务详情
     */
    public List<ImportTaskDetail> getImportTaskDetails(Long taskId) {
        LambdaQueryWrapper<ImportTaskDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportTaskDetail::getTaskId, taskId)
                   .eq(ImportTaskDetail::getIsDeleted, 0)
                   .orderByAsc(ImportTaskDetail::getRowNumber);
        
        return importTaskDetailMapper.selectList(queryWrapper);
    }
    
    /**
     * 获取导入成功的患者ID列表
     */
    public List<Long> getImportedPatientIds(Long taskId) {
        LambdaQueryWrapper<ImportTaskDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportTaskDetail::getTaskId, taskId)
                   .eq(ImportTaskDetail::getProcessStatus, "SUCCESS")
                   .eq(ImportTaskDetail::getIsDeleted, 0)
                   .isNotNull(ImportTaskDetail::getProcessedDataId);
        
        List<ImportTaskDetail> successDetails = importTaskDetailMapper.selectList(queryWrapper);
        
        return successDetails.stream()
                .map(ImportTaskDetail::getProcessedDataId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toList());
    }
}
