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
package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;
import java.util.List;

/**
 * 出库单反馈信息
 *
 */
public class WmsOutboundConfirm implements Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 1234575103500528426L;
    /** 上位系统出库单号 */
    private String extOdoCode;
    /** WMS出库单号 */
    private String wmsOdoCode;
    /** 上位系统出库单类型 */
    private String extOdoType;
    /** 运输服务商-快递单号 */
    private List<String> transportServiceProvider;
    /** 出库单状态 */
    private Integer wmsOdoStatus;
    /** 客户CODE */
    private String customerCode;
    /** 店铺CODE */
    private String storeCode;
    /** 仓库组织Code */
    private String whCode;
    /** 数据来源 区分上位系统 */
    private String dataSource;
    /** 是否整单出库完成 默认是 */
    private Boolean isOutboundFinish;
    /** 出库单反馈明细 */
    private List<WmsOutboundLineConfirm> wmsOutBoundLineConfirm;
    /** 出库单发票信息 */
    private List<WmsOutboundInvoiceConfirm> wmsOutBoundInvoiceConfirm;

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

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(List<String> transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public Boolean getIsOutboundFinish() {
        return isOutboundFinish;
    }

    public void setIsOutboundFinish(Boolean isOutboundFinish) {
        this.isOutboundFinish = isOutboundFinish;
    }

    public List<WmsOutboundLineConfirm> getWmsOutBoundLineConfirm() {
        return wmsOutBoundLineConfirm;
    }

    public void setWmsOutBoundLineConfirm(List<WmsOutboundLineConfirm> wmsOutBoundLineConfirm) {
        this.wmsOutBoundLineConfirm = wmsOutBoundLineConfirm;
    }

    public List<WmsOutboundInvoiceConfirm> getWmsOutBoundInvoiceConfirm() {
        return wmsOutBoundInvoiceConfirm;
    }

    public void setWmsOutBoundInvoiceConfirm(List<WmsOutboundInvoiceConfirm> wmsOutBoundInvoiceConfirm) {
        this.wmsOutBoundInvoiceConfirm = wmsOutBoundInvoiceConfirm;
    }



}
