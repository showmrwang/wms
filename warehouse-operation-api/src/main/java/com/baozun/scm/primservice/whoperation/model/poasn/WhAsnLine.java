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
 * ASN单据明细信息
 * 
 * @author larkark
 * 
 */
public class WhAsnLine extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 8263596445503189745L;

    /** ASN单ID */
    private Long asnId;
    /** 对应po单lineid */
    private Long poLineId;
    /** 对应组织ID */
    private Long ouId;
    /** POLine行号 */
    private Integer poLinenum;
    /** SKU_ID */
    private Long skuId;
    /** 计划数量 */
    private Double qtyPlanned = 0.0;
    /** 计划箱数 */
    private Integer ctnPlanned = 0;
    /** 已收数量 */
    private Double qtyRcvd = 0.0;
    /** 已收箱数 */
    private Integer ctnRcvd = 0;
    /** 状态 */
    private Integer status;
    /** 是否质检 1:是 0:否 */
    private Boolean isIqc;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 有效期天数 */
    private Integer validDate;
    /** 批次号 */
    private String batchNo;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private Long invStatus;
    /** 库存属性1 */
    private String invAttr1;
    /** 库存属性2 */
    private String invAttr2;
    /** 库存属性3 */
    private String invAttr3;
    /** 库存属性4 */
    private String invAttr4;
    /** 库存属性5 */
    private String invAttr5;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最好修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 库存类型 */
    private String invType;
    /** 保质期单位 */
    private String validDateUom;
    /** IT专用 */
    private String uuid;

    /** 当前月份 用于归档 */
    private String sysDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getValidDateUom() {
        return validDateUom;
    }

    public void setValidDateUom(String validDateUom) {
        this.validDateUom = validDateUom;
    }


    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public Long getAsnId() {
        return this.asnId;
    }

    public void setAsnId(Long value) {
        this.asnId = value;
    }

    public Long getPoLineId() {
        return this.poLineId;
    }

    public void setPoLineId(Long value) {
        this.poLineId = value;
    }

    public Integer getPoLinenum() {
        return poLinenum;
    }

    public void setPoLinenum(Integer poLinenum) {
        this.poLinenum = poLinenum;
    }

    public Long getSkuId() {
        return this.skuId;
    }

    public void setSkuId(Long value) {
        this.skuId = value;
    }

    public Integer getCtnPlanned() {
        return this.ctnPlanned;
    }

    public void setCtnPlanned(Integer value) {
        this.ctnPlanned = value;
    }

    public Integer getCtnRcvd() {
        return this.ctnRcvd;
    }

    public void setCtnRcvd(Integer value) {
        this.ctnRcvd = value;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer value) {
        this.status = value;
    }

    public Boolean getIsIqc() {
        return this.isIqc;
    }

    public void setIsIqc(Boolean value) {
        this.isIqc = value;
    }

    public Date getMfgDate() {
        return this.mfgDate;
    }

    public void setMfgDate(Date value) {
        this.mfgDate = value;
    }

    public Date getExpDate() {
        return this.expDate;
    }

    public void setExpDate(Date value) {
        this.expDate = value;
    }

    public Integer getValidDate() {
        return this.validDate;
    }

    public void setValidDate(Integer value) {
        this.validDate = value;
    }

    public String getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(String value) {
        this.batchNo = value;
    }

    public String getCountryOfOrigin() {
        return this.countryOfOrigin;
    }

    public void setCountryOfOrigin(String value) {
        this.countryOfOrigin = value;
    }

    public Long getInvStatus() {
        return this.invStatus;
    }

    public void setInvStatus(Long value) {
        this.invStatus = value;
    }

    public String getInvAttr1() {
        return this.invAttr1;
    }

    public void setInvAttr1(String value) {
        this.invAttr1 = value;
    }

    public String getInvAttr2() {
        return this.invAttr2;
    }

    public void setInvAttr2(String value) {
        this.invAttr2 = value;
    }

    public String getInvAttr3() {
        return this.invAttr3;
    }

    public void setInvAttr3(String value) {
        this.invAttr3 = value;
    }

    public String getInvAttr4() {
        return this.invAttr4;
    }

    public void setInvAttr4(String value) {
        this.invAttr4 = value;
    }

    public String getInvAttr5() {
        return this.invAttr5;
    }

    public void setInvAttr5(String value) {
        this.invAttr5 = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Long getCreatedId() {
        return this.createdId;
    }

    public void setCreatedId(Long value) {
        this.createdId = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Long getModifiedId() {
        return this.modifiedId;
    }

    public void setModifiedId(Long value) {
        this.modifiedId = value;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Double getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(Double qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public Double getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Double qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }



}
