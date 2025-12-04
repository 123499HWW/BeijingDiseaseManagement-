package com.hxj.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxj.common.entity.ImportTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导入任务Mapper接口
 */
@Mapper
public interface ImportTaskMapper extends BaseMapper<ImportTask> {
}
