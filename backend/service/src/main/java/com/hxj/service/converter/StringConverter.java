package com.hxj.service.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * 自定义String转换器，处理各种类型的Excel单元格数据
 */
public class StringConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null; // 支持所有类型
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                   GlobalConfiguration globalConfiguration) {
        if (cellData == null) {
            return null;
        }
        
        try {
            // 根据不同的单元格类型进行转换
            switch (cellData.getType()) {
                case STRING:
                    return cellData.getStringValue();
                case NUMBER:
                    // 数字类型转为字符串，去除科学计数法
                    return cellData.getNumberValue().toPlainString();
                case BOOLEAN:
                    return cellData.getBooleanValue().toString();
                case ERROR:
                    return null;
                case EMPTY:
                    return null;
                default:
                    return cellData.getStringValue();
            }
        } catch (Exception e) {
            // 如果转换失败，返回空字符串而不是抛出异常
            return "";
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty,
                                              GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value);
    }
}
