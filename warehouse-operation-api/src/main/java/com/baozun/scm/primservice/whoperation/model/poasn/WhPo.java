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
package com.baozun.scm.primservice.whoperation.model.poasn;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 采购单
 * 
 * @author larkark
 * 
 */
public class WhPo extends BaseModel {

    private static final long serialVersionUID = 6476267264305613566L;

    /** PO单号 */
    private String poCode;
    /** 对应组织ID */
    private Long ouId;
    /** 供应商ID */
    private Long supplierId;
    /** 运输商ID */
    private Long logisticsProviderId;
    /** PO单类型 */
    private Integer poType;
    /** 状态 */
    private Integer status;
    /** 是否质检 1:是 0:否 */
    private Boolean isIqc;
    /** 采购时间 */
    private Date poDate;
    /** 计划到货时间 */
    private Date eta;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getLogisticsProviderId() {
        return logisticsProviderId;
    }

    public void setLogisticsProviderId(Long logisticsProviderId) {
        this.logisticsProviderId = logisticsProviderId;
    }

    public Integer getPoType() {
        return poType;
    }

    public void setPoType(Integer poType) {
        this.poType = poType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsIqc() {
        return isIqc;
    }

    public void setIsIqc(Boolean isIqc) {
        this.isIqc = isIqc;
    }

    public Date getPoDate() {
        return poDate;
    }

    public void setPoDate(Date poDate) {
        this.poDate = poDate;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
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
