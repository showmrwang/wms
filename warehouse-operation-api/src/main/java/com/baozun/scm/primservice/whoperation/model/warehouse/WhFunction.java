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
 * 功能主表
 * 
 * @author larkark
 * 
 */
public class WhFunction extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 6137253916892248796L;

    /** 功能编码 */
    private String functionCode;
    /** 功能名称 */
    private String functionName;
    /** 功能模板 收货 上架 入库分拣 */
    private String functionTemplet;
    /** 平台类型 PDA 操作台opstation */
    private String platformType;
    /** SKU属性管理 */
    private String skuAttrMgmt;
    /** 对应仓库ID */
    private Long ouId;
    /** 是否系统创建人 */
    private Boolean isSys = false;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createId;
    /** 操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionTemplet() {
        return functionTemplet;
    }

    public void setFunctionTemplet(String functionTemplet) {
        this.functionTemplet = functionTemplet;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Boolean getIsSys() {
        return isSys;
    }

    public void setIsSys(Boolean isSys) {
        this.isSys = isSys;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getSkuAttrMgmt() {
        return skuAttrMgmt;
    }

    public void setSkuAttrMgmt(String skuAttrMgmt) {
        this.skuAttrMgmt = skuAttrMgmt;
    }



}
