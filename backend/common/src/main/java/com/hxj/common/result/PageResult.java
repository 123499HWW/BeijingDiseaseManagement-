package com.hxj.common.result;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> {
    
    private List<T> records; // 数据列表
    
    private Long total; // 总记录数
    
    private Long current; // 当前页码
    
    private Long size; // 每页大小
    
    private Long pages; // 总页数
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size; // 计算总页数
    }
}
