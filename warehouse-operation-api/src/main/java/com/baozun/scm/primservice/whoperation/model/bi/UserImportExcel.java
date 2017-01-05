/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.bi;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


public class UserImportExcel extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -6597448559984915911L;
    
    /** 导入编码 */
    private String importCode;
    /** 导入文件名 */
    private String fileName;
    /** 导入失败原因文件名 */
    private String errorFileName;
    /** 导出用户ID */
    private Long userId;
    /** 导出类型 */
    private String type;
    /** 组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 是否执行成功 0:否 1:是 */
    private Boolean isSuccess = false;
    
    public String getImportCode() {
        return this.importCode;
    }

    public void setImportCode(String value) {
        this.importCode = value;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String value) {
        this.fileName = value;
    }

    public String getErrorFileName() {
        return this.errorFileName;
    }

    public void setErrorFileName(String value) {
        this.errorFileName = value;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long value) {
        this.userId = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    public void setIsSuccess(Boolean value) {
        this.isSuccess = value;
    }

}
