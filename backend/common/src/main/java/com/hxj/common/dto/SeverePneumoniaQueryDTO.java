package com.hxj.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重症肺炎诊断分页查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeverePneumoniaQueryDTO extends BasePageQueryDTO {
    
    /**
     * 是否重症肺炎
     */
    private Boolean isSeverePneumonia;
    
    /**
     * 主要标准满足数
     */
    private Integer majorCriteriaCount;
    
    /**
     * 次要标准满足数
     */
    private Integer minorCriteriaCount;
}
