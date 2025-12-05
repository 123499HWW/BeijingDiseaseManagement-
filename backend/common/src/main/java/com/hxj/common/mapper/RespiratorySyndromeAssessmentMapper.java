package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.RespiratorySyndromeQueryDTO;
import com.hxj.common.entity.RespiratorySyndromeAssessment;
import com.hxj.common.vo.RespiratorySyndromePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 呼吸道症候群评估Mapper接口
 */
@Mapper
public interface RespiratorySyndromeAssessmentMapper extends BaseMapper<RespiratorySyndromeAssessment> {

    /**
     * 根据症候群ID查询评估结果
     */
    RespiratorySyndromeAssessment selectBySyndromeId(@Param("syndromeId") Long syndromeId);

    /**
     * 根据症候群ID列表查询评估结果
     */
    List<RespiratorySyndromeAssessment> selectBySyndromeIds(@Param("syndromeIds") List<Long> syndromeIds);

    /**
     * 批量插入评估结果
     */
    int batchInsert(@Param("list") List<RespiratorySyndromeAssessment> list);

    /**
     * 根据严重程度等级统计数量
     */
    int countBySeverityLevel(@Param("severityLevel") String severityLevel);

    /**
     * 根据日期范围查询评估结果
     */
    List<RespiratorySyndromeAssessment> selectByDateRange(@Param("startDate") String startDate, 
                                                          @Param("endDate") String endDate);
    
    /**
     * 分页查询呼吸道症候群评估结果
     */
    IPage<RespiratorySyndromePageVO> selectRespiratorySyndromePage(Page<?> page, @Param("query") RespiratorySyndromeQueryDTO query);
}
