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
public class WhOutboundbox extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 8454463474615848733L;

    // columns START
    /** 小批次 */
    private String batch;
    /** 波次号 */
    private String waveCode;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 运输服务商CODE */
    private String transportCode;
    /** 运输服务商名称 */
    private String transportName;
    /** 产品类型CODE */
    private String productCode;
    /** 产品类型名称 */
    private String productName;
    /** 时效类型CODE */
    private String timeEffectCode;
    /** 时效类型名称 */
    private String timeEffectName;
    /** 状态 */
    private String status;
    /** 对应组织ID */
    private Long ouId;
    /** 出库单ID */
    private Long odoId;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 配货模式 */
    private String distributionMode;
    /** 拣货模式 */
    private String pickingMode;
    /** 复核模式 */
    private String checkingMode;

    /** 当前月份 用于归档 */
    private String sysDate;
    
    private Long createId;
    
    
    private Date createTime;
    
    
    private Date lastModifyTime;
    
    
    private Long modifiedId;

    // columns END


    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTimeEffectCode() {
        return timeEffectCode;
    }

    public void setTimeEffectCode(String timeEffectCode) {
        this.timeEffectCode = timeEffectCode;
    }

    public String getTimeEffectName() {
        return timeEffectName;
    }

    public void setTimeEffectName(String timeEffectName) {
        this.timeEffectName = timeEffectName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(String distributionMode) {
        this.distributionMode = distributionMode;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public String getCheckingMode() {
        return checkingMode;
    }

    public void setCheckingMode(String checkingMode) {
        this.checkingMode = checkingMode;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
