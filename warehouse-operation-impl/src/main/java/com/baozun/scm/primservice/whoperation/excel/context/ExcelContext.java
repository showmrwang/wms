package com.baozun.scm.primservice.whoperation.excel.context;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.baozun.scm.primservice.whoperation.excel.ExcelExport;
import com.baozun.scm.primservice.whoperation.excel.ExcelHeader;
import com.baozun.scm.primservice.whoperation.excel.ExcelImport;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelExportResult;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.excel.util.ReflectUtil;
import com.baozun.scm.primservice.whoperation.excel.vo.ExcelDefinition;
import com.baozun.scm.primservice.whoperation.excel.vo.FieldValue;
import com.baozun.scm.primservice.whoperation.excel.xml.ExcelDefinitionReader;

public abstract class ExcelContext {

    private ExcelDefinitionReader definitionReader;

    /** 用于缓存Excel配置 */
    private Map<String, List<FieldValue>> fieldValueMap = new HashMap<String, List<FieldValue>>();
    /** 导出 */
    private ExcelExport excelExport;
    /** 导入 */
    private ExcelImport excelImport;


    @SuppressWarnings("unused")
    private ExcelContext() {

    }
    /**
     * @param location 配置文件类路径
     */
    protected ExcelContext(String location) {
        try {
            definitionReader = new ExcelDefinitionReader(location);
            excelExport = new ExcelExport(definitionReader);
            excelImport = new ExcelImport(definitionReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建Excel
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @return Workbook
     * @throws Exception
     */
    public Workbook createExcel(String id, List<?> beans) throws Exception {
        return createExcel(id, beans, null, null);
    }

    /**
     * 创建Excel部分信息
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @return Workbook
     * @throws Exception
     */
    public ExcelExportResult createExcelForPart(String id, List<?> beans) throws Exception {
        return createExcelForPart(id, beans, null, null);
    }

    /**
     * 创建Excel
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @param header 导出之前,在标题前面做出一些额外的操作，比如增加文档描述等,可以为null
     * @return Workbook
     * @throws Exception
     */
    public Workbook createExcel(String id, List<?> beans, ExcelHeader header) throws Exception {
        return createExcel(id, beans, header, null);
    }

    /**
     * 创建Excel部分信息
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @param header 导出之前,在标题前面做出一些额外的操作，比如增加文档描述等,可以为null
     * @return Workbook
     * @throws Exception
     */
    public ExcelExportResult createExcelForPart(String id, List<?> beans, ExcelHeader header) throws Exception {
        return createExcelForPart(id, beans, header, null);
    }

    /**
     * 创建Excel
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @param header 导出之前,在标题前面做出一些额外的操作,比如增加文档描述等,可以为null
     * @param fields 指定Excel导出的字段(bean对应的字段名称),可以为null
     * @return Workbook
     * @throws Exception
     */
    public Workbook createExcel(String id, List<?> beans, ExcelHeader header, List<String> fields) throws Exception {
        return excelExport.createExcel(id, beans, header, fields).build();
    }

    /**
     * 创建Excel部分信息
     * 
     * @param id 配置ID
     * @param beans 配置class对应的List
     * @param header 导出之前,在标题前面做出一些额外的操作,比如增加文档描述等,可以为null
     * @param fields 指定Excel导出的字段(bean对应的字段名称),可以为null
     * @return Workbook
     * @throws Exception
     */
    public ExcelExportResult createExcelForPart(String id, List<?> beans, ExcelHeader header, List<String> fields) throws Exception {
        return excelExport.createExcel(id, beans, header, fields);
    }

    /**
     * 创建Excel,模板信息
     * 
     * @param id ExcelXML配置Bean的ID
     * @param header Excel头信息(在标题之前)
     * @param fields 指定导出的字段
     * @return
     * @throws Exception
     */
    public Workbook createExcelTemplate(String id, ExcelHeader header, List<String> fields) throws Exception {
        return excelExport.createExcelTemplate(id, header, fields);
    }

    /***
     * 读取Excel信息
     * 
     * @param id 配置ID
     * @param excelStream Excel文件流
     * @param outStream 校验异常EXCEL文件输出流
     * @param locale 语言
     * @return ExcelImportResult
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, InputStream excelStream, OutputStream outStream, Locale locale) throws Exception {
        return excelImport.readExcel(id, 0, excelStream, outStream, locale);
    }

    /***
     * 读取Excel信息
     * 
     * @param id 配置ID
     * @param excelStream Excel文件流
     * @param locale 语言
     * @return ExcelImportResult
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, InputStream excelStream, Locale locale) throws Exception {
        return excelImport.readExcel(id, 0, excelStream, null, locale);
    }

    /***
     * 读取Excel信息
     * 
     * @param id 配置ID
     * @param titleIndex 标题索引,从0开始
     * @param excelStream Excel文件流
     * @param outStream Excel异常文件输出流
     * @param locale 语言
     * @return ExcelImportResult
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, int titleIndex, InputStream excelStream, OutputStream outStream, Locale locale) throws Exception {
        return excelImport.readExcel(id, titleIndex, excelStream, outStream, locale);
    }

    /***
     * 读取Excel信息
     * 
     * @param id 配置ID
     * @param titleIndex 标题索引,从0开始
     * @param excelStream Excel文件流
     * @param locale 语言
     * @return ExcelImportResult
     * @throws Exception
     */
    public ExcelImportResult readExcel(String id, int titleIndex, InputStream excelStream, Locale locale) throws Exception {
        return excelImport.readExcel(id, titleIndex, excelStream, null, locale);
    }

    /**
     * 获取Excel 配置文件中的字段
     * 
     * @param key
     * @return
     */
    public List<FieldValue> getFieldValues(String key) {
        List<FieldValue> list = fieldValueMap.get(key);
        if (list == null) {
            ExcelDefinition def = definitionReader.getRegistry().get(key);
            if (def == null) {
                throw new IllegalArgumentException("没有找到[" + key + "]的配置信息");
            }
            // 使用copy方式,避免使用者修改原生的配置信息
            List<FieldValue> fieldValues = def.getFieldValues();
            list = new ArrayList<FieldValue>(fieldValues.size());
            for (FieldValue fieldValue : fieldValues) {
                FieldValue val = new FieldValue();
                ReflectUtil.copyProps(fieldValue, val);
                list.add(val);
            }
            fieldValueMap.put(key, list);
        }
        return list;
    }

}