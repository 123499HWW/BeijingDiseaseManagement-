package com.hxj.service;

import com.hxj.common.entity.PhysicalExaminationDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 患者体检信息拆分服务
 * 负责将patient_info表中的physical_examination和chest_ct_report字段按规则拆分
 */
@Slf4j
@Service
public class PatientExaminationSplitService {

    /**
     * 解析体格检查信息
     * 规则：根据"。"进行分割拆解，第二段包含"T,P,R,BP,SpO2"信息需要进一步拆分
     * 
     * @param physicalExamination 体格检查原始字符串
     * @return 解析后的体检详细信息对象
     */
    public PhysicalExaminationDetail parsePhysicalExamination(String physicalExamination) {
        PhysicalExaminationDetail detail = new PhysicalExaminationDetail();
        
        if (!StringUtils.hasText(physicalExamination)) {
            return detail;
        }
        
        try {
            // 按"。"分割
            String[] segments = physicalExamination.split("。");
            
            // 第一段：一般状况
            if (segments.length > 0) {
                detail.setGeneralCondition(segments[0].trim());
            }
            
            // 第二段：生命体征 (T,P,R,BP,SpO2)
            if (segments.length > 1) {
                parseVitalSigns(segments[1].trim(), detail);
            }
            
            // 第三段：其他检查
            if (segments.length > 2) {
                detail.setOtherExamination(segments[2].trim());
            }
            
            // 第四段：额外检查（如果存在）
            if (segments.length > 3) {
                StringBuilder additional = new StringBuilder();
                for (int i = 3; i < segments.length; i++) {
                    if (i > 3) additional.append("。");
                    additional.append(segments[i].trim());
                }
                detail.setAdditionalExamination(additional.toString());
            }
            
        } catch (Exception e) {
            log.error("解析体格检查信息失败: {}", physicalExamination, e);
        }
        
        return detail;
    }
    
    /**
     * 解析生命体征信息
     * 格式示例：T:36.5℃，P:80次/分，R:20次/分，BP:120/80mmHg，SpO2:98%
     * 
     * @param vitalSignsText 生命体征文本
     * @param detail 体检详细信息对象
     */
    private void parseVitalSigns(String vitalSignsText, PhysicalExaminationDetail detail) {
        if (!StringUtils.hasText(vitalSignsText)) {
            return;
        }
        
        try {
            // 按"，"分割生命体征
            String[] vitalSigns = vitalSignsText.split("[，,]");
            
            for (String vitalSign : vitalSigns) {
                String trimmed = vitalSign.trim();
                
                // 解析体温 T
                if (trimmed.toUpperCase().startsWith("T")) {
                    parseTemperature(trimmed, detail);
                }
                // 解析脉搏 P
                else if (trimmed.toUpperCase().startsWith("P")) {
                    parsePulse(trimmed, detail);
                }
                // 解析呼吸 R
                else if (trimmed.toUpperCase().startsWith("R")) {
                    parseRespiration(trimmed, detail);
                }
                // 解析血压 BP
                else if (trimmed.toUpperCase().startsWith("BP")) {
                    parseBloodPressure(trimmed, detail);
                }
                // 解析血氧饱和度 SpO2
                else if (trimmed.toUpperCase().contains("SPO2") || trimmed.toUpperCase().contains("SO2")) {
                    parseSpO2(trimmed, detail);
                }
            }
            
        } catch (Exception e) {
            log.error("解析生命体征失败: {}", vitalSignsText, e);
        }
    }
    
    /**
     * 解析体温
     */
    private void parseTemperature(String tempText, PhysicalExaminationDetail detail) {
        try {
            Pattern pattern = Pattern.compile("T[：:]?\\s*(\\d+\\.?\\d*)");
            Matcher matcher = pattern.matcher(tempText);
            if (matcher.find()) {
                detail.setTemperature(new BigDecimal(matcher.group(1)));
            }
        } catch (Exception e) {
            log.warn("解析体温失败: {}", tempText, e);
        }
    }
    
    /**
     * 解析脉搏
     */
    private void parsePulse(String pulseText, PhysicalExaminationDetail detail) {
        try {
            Pattern pattern = Pattern.compile("P[：:]?\\s*(\\d+)");
            Matcher matcher = pattern.matcher(pulseText);
            if (matcher.find()) {
                detail.setPulse(Integer.parseInt(matcher.group(1)));
            }
        } catch (Exception e) {
            log.warn("解析脉搏失败: {}", pulseText, e);
        }
    }
    
    /**
     * 解析呼吸
     */
    private void parseRespiration(String respText, PhysicalExaminationDetail detail) {
        try {
            Pattern pattern = Pattern.compile("R[：:]?\\s*(\\d+)");
            Matcher matcher = pattern.matcher(respText);
            if (matcher.find()) {
                detail.setRespiration(Integer.parseInt(matcher.group(1)));
            }
        } catch (Exception e) {
            log.warn("解析呼吸失败: {}", respText, e);
        }
    }
    
    /**
     * 解析血压
     * BP字段根据"/"分割，前面是收缩压(SBP)，后面是舒张压(DBP)
     */
    private void parseBloodPressure(String bpText, PhysicalExaminationDetail detail) {
        try {
            Pattern pattern = Pattern.compile("BP[：:]?\\s*(\\d+)/(\\d+)");
            Matcher matcher = pattern.matcher(bpText);
            if (matcher.find()) {
                detail.setSystolicBp(Integer.parseInt(matcher.group(1)));
                detail.setDiastolicBp(Integer.parseInt(matcher.group(2)));
            }
        } catch (Exception e) {
            log.warn("解析血压失败: {}", bpText, e);
        }
    }
    
    /**
     * 解析血氧饱和度
     */
    private void parseSpO2(String spo2Text, PhysicalExaminationDetail detail) {
        try {
            Pattern pattern = Pattern.compile("SpO2[：:]?\\s*(\\d+\\.?\\d*)");
            Matcher matcher = pattern.matcher(spo2Text);
            if (matcher.find()) {
                detail.setSpo2(new BigDecimal(matcher.group(1)));
            }
        } catch (Exception e) {
            log.warn("解析血氧饱和度失败: {}", spo2Text, e);
        }
    }
    
    /**
     * 解析胸部CT报告
     * 规则：根据"【】"分别分割成【检查方法】、【影像所见】和【诊断意见】字段
     * 
     * @param chestCtReport 胸部CT报告原始字符串
     * @param detail 体检详细信息对象
     */
    public void parseChestCtReport(String chestCtReport, PhysicalExaminationDetail detail) {
        if (!StringUtils.hasText(chestCtReport)) {
            return;
        }
        
        try {
            // 解析【检查方法】
            String examinationMethod = extractBracketContent(chestCtReport, "检查方法");
            if (StringUtils.hasText(examinationMethod)) {
                detail.setCtExaminationMethod(examinationMethod);
            }
            
            // 解析【影像所见】
            String imagingFindings = extractBracketContent(chestCtReport, "影像所见");
            if (StringUtils.hasText(imagingFindings)) {
                detail.setCtImagingFindings(imagingFindings);
            }
            
            // 解析【诊断意见】
            String diagnosisOpinion = extractBracketContent(chestCtReport, "诊断意见");
            if (StringUtils.hasText(diagnosisOpinion)) {
                detail.setCtDiagnosisOpinion(diagnosisOpinion);
            }
            
        } catch (Exception e) {
            log.error("解析胸部CT报告失败: {}", chestCtReport, e);
        }
    }
    
    /**
     * 从文本中提取指定标题的【】括号内容
     * 
     * @param text 原始文本
     * @param title 要提取的标题
     * @return 提取的内容
     */
    private String extractBracketContent(String text, String title) {
        try {
            // 匹配【标题】后面的内容，直到下一个【】或文本结束
            Pattern pattern = Pattern.compile("【" + title + "】([^【]*)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            log.warn("提取括号内容失败: title={}, text={}", title, text, e);
        }
        return null;
    }
    
    /**
     * 完整解析患者体检信息
     * 
     * @param physicalExamination 体格检查字符串
     * @param chestCtReport 胸部CT报告字符串
     * @return 解析后的体检详细信息
     */
    public PhysicalExaminationDetail parseCompleteExamination(String physicalExamination, String chestCtReport) {
        PhysicalExaminationDetail detail = parsePhysicalExamination(physicalExamination);
        parseChestCtReport(chestCtReport, detail);
        return detail;
    }
}
