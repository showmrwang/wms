package com.baozun.scm.primservice.whoperation.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.baozun.scm.primservice.whoperation.excel.exception.ExcelException;
import com.baozun.scm.primservice.whoperation.excel.exception.RootExcelException;
import com.baozun.scm.primservice.whoperation.excel.util.ReflectUtil;
import com.baozun.scm.primservice.whoperation.excel.util.SpringUtil;
import com.baozun.scm.primservice.whoperation.excel.vo.ExcelDefinition;
import com.baozun.scm.primservice.whoperation.excel.vo.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.excel.vo.FieldValue;
import com.baozun.scm.primservice.whoperation.excel.xml.ExcelDefinitionReader;



/**
 * Excel导入实现类
 * 
 * @author lisuo
 *
 */
public class ExcelImport extends AbstractExcelResolver {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImport.class);
	public ExcelImport(ExcelDefinitionReader definitionReader) {
		super(definitionReader);
	}

	/**
	 * 读取Excel信息
	 * 
	 * @param id
	 *            注册的ID
	 * @param titleIndex
	 *            标题索引
	 * @param excelStream
	 *            Excel文件流
	 * @param locale
	 * 			  语言
	 * @return
	 * @throws Exception
	 */
	public ExcelImportResult readExcel(String id, int titleIndex, InputStream excelStream,OutputStream outStream,Locale locale) throws Exception {
		// 从注册信息中获取Bean信息
		ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
		if (excelDefinition == null) {
			throw new IllegalArgumentException("没有找到 [" + id + "] 的配置信息");
		}
		return doReadExcel(excelDefinition, titleIndex, excelStream,outStream,locale);
	}

	protected ExcelImportResult doReadExcel(ExcelDefinition excelDefinition, int titleIndex, InputStream excelStream,OutputStream outStream,Locale locale)
			throws Exception {
		Workbook workbook = WorkbookFactory.create(excelStream);
		ExcelImportResult result = new ExcelImportResult();
		// 只读取第一个sheet
		Sheet sheet = workbook.getSheet(excelDefinition.getSheetname());
		if (sheet == null) {
			throw new RootExcelException("no sheet", excelDefinition.getSheetname());
		}
		// 标题之前的数据处理
		try{
			List<List<Object>> header = readHeader(excelDefinition, sheet, titleIndex);
			result.setHeader(header);
			// 获取标题
			List<String> titles = readTitle(excelDefinition, sheet, titleIndex);
			// 获取Bean
			List<Object> listBean = readRows(excelDefinition, titles, sheet, titleIndex,locale);
			result.setListBean(listBean);
			return result;
		}catch (RootExcelException e){
			if(outStream != null){
				exportImportErroeMsg(workbook, e);
				workbook.write(outStream);
				workbook.close();
				throw new ExcelException("excel validate error");
			}else{
				throw e;
			}
		}finally {
			try{
				outStream.close();
			}catch(Exception e){
			}
			try{
				excelStream.close();
			}catch(Exception e){
			}
		}
	}

	/**
	 * 解析标题之前的内容,如果ExcelDefinition中titleIndex 不是0
	 * 
	 * @param excelDefinition
	 * @param sheet
	 * @return
	 */
	protected List<List<Object>> readHeader(ExcelDefinition excelDefinition, Sheet sheet, int titleIndex) {
		List<List<Object>> header = null;
		if (titleIndex != 0) {
			header = new ArrayList<List<Object>>(titleIndex);
			for (int i = 0; i < titleIndex; i++) {
				Row row = sheet.getRow(i);
				short cellNum = row.getLastCellNum();
				List<Object> item = new ArrayList<Object>(cellNum);
				for (int j = 0; j < cellNum; j++) {
					Cell cell = row.getCell(j);
					Object value = getCellValue(cell);
					item.add(value);
				}
				header.add(item);
			}
		}
		return header;
	}

	/**
	 * 读取多行
	 * 
	 * @param excelDefinition
	 * @param titles
	 * @param sheet
	 * @param titleIndex
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> readRows(ExcelDefinition excelDefinition, List<String> titles, Sheet sheet, int titleIndex, Locale locale)
			throws Exception {
		int rowNum = sheet.getLastRowNum();
		// 读取数据的总共次数
		int totalNum = rowNum - titleIndex;
		RootExcelException exception = new RootExcelException("", sheet.getSheetName(), titles.size());
		List<T> listBean = new ArrayList<T>(totalNum);
		for (int i = titleIndex + 1; i <= rowNum; i++) {
			Row row = sheet.getRow(i);
			Object bean = readRow(excelDefinition, row, titles, i, exception,locale);
			listBean.add((T) bean);
		}
		if (exception.isException()) {
			throw exception;
		}
		return listBean;
	}

	/**
	 * 读取1行
	 * 
	 * @param excelDefinition
	 * @param row
	 * @param titles
	 * @param rowNum
	 *            第几行
	 * @return
	 * @throws Exception
	 */
	protected Object readRow(ExcelDefinition excelDefinition, Row row, List<String> titles, int rowNum,
			RootExcelException exception,Locale locale) throws Exception {
		// 创建注册时配置的bean类型
		Object bean = ReflectUtil.newInstance(excelDefinition.getClazz());
		for (FieldValue fieldValue : excelDefinition.getFieldValues()) {
			String title = fieldValue.getTitle();
			for (int j = 0; j < titles.size(); j++) {
				if (title.equals(titles.get(j))) {
					// 如果当前行为空行则跳过
					if (row == null) {
						continue;
					}
					Cell cell = row.getCell(j);
					// 获取Excel原生value值
					Object value = getCellValue(cell);
					// 校验
					validate(fieldValue, value, rowNum, exception,locale);
					if (value != null) {
						if (value instanceof String) {
							// 去除前后空格
							value = value.toString().trim();
						}
						value = super.resolveFieldValue(bean, value, fieldValue, Type.IMPORT, rowNum);
						ReflectUtil.setProperty(bean, fieldValue.getName(), value);
					}
					break;
				}
			}
		}
		return bean;
	}

	protected List<String> readTitle(ExcelDefinition excelDefinition, Sheet sheet, int titleIndex) {
		// 获取Excel标题数据
		Row hssfRowTitle = sheet.getRow(titleIndex);
		int cellNum = hssfRowTitle.getLastCellNum();
		List<String> titles = new ArrayList<String>(cellNum);
		// 获取标题数据
		for (int i = 0; i < cellNum; i++) {
			Cell cell = hssfRowTitle.getCell(i);
			Object value = getCellValue(cell);
			if (value == null) {
				throw new IllegalArgumentException("id 为:[" + excelDefinition.getId() + "]的标题不能为[ null ]");
			}
			titles.add(value.toString());
		}
		return titles;
	}

	/**
	 * 数据有效性校验
	 * 
	 * @param fieldValue
	 * @param value
	 * @param rowNum
	 */
	private void validate(FieldValue fieldValue, Object value, int rowNum, RootExcelException rootExcelException,Locale locale) {
		if (value == null || StringUtils.isBlank(value.toString())) {
			// 空校验
			if (!fieldValue.isNull()) {
				ResourceBundleMessageSource msg = (ResourceBundleMessageSource) SpringUtil.getBean(ResourceBundleMessageSource.class);
				String errorMsg = "is null";
				try{
					errorMsg = msg.getMessage(fieldValue.getNullErrorCode(), null, locale);
				}catch(NoSuchMessageException e){
					logger.warn("i18n message is not defined,error code : {}",fieldValue.getNullErrorCode());
				}
				rootExcelException.getExcelExceptions()
						.add(new ExcelException(errorMsg, fieldValue.getNullErrorCode(), rowNum, fieldValue.getTitle()));
			}
		} else {
			// 正则校验
			String regex = fieldValue.getRegex();
			if (StringUtils.isNotBlank(regex)) {
				String val = value.toString().trim();
				if (!val.matches(regex)) {
					ResourceBundleMessageSource msg = (ResourceBundleMessageSource) SpringUtil.getBean(ResourceBundleMessageSource.class);
					String errorMsg = fieldValue.getRegexErrMsg() == null ? "format error" : fieldValue.getRegexErrMsg();
					//判断是否定义错误信息编码，如定义查询国际化中是否存在对应国际化文本
					if(fieldValue.getRegexErrCode() != null){
						try{
							errorMsg = msg.getMessage(fieldValue.getRegexErrCode(), null, locale);
						}catch(NoSuchMessageException e){
							logger.warn("i18n message is not defined,error code : {}",fieldValue.getRegexErrCode());
						}
					}
					rootExcelException.getExcelExceptions().add(
							new ExcelException(errorMsg, fieldValue.getRegexErrCode(), rowNum, fieldValue.getTitle()));
				}
			}
		}
	}

	public void exportImportErroeMsg(Workbook workbook, RootExcelException rootExcelException)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		Sheet sheet = workbook.getSheet(rootExcelException.getSheetName());
		if (sheet == null) {
			throw new RootExcelException("when write sheet error msg, sheet name is null!");
		}
		for (ExcelException ee : rootExcelException.getExcelExceptions()) {
			Row row = sheet.getRow(ee.getRow());
			Cell cell = row.getCell(rootExcelException.getCellNum());
			if (cell == null) {
				cell = row.createCell(rootExcelException.getCellNum());
				CellStyle style = workbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			}else{
				cell.setCellValue(cell.getStringCellValue() + ";");
			}
			cell.setCellValue(cell.getStringCellValue() + "[" + ee.getTitleName() + "]" + ee.getMessage());
		}
	}

}
