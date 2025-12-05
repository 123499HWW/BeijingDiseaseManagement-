package com.hxj.common.dto.patient;

import com.hxj.common.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

/**
 * 患者信息查询请求DTO
 */
@Data
public class PatientQueryRequest {

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 年龄范围 - 最小年龄
     */
    private Integer minAge;

    /**
     * 年龄范围 - 最大年龄
     */
    private Integer maxAge;

    /**
     * 住院日期范围 - 开始日期
     */
    private LocalDate admissionDateStart;

    /**
     * 住院日期范围 - 结束日期
     */
    private LocalDate admissionDateEnd;

    /**
     * 主诉关键词（模糊查询）
     */
    private String chiefComplaintKeyword;

    /**
     * 是否入住ICU
     */
    private Boolean icuAdmission;

    /**
     * 是否应用呼吸机
     */
    private Boolean ventilatorUsed;

    /**
     * 页码，默认第1页
     */
    private Integer pageNum = 1;

    /**
     * 每页大小，默认10条
     */
    private Integer pageSize = 10;
}
