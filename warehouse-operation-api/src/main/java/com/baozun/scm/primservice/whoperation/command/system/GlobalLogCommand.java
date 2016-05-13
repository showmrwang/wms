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
package com.baozun.scm.primservice.whoperation.command.system;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author 全局日志
 * 
 */
public class GlobalLogCommand extends BaseCommand {

    private static final long serialVersionUID = 9026646643332638341L;
    /** 主键ID */
    private Long id;
    /** 类型 INSERT UPDATE DELETE */
    private String type;
    /** 对象名称 */
    private String objectType;
    /** 操作值 */
    private Object modifiedValues;
    /** 解疑文本 */
    private String modifiedValuesTranslate;
    /** 对应表头code */
    private String parentCode;
    /** 是否解疑0:否 1:是 */
    private Boolean isTranslate;
    /** 对应仓库 */
    private Long ouId;
    /** 操作人ID */
    private Long modifiedId;
    /** 操作时间 */
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Object getModifiedValues() {
        return modifiedValues;
    }

    public void setModifiedValues(Object modifiedValues) {
        this.modifiedValues = modifiedValues;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getModifiedValuesTranslate() {
        return modifiedValuesTranslate;
    }

    public void setModifiedValuesTranslate(String modifiedValuesTranslate) {
        this.modifiedValuesTranslate = modifiedValuesTranslate;
    }

    public Boolean getIsTranslate() {
        return isTranslate;
    }

    public void setIsTranslate(Boolean isTranslate) {
        this.isTranslate = isTranslate;
    }


}
