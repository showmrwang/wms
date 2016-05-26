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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class RecommendRuleCondition extends BaseModel {
    private static final long serialVersionUID = 524600918430513521L;


    // columns START
    /** 表名 */
    private String tableName;
    /** 表名编码 */
    private String tableCode;
    /** 字段名 */
    private String columnName;
    /** 字段编码 */
    private String columnCode;
    /** 字段属性 */
    private String columnProperty;
    /** 规则类型 */
    private String ruleType;
    /** 所属分组编码 */
    private String gorupCode;
    /** 所属分组名称 */
    private String gorupName;
    /** 所属分组ID */
    private Integer gorupId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnProperty() {
        return columnProperty;
    }

    public void setColumnProperty(String columnProperty) {
        this.columnProperty = columnProperty;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getGorupCode() {
        return gorupCode;
    }

    public void setGorupCode(String gorupCode) {
        this.gorupCode = gorupCode;
    }

    public String getGorupName() {
        return gorupName;
    }

    public void setGorupName(String gorupName) {
        this.gorupName = gorupName;
    }

    public Integer getGorupId() {
        return gorupId;
    }

    public void setGorupId(Integer gorupId) {
        this.gorupId = gorupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }
}
