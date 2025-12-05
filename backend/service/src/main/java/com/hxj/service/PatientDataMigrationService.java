package com.hxj.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxj.common.entity.Patient;
import com.hxj.common.entity.PatientExaminationRelation;
import com.hxj.common.entity.PhysicalExaminationDetail;
import com.hxj.common.mapper.PatientExaminationRelationMapper;
import com.hxj.common.mapper.PatientMapper;
import com.hxj.common.mapper.PhysicalExaminationDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 患者数据迁移服务
 * 负责将现有患者数据中的体检信息拆分到新的表结构中
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientDataMigrationService {

    private final PatientMapper patientMapper;
    private final PhysicalExaminationDetailMapper physicalExaminationDetailMapper;
    private final PatientExaminationRelationMapper patientExaminationRelationMapper;
    private final PatientExaminationSplitService splitService;

    /**
     * 获取所有已迁移的患者ID列表
     * 
     * @return 已迁移的患者ID列表
     */
    public List<Long> getMigratedPatientIds() {
        LambdaQueryWrapper<PatientExaminationRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatientExaminationRelation::getIsDeleted, 0)
                   .select(PatientExaminationRelation::getPatientId);
        
        List<PatientExaminationRelation> relations = patientExaminationRelationMapper.selectList(queryWrapper);
        return relations.stream()
                       .map(PatientExaminationRelation::getPatientId)
                       .distinct()
                       .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 执行完整的数据迁移
     * 
     * @param createdBy 创建人
     * @return 迁移结果统计
     */
    @Transactional(rollbackFor = Exception.class)
    public MigrationResult migrateAllPatientData(String createdBy) {
        log.info("开始执行患者体检数据迁移，操作人: {}", createdBy);
        
        MigrationResult result = new MigrationResult();
        
        try {
            // 查询所有需要迁移的患者数据
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Patient::getIsDeleted, 0)
                       .and(wrapper -> wrapper.isNotNull(Patient::getPhysicalExamination)
                                             .or()
                                             .isNotNull(Patient::getChestCtReport));
            
            List<Patient> patients = patientMapper.selectList(queryWrapper);
            result.setTotalCount(patients.size());
            
            log.info("找到需要迁移的患者记录数: {}", patients.size());
            
            // 逐个处理患者数据
            for (Patient patient : patients) {
                try {
                    migratePatientData(patient, createdBy);
                    result.incrementSuccessCount();
                } catch (Exception e) {
                    log.error("迁移患者数据失败, patientId: {}", patient.getPatientId(), e);
                    result.incrementFailureCount();
                    result.addErrorMessage(String.format("患者ID %d 迁移失败: %s", 
                                                        patient.getPatientId(), e.getMessage()));
                }
            }
            
            log.info("患者体检数据迁移完成，成功: {}, 失败: {}", 
                    result.getSuccessCount(), result.getFailureCount());
            
        } catch (Exception e) {
            log.error("数据迁移过程中发生异常", e);
            throw new RuntimeException("数据迁移失败", e);
        }
        
        return result;
    }
    
    /**
     * 迁移单个患者的数据
     * 
     * @param patient 患者信息
     * @param createdBy 创建人
     */
    @Transactional(rollbackFor = Exception.class)
    public void migratePatientData(Patient patient, String createdBy) {
        log.debug("开始迁移患者数据, patientId: {}", patient.getPatientId());
        
        // 检查是否已经迁移过
        LambdaQueryWrapper<PatientExaminationRelation> existsWrapper = new LambdaQueryWrapper<>();
        existsWrapper.eq(PatientExaminationRelation::getPatientId, patient.getPatientId())
                    .eq(PatientExaminationRelation::getIsDeleted, 0);
        
        if (patientExaminationRelationMapper.selectCount(existsWrapper) > 0) {
            log.warn("患者 {} 的数据已经迁移过，跳过", patient.getPatientId());
            return;
        }
        
        // 解析体检信息
        PhysicalExaminationDetail detail = splitService.parseCompleteExamination(
            patient.getPhysicalExamination(), 
            patient.getChestCtReport()
        );
        
        // 设置通用字段
        LocalDateTime now = LocalDateTime.now();
        detail.setCreatedAt(now);
        detail.setCreatedBy(createdBy);
        detail.setUpdatedAt(now);
        detail.setUpdatedBy(createdBy);
        detail.setIsDeleted(0);
        
        // 保存体检详细信息
        physicalExaminationDetailMapper.insert(detail);
        
        // 创建关系记录
        PatientExaminationRelation relation = new PatientExaminationRelation();
        relation.setPatientId(patient.getPatientId());
        relation.setDetailId(detail.getDetailId());
        
        // 确定关系类型
        String relationType = determineRelationType(patient.getPhysicalExamination(), 
                                                  patient.getChestCtReport());
        relation.setRelationType(relationType);
        relation.setRemark("系统自动迁移生成");
        
        // 设置通用字段
        relation.setCreatedAt(now);
        relation.setCreatedBy(createdBy);
        relation.setUpdatedAt(now);
        relation.setUpdatedBy(createdBy);
        relation.setIsDeleted(0);
        
        // 保存关系记录
        patientExaminationRelationMapper.insert(relation);
        
        log.debug("患者数据迁移完成, patientId: {}, detailId: {}", 
                 patient.getPatientId(), detail.getDetailId());
    }
    
    /**
     * 确定关系类型
     */
    private String determineRelationType(String physicalExamination, String chestCtReport) {
        boolean hasPhysical = StringUtils.hasText(physicalExamination);
        boolean hasChestCt = StringUtils.hasText(chestCtReport);
        
        if (hasPhysical && hasChestCt) {
            return "COMBINED";
        } else if (hasPhysical) {
            return "PHYSICAL_EXAM";
        } else if (hasChestCt) {
            return "CHEST_CT";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 回滚指定患者的迁移数据
     * 
     * @param patientId 患者ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void rollbackPatientMigration(Long patientId) {
        log.info("开始回滚患者迁移数据, patientId: {}", patientId);
        
        // 查找关系记录
        LambdaQueryWrapper<PatientExaminationRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(PatientExaminationRelation::getPatientId, patientId)
                      .eq(PatientExaminationRelation::getIsDeleted, 0);
        
        List<PatientExaminationRelation> relations = patientExaminationRelationMapper.selectList(relationWrapper);
        
        for (PatientExaminationRelation relation : relations) {
            // 删除体检详细信息
            physicalExaminationDetailMapper.deleteById(relation.getDetailId());
            
            // 删除关系记录
            patientExaminationRelationMapper.deleteById(relation.getRelationId());
        }
        
        log.info("患者迁移数据回滚完成, patientId: {}, 删除记录数: {}", patientId, relations.size());
    }
    
    /**
     * 迁移结果统计类
     */
    public static class MigrationResult {
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
            return String.format("迁移结果: 总数=%d, 成功=%d, 失败=%d", 
                               totalCount, successCount, failureCount);
        }
    }
}
