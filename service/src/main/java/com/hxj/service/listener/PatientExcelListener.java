package com.hxj.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.hxj.common.dto.patient.PatientExcelImportDTO;
import com.hxj.common.entity.Patient;
import com.hxj.common.enums.Gender;
import com.hxj.common.mapper.PatientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 患者信息Excel导入监听器
 */
@Slf4j
public class PatientExcelListener implements ReadListener<PatientExcelImportDTO> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    
    private final PatientMapper patientMapper;
    private final String operatorUsername;
    
    /**
     * 缓存的数据
     */
    private List<PatientExcelImportDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    
    /**
     * 导入结果统计
     */
    private int successCount = 0;
    private int errorCount = 0;
    private List<String> errorMessages = ListUtils.newArrayList();

    public PatientExcelListener(PatientMapper patientMapper, String operatorUsername) {
        this.patientMapper = patientMapper;
        this.operatorUsername = operatorUsername;
    }

    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(PatientExcelImportDTO data, AnalysisContext context) {
        log.info("解析到一条数据:{}", data);
        
        // 设置行号用于错误定位
        data.setRowIndex(String.valueOf(context.readRowHolder().getRowIndex() + 1));
        
        // 数据验证
        String validationError = validateData(data);
        if (validationError != null) {
            errorCount++;
            errorMessages.add("第" + data.getRowIndex() + "行: " + validationError);
            return;
        }
        
        cachedDataList.add(data);
        
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！成功导入{}条，失败{}条", successCount, errorCount);
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        
        for (PatientExcelImportDTO excelData : cachedDataList) {
            try {
                Patient patient = convertToPatient(excelData);
                patientMapper.insert(patient);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                errorMessages.add("第" + excelData.getRowIndex() + "行: 保存失败 - " + e.getMessage());
                log.error("保存患者数据失败，行号：{}", excelData.getRowIndex(), e);
            }
        }
        
        log.info("存储数据库成功！");
    }
    
    /**
     * 数据验证
     */
    private String validateData(PatientExcelImportDTO data) {
        // 验证就诊次数
        if (!StringUtils.hasText(data.getVisitCount())) {
            return "就诊次数不能为空";
        }
        try {
            int visitCount = Integer.parseInt(data.getVisitCount().trim());
            if (visitCount <= 0) {
                return "就诊次数必须大于0";
            }
        } catch (NumberFormatException e) {
            return "就诊次数格式不正确，必须是数字";
        }
        
        // 验证住院日期
        if (!StringUtils.hasText(data.getAdmissionDate())) {
            return "住院日期不能为空";
        }
        try {
            parseDate(data.getAdmissionDate());
        } catch (Exception e) {
            return "住院日期格式不正确，请使用yyyy-MM-dd格式";
        }
        
        // 验证性别
        if (!StringUtils.hasText(data.getGender())) {
            return "性别不能为空";
        }
        
        if (!"男".equals(data.getGender().trim()) && !"女".equals(data.getGender().trim())) {
            return "性别只能是'男'或'女'";
        }
        
        // 验证年龄
        if (!StringUtils.hasText(data.getAge())) {
            return "年龄不能为空";
        }
        try {
            int age = Integer.parseInt(data.getAge().trim());
            if (age <= 0 || age > 150) {
                return "年龄必须在1-150之间";
            }
        } catch (NumberFormatException e) {
            return "年龄格式不正确，必须是数字";
        }
        
        if (!StringUtils.hasText(data.getChiefComplaint())) {
            return "主诉不能为空";
        }
        
        if (!StringUtils.hasText(data.getPresentIllness())) {
            return "现病史不能为空";
        }
        
        if (!StringUtils.hasText(data.getPhysicalExamination())) {
            return "查体不能为空";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 将Excel数据转换为Patient实体
     */
    private Patient convertToPatient(PatientExcelImportDTO excelData) {
        Patient patient = new Patient();
        
        // 基本信息
        patient.setVisitCount(Integer.parseInt(excelData.getVisitCount().trim()));
        patient.setAdmissionDate(parseDate(excelData.getAdmissionDate()));
        patient.setGender("男".equals(excelData.getGender().trim()) ? Gender.MALE : Gender.FEMALE);
        patient.setAge(Integer.parseInt(excelData.getAge().trim()));
        patient.setChiefComplaint(excelData.getChiefComplaint());
        patient.setPresentIllness(excelData.getPresentIllness());
        patient.setPhysicalExamination(excelData.getPhysicalExamination());
        
        // 动脉血气分析
        if (StringUtils.hasText(excelData.getArterialBloodGasPh())) {
            patient.setArterialPh(parseDecimal(excelData.getArterialBloodGasPh()));
        }
        if (StringUtils.hasText(excelData.getArterialBloodGasPo2())) {
            patient.setArterialPo2(parseDecimal(excelData.getArterialBloodGasPo2()));
        }
        if (StringUtils.hasText(excelData.getArterialBloodGasOxygenationIndex())) {
            patient.setArterialOxygenationIndex(parseDecimal(excelData.getArterialBloodGasOxygenationIndex()));
        }
        if (StringUtils.hasText(excelData.getArterialBloodGasPco2())) {
            patient.setArterialPco2(parseDecimal(excelData.getArterialBloodGasPco2()));
        }
        
        // 血液检查
        if (StringUtils.hasText(excelData.getPlateletCount())) {
            patient.setPlateletCount(parseDecimal(excelData.getPlateletCount()));
        }
        if (StringUtils.hasText(excelData.getBloodUreaNitrogen())) {
            patient.setBloodUreaNitrogen(parseDecimal(excelData.getBloodUreaNitrogen()));
        }
        if (StringUtils.hasText(excelData.getSerumCreatinine())) {
            patient.setSerumCreatinine(parseDecimal(excelData.getSerumCreatinine()));
        }
        if (StringUtils.hasText(excelData.getTotalBilirubin())) {
            patient.setTotalBilirubin(parseDecimal(excelData.getTotalBilirubin()));
        }
        
        // 影像学检查
        patient.setChestCtOrdered(convertYesNoToBoolean(excelData.getChestCtOrdered()));
        patient.setChestCtReport(excelData.getChestCtReport());
        
        // 药物使用
        patient.setDopamineUsed(convertYesNoToBoolean(excelData.getDopamineUsed()));
        patient.setDobutamineUsed(convertYesNoToBoolean(excelData.getDobutamineUsed()));
        patient.setNorepinephrineUsed(convertYesNoToBoolean(excelData.getNorepinephrineUsed()));
        patient.setVasoactiveDrugsUsed(convertYesNoToBoolean(excelData.getVasoactiveDrugUsed()));
        patient.setSpecialAntibioticsUsed(convertYesNoToBoolean(excelData.getSpecialAntibioticUsed()));
        patient.setAntibioticTypes(excelData.getAntibioticType());
        
        // 治疗措施
        patient.setVentilatorUsed(convertYesNoToBoolean(excelData.getVentilatorUsed()));
        patient.setIcuAdmission(convertYesNoToBoolean(excelData.getIcuAdmission()));
        
        // 审计字段
        LocalDateTime now = LocalDateTime.now();
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);
        patient.setCreatedBy(operatorUsername);
        patient.setUpdatedBy(operatorUsername);
        patient.setIsDeleted(0);
        
        return patient;
    }
    
    /**
     * 将"是"/"否"转换为Boolean
     */
    private Boolean convertYesNoToBoolean(String yesNo) {
        if ("是".equals(yesNo)) {
            return true;
        } else if ("否".equals(yesNo)) {
            return false;
        }
        return null;
    }
    
    // Getter方法用于获取导入结果
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    /**
     * 解析数值字符串为BigDecimal
     */
    private BigDecimal parseDecimal(String numberStr) {
        if (!StringUtils.hasText(numberStr)) {
            return null;
        }
        
        try {
            String trimmedNumber = numberStr.trim();
            return new BigDecimal(trimmedNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("数值格式错误: " + numberStr);
        }
    }
    
    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        
        try {
            // 尝试多种日期格式
            DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("yyyy/M/d")
            };
            
            String trimmedDate = dateStr.trim();
            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDate.parse(trimmedDate, formatter);
                } catch (DateTimeParseException ignored) {
                    // 继续尝试下一个格式
                }
            }
            
            throw new IllegalArgumentException("无法解析日期格式: " + dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("日期格式错误: " + dateStr);
        }
    }
}
