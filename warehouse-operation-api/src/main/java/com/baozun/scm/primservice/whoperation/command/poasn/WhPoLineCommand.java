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
package com.baozun.scm.primservice.whoperation.command.poasn;

import java.io.Serializable;
import java.util.Date;


public class WhPoLineCommand implements Serializable {

    private static final long serialVersionUID = -2406000061598125009L;

    /** 主键ID */
    private Long id;
    /** PO单ID */
    private Long poId;
    /** PO行号 */
    private Integer linenum;
    /** SKU_ID */
    private Long skuId;
    /** 计划数量 */
    private Integer qtyPlanned;
    /** 超收数量 */
    private Integer overshipped;
    /** 可用数量 */
    private Integer availableQty;
    /** 计划箱数 */
    private Integer ctnPlanned;
    /** 已收数量 */
    private Integer qtyRcvd;
    /** 已收箱数 */
    private Integer ctnRcvd;
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
    /** 临时数据批次 IT使用 */
    private String uuid;

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getPoId() {
        return this.poId;
    }

    public void setPoId(Long value) {
        this.poId = value;
    }

    public Integer getLinenum() {
        return this.linenum;
    }

    public void setLinenum(Integer value) {
        this.linenum = value;
    }

    public Long getSkuId() {
        return this.skuId;
    }

    public void setSkuId(Long value) {
        this.skuId = value;
    }

    public Integer getQtyPlanned() {
        return this.qtyPlanned;
    }

    public void setQtyPlanned(Integer value) {
        this.qtyPlanned = value;
    }

    public Integer getOvershipped() {
        return this.overshipped;
    }

    public void setOvershipped(Integer value) {
        this.overshipped = value;
    }

    public Integer getCtnPlanned() {
        return this.ctnPlanned;
    }

    public void setCtnPlanned(Integer value) {
        this.ctnPlanned = value;
    }

    public Integer getQtyRcvd() {
        return this.qtyRcvd;
    }

    public void setQtyRcvd(Integer value) {
        this.qtyRcvd = value;
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

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String value) {
        this.uuid = value;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

}
