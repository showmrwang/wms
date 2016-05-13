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


public class BiPoCommand extends BaseCommand {



    private static final long serialVersionUID = -2160506393189014753L;

    /** 主键ID */
    private Long id;
    /** PO单号 */
    private String poCode;
    /** 相关单据号 */
    private String extCode;
    /** 对应组织ID */
    private Long ouId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
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
    /** 实际到货时间 */
    private Date deliveryTime;
    /** 计划到货数量 */
    private Double qtyPlanned;
    /** 实际到货数量 */
    private Double qtyRcvd;
    /** 计划箱数 */
    private Integer ctnPlanned;
    /** 实际箱数 */
    private Integer ctnRcvd;
    /** 开始收货时间 */
    private Date startTime;
    /** 结束收货时间 */
    private Date stopTime;
    /** 上架完成时间 */
    private Date inboundTime;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    /** 采购时间Str */
    private String poDateStr;
    /** 计划到货时间Str */
    private String etaStr;

    /** poLine数据 */
    private List<BiPoLineCommand> poLineList;

    /** 是否WMS创建PO单 true:wms创建 false:上位系统创建 */
    private Boolean isWms = false;
    /** 是否VMI创建PO单 true:是 false:否 */
    private Boolean isVmi = false;
    /** 客户名称 */
    private String customerName;
    /** 店铺名称 */
    private String storeName;
    /** 操作员工ID */
    private Long userId;

/** poid list */
    private List<Long> poIds;
    /** 状态名称 */
    private String statusName;
    /** 仓库名称 */
    private String ouName;
    /** 单据类型名称 */
    private String poTypeName;
    /** 是否 */
    private String isIqcName;
    /** 供应商ID */
    private String supplierName;
    /** 运输商ID */
    private String logisticsProviderName;
    /** Asn相关单据号 */
    private String asnExtCode;
    /** 收货开始时间字符串类型 */
    private String startTimeStr;
    /** 收货结束时间字符串类型 */
    private String stopTimeStr;
    /** 上架完成时间字符串类型 */
    private String inboundTimeStr;
    /** 修改者名称 */
    private String modifiedUserName;
    /** 模糊查询限定行数 */
    private Integer linenum;
    /** 公共库列表集合 */
    List<WhPoCommand> whPoList;
    /** Uuid */
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<WhPoCommand> getWhPoList() {
        return whPoList;
    }

    public void setWhPoList(List<WhPoCommand> whPoList) {
        this.whPoList = whPoList;
    }

    public Integer getLinenum() {
        return linenum;
    }

    public void setLinenum(Integer linenum) {
        this.linenum = linenum;
    }

    public String getModifiedUserName() {
        return modifiedUserName;
    }

    public void setModifiedUserName(String modifiedUserName) {
        this.modifiedUserName = modifiedUserName;
    }

    private List<Integer> statusList;
    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getPoCode() {
        return this.poCode;
    }

    public void setPoCode(String value) {
        this.poCode = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long value) {
        this.customerId = value;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public void setStoreId(Long value) {
        this.storeId = value;
    }

    public Long getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(Long value) {
        this.supplierId = value;
    }

    public Long getLogisticsProviderId() {
        return this.logisticsProviderId;
    }

    public void setLogisticsProviderId(Long value) {
        this.logisticsProviderId = value;
    }

    public Integer getPoType() {
        return this.poType;
    }

    public void setPoType(Integer value) {
        this.poType = value;
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

    public Date getPoDate() {
        return this.poDate;
    }

    public void setPoDate(Date value) {
        this.poDate = value;
    }

    public Date getEta() {
        return this.eta;
    }

    public void setEta(Date value) {
        this.eta = value;
    }

    public Date getDeliveryTime() {
        return this.deliveryTime;
    }

    public void setDeliveryTime(Date value) {
        this.deliveryTime = value;
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

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date value) {
        this.startTime = value;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Date value) {
        this.stopTime = value;
    }

    public Date getInboundTime() {
        return this.inboundTime;
    }

    public void setInboundTime(Date value) {
        this.inboundTime = value;
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

    public String getPoDateStr() {
        return poDateStr;
    }

    public void setPoDateStr(String poDateStr) {
        this.poDateStr = poDateStr;
    }

    public String getEtaStr() {
        return etaStr;
    }

    public void setEtaStr(String etaStr) {
        this.etaStr = etaStr;
    }


    public List<BiPoLineCommand> getPoLineList() {
        return poLineList;
    }

    public void setPoLineList(List<BiPoLineCommand> poLineList) {
        this.poLineList = poLineList;
    }

    public Boolean getIsWms() {
        return isWms;
    }

    public void setIsWms(Boolean isWms) {
        this.isWms = isWms;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsVmi() {
        return isVmi;
    }

    public void setIsVmi(Boolean isVmi) {
        this.isVmi = isVmi;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<Long> getPoIds() {
        return poIds;
    }

    public void setPoIds(List<Long> poIds) {
        this.poIds = poIds;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public String getPoTypeName() {
        return poTypeName;
    }

    public void setPoTypeName(String poTypeName) {
        this.poTypeName = poTypeName;
    }

    public String getIsIqcName() {
        return isIqcName;
    }

    public void setIsIqcName(String isIqcName) {
        this.isIqcName = isIqcName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLogisticsProviderName() {
        return logisticsProviderName;
    }

    public void setLogisticsProviderName(String logisticsProviderName) {
        this.logisticsProviderName = logisticsProviderName;
    }

    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getStopTimeStr() {
        return stopTimeStr;
    }

    public void setStopTimeStr(String stopTimeStr) {
        this.stopTimeStr = stopTimeStr;
    }

    public String getInboundTimeStr() {
        return inboundTimeStr;
    }

    public void setInboundTimeStr(String inboundTimeStr) {
        this.inboundTimeStr = inboundTimeStr;
    }



}
