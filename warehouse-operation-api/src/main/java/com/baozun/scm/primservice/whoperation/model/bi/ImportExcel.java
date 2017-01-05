package com.baozun.scm.primservice.whoperation.model.bi;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/***
 * 导入excel参数表
 * 
 * @author bin.hu
 *
 */
public class ImportExcel extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 5531235598677756072L;

    /** 文件流 */
    private InputStream inputStream;

    /** 导入类型 */
    private String importType;

    /** 仓库组织ID */
    private Long ouId;

    /** 用户ID */
    private Long userId;

    private Workbook workbook;

    /** 导入用户信息表 */
    private Long userImportExcelId;


    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Long getUserImportExcelId() {
        return userImportExcelId;
    }

    public void setUserImportExcelId(Long userImportExcelId) {
        this.userImportExcelId = userImportExcelId;
    }



}
