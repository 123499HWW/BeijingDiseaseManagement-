package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxj.common.dto.ImportTaskQueryDTO;
import com.hxj.common.entity.ImportTask;
import com.hxj.common.vo.ImportTaskPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 导入任务Mapper接口
 */
@Mapper
public interface ImportTaskMapper extends BaseMapper<ImportTask> {
    
    /**
     * 分页查询导入任务
     */
    IPage<ImportTaskPageVO> selectImportTaskPage(Page<?> page, @Param("query") ImportTaskQueryDTO query);
}
