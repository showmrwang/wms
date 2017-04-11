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
package com.baozun.scm.primservice.whoperation.model.confirm.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库单反馈信息
 *
 */
public class WhOutboundConfirm extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -8596808927374075521L;

    /** 上位系统出库单号 */
    private String extOdoCode;
    /** WMS出库单号 */
    private String wmsOdoCode;
    /** 上位系统出库单类型 */
    private String extOdoType;
    /** 运输服务商 */
    private String transportServiceProvider;
    /** 快递单号 */
    private String trackingNumber;
    /** 出库单状态 */
    private Integer wmsOdoStatus;
    /** 客户CODE */
    private String customerCode;
    /** 店铺CODE */
    private String storeCode;
    /** 仓库编码 */
    private String ouCode;
    /** 仓库组织ID */
    private Long ouId;
    /** 数据来源 区分上位系统 */
    private String dataSource;

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }

    public String getWmsOdoCode() {
        return wmsOdoCode;
    }

    public void setWmsOdoCode(String wmsOdoCode) {
        this.wmsOdoCode = wmsOdoCode;
    }

    public String getExtOdoType() {
        return extOdoType;
    }

    public void setExtOdoType(String extOdoType) {
        this.extOdoType = extOdoType;
    }

    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Integer getWmsOdoStatus() {
        return wmsOdoStatus;
    }

    public void setWmsOdoStatus(Integer wmsOdoStatus) {
        this.wmsOdoStatus = wmsOdoStatus;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getOuCode() {
        return ouCode;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }



}
