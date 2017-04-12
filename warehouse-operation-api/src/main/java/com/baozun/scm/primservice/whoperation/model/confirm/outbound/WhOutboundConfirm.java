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

import java.util.Date;
import java.util.List;

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
    /** 运输服务商-快递单号 */
    private String transportServiceProvider;
    /** 出库单状态 */
    private Integer wmsOdoStatus;
    /** 客户CODE */
    private String customerCode;
    /** 店铺CODE */
    private String storeCode;
    /** 仓库组织ID */
    private Long ouId;
    /** 数据来源 区分上位系统 */
    private String dataSource;
    /** 是否整单出库完成 默认是 */
    private Boolean isOutboundFinish = true;
    /** 创建时间 */
    private Date createTime;

    /** 出库单反馈明细 */
    private List<WhOutboundLineConfirm> whOutBoundLineConfirm;
    /** 出库单发票信息 */
    private List<WhOutboundInvoiceConfirm> whOutBoundInvoiceConfirm;

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

    public Boolean getIsOutboundFinish() {
        return isOutboundFinish;
    }

    public void setIsOutboundFinish(Boolean isOutboundFinish) {
        this.isOutboundFinish = isOutboundFinish;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<WhOutboundLineConfirm> getWhOutBoundLineConfirm() {
        return whOutBoundLineConfirm;
    }

    public void setWhOutBoundLineConfirm(List<WhOutboundLineConfirm> whOutBoundLineConfirm) {
        this.whOutBoundLineConfirm = whOutBoundLineConfirm;
    }

    public List<WhOutboundInvoiceConfirm> getWhOutBoundInvoiceConfirm() {
        return whOutBoundInvoiceConfirm;
    }

    public void setWhOutBoundInvoiceConfirm(List<WhOutboundInvoiceConfirm> whOutBoundInvoiceConfirm) {
        this.whOutBoundInvoiceConfirm = whOutBoundInvoiceConfirm;
    }



}
