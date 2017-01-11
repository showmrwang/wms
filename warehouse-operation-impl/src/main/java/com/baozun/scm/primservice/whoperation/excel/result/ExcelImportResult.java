package com.baozun.scm.primservice.whoperation.excel.result;

import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baozun.scm.primservice.whoperation.excel.ExcelImport;
import com.baozun.scm.primservice.whoperation.excel.exception.ExcelException;
import com.baozun.scm.primservice.whoperation.excel.exception.RootExcelException;

/**
 * Excel导入结果
 * 
 * @author lisuo
 *
 */
public class ExcelImportResult {
	
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportResult.class);

    public static final int READ_STATUS_SUCCESS = 1;
    public static final int READ_STATUS_FAILED = 0;

    /** excel读取状态 0:失败，1：成功 **/
    private int readstatus;
    /** excel 读取当前sheet名称 **/
    private String sheetName;
    /** 标题大小 **/
    private int titleSize;

    private RootExcelException exception = null;

    /** 头信息,标题行之前的数据,每行表示一个List<Object>,每个Object存放一个cell单元的值 */
    private List<List<Object>> header = null;

    /** JavaBean集合,从标题行下面解析的数据 */
    private List<?> listBean;

    /** 标题位置行 **/
    private int titleIndex;

    /** 导入EXCEL工作薄 **/
    private Workbook workbook;

    public List<List<Object>> getHeader() {
        return header;
    }

    public void setHeader(List<List<Object>> header) {
        this.header = header;
    }

    /**
     * 获取构造导入异常链表信息
     * 
     * @return
     */
    public RootExcelException getRootExcelException() {
        if (exception == null) {
            exception = new RootExcelException("", sheetName, titleSize);
        }
        return exception;
    }

    /**
     * 封装行错误信息
     * 
     * @param errorMsg 国际化错误信息
     * @param errorCode 错误编码
     * @param listIndex 实体bean的索引位置，系统或默认增加标题行偏移量
     */
    public void addRowException(String errorMsg, String errorCode, int listIndex) {
        addRowException(errorMsg, errorCode, listIndex + titleIndex, null);
    }

    /**
     * 通过异常信息重新构建工作薄
     * 
     * @return
     */
    public Workbook constructionWookbookException() {
        try {
            ExcelImport.exportImportErroeMsg(workbook, getRootExcelException());
        } catch (EncryptedDocumentException e) {
            logger.error("", e);
        } catch (InvalidFormatException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }
        return workbook;
    }

    /**
     * 封装行错误信息
     * 
     * @param errorMsg 国际化错误信息
     * @param errorCode 错误编码
     * @param row 行号
     * @param titleName 标题名称
     */
    public void addRowException(String errorMsg, String errorCode, int row, String titleName) {
        ExcelException ex = new ExcelException(errorMsg, errorCode, row, titleName);
        getRootExcelException().getExcelExceptions().add(ex);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getListBean() {
        return (List<T>) listBean;
    }

    public void setListBean(List<?> listBean) {
        this.listBean = listBean;
    }

    public int getTitleIndex() {
        return titleIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setTitleIndex(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public int getReadstatus() {
        return readstatus;
    }

    public void setReadstatus(int readstatus) {
        this.readstatus = readstatus;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setRootExcelException(RootExcelException exception) {
        this.exception = exception;
    }

}
