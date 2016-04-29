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

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class WhAsnLineCommand extends BaseCommand {


    private static final long serialVersionUID = 1258888820659436722L;

    /** 主键ID */
    private Long id;
    /** ASN单ID */
    private Long asnId;
    /** 对应组织ID */
    private Long ouId;
    /** 对应po单lineid */
    private Long poLineId;
    /** 对应po单line的ouid */
    private Long poOuId;
    /** POLine行号 */
    private Integer poLinenum;
    /** SKU_ID */
    private Long skuId;
    /** 计划数量 */
    private Double qtyPlanned;
    /** 原计划数量 */
    private Integer qtyPlannedOld;
    /** 可拆数量 */
    private Integer usableDevanningQty;
    /** 计划箱数 */
    private Integer ctnPlanned;
    /** 已收数量 */
    private Double qtyRcvd;
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
    /** 对应ASNCODE */
    private String asnCode;
    /** 对应ASN 相关单据号 */
    private String asnExtCode;
    /** 状态名称 */
    private String statusName;
    /** 生产日期String */
    private String mfgDateStr;
    /** 失效日期String */
    private String expDateStr;
    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 商品对接码 */
    private String skuExtCode;
    /** 商品条码 */
    private String skuBarCode;
    /** 库存类型 */
    private String invType;
    /** 库存类型label */
    private String invTypeLabel;
    /** 批量操作时候，记录主键集合 */
    private List<Long> ids;
    /** 保质期单位 */
    private String validDateUom;
    /** 单位换算率 */
    private Double conversionRate;
    /** 库存状态名称 */
    private String invName;
    /** 商品ID */
    private Long skuid;
    /** 库存属性1 */
    private String inv1Str;
    /** 库存属性2 */
    private String inv2Str;
    /** 库存属性3 */
    private String inv3Str;
    /** 库存属性4 */
    private String inv4Str;
    /** 库存属性5 */
    private String inv5Str;

    /** =============商品相关属性================== */
    /** 是否管理效期 有效期商品 */
    private Boolean isValid;
    /** 是否管理批次号 */
    private Boolean isBatchNo;
    /** 是否管理原产地 */
    private Boolean isCountryOfOrigin;
    /** 是否管理库存类型 */
    private Boolean isInvType;
    /** 是否库存属性1 */
    private Boolean invAttr1Boolean;
    /** 是否库存属性2 */
    private Boolean invAttr2Boolean;
    /** 是否库存属性3 */
    private Boolean invAttr3Boolean;
    /** 是否库存属性4 */
    private Boolean invAttr4Boolean;
    /** 是否库存属性5 */
    private Boolean invAttr5Boolean;
    /** 单位名称 */
    private String uomName;
    /** 单位CODE */
    private String uomCode;

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
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

    public String getAsnCode() {
        return asnCode;
    }

    public void setAsnCode(String asnCode) {
        this.asnCode = asnCode;
    }

    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getMfgDateStr() {
        return mfgDateStr;
    }

    public void setMfgDateStr(String mfgDateStr) {
        this.mfgDateStr = mfgDateStr;
    }

    public String getExpDateStr() {
        return expDateStr;
    }

    public void setExpDateStr(String expDateStr) {
        this.expDateStr = expDateStr;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuExtCode() {
        return skuExtCode;
    }

    public void setSkuExtCode(String skuExtCode) {
        this.skuExtCode = skuExtCode;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public Long getPoOuId() {
        return poOuId;
    }

    public void setPoOuId(Long poOuId) {
        this.poOuId = poOuId;
    }

    public Integer getQtyPlannedOld() {
        return qtyPlannedOld;
    }

    public void setQtyPlannedOld(Integer qtyPlannedOld) {
        this.qtyPlannedOld = qtyPlannedOld;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getSkuid() {
        return skuid;
    }

    public void setSkuid(Long skuid) {
        this.skuid = skuid;
    }

    public String getInv1Str() {
        return inv1Str;
    }

    public void setInv1Str(String inv1Str) {
        this.inv1Str = inv1Str;
    }

    public String getInv2Str() {
        return inv2Str;
    }

    public void setInv2Str(String inv2Str) {
        this.inv2Str = inv2Str;
    }

    public String getInv3Str() {
        return inv3Str;
    }

    public void setInv3Str(String inv3Str) {
        this.inv3Str = inv3Str;
    }

    public String getInv4Str() {
        return inv4Str;
    }

    public void setInv4Str(String inv4Str) {
        this.inv4Str = inv4Str;
    }

    public String getInv5Str() {
        return inv5Str;
    }

    public void setInv5Str(String inv5Str) {
        this.inv5Str = inv5Str;
    }

    public String getInvTypeLabel() {
        return invTypeLabel;
    }

    public void setInvTypeLabel(String invTypeLabel) {
        this.invTypeLabel = invTypeLabel;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Boolean getIsBatchNo() {
        return isBatchNo;
    }

    public void setIsBatchNo(Boolean isBatchNo) {
        this.isBatchNo = isBatchNo;
    }

    public Boolean getIsCountryOfOrigin() {
        return isCountryOfOrigin;
    }

    public void setIsCountryOfOrigin(Boolean isCountryOfOrigin) {
        this.isCountryOfOrigin = isCountryOfOrigin;
    }

    public Boolean getIsInvType() {
        return isInvType;
    }

    public void setIsInvType(Boolean isInvType) {
        this.isInvType = isInvType;
    }

    public Boolean getInvAttr1Boolean() {
        return invAttr1Boolean;
    }

    public void setInvAttr1Boolean(Boolean invAttr1Boolean) {
        this.invAttr1Boolean = invAttr1Boolean;
    }

    public Boolean getInvAttr2Boolean() {
        return invAttr2Boolean;
    }

    public void setInvAttr2Boolean(Boolean invAttr2Boolean) {
        this.invAttr2Boolean = invAttr2Boolean;
    }

    public Boolean getInvAttr3Boolean() {
        return invAttr3Boolean;
    }

    public void setInvAttr3Boolean(Boolean invAttr3Boolean) {
        this.invAttr3Boolean = invAttr3Boolean;
    }

    public Boolean getInvAttr4Boolean() {
        return invAttr4Boolean;
    }

    public void setInvAttr4Boolean(Boolean invAttr4Boolean) {
        this.invAttr4Boolean = invAttr4Boolean;
    }

    public Boolean getInvAttr5Boolean() {
        return invAttr5Boolean;
    }

    public void setInvAttr5Boolean(Boolean invAttr5Boolean) {
        this.invAttr5Boolean = invAttr5Boolean;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public Integer getUsableDevanningQty() {
        return usableDevanningQty;
    }

    public void setUsableDevanningQty(Integer usableDevanningQty) {
        this.usableDevanningQty = usableDevanningQty;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
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


}
