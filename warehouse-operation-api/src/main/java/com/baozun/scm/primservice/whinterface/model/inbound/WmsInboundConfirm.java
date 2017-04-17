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
package com.baozun.scm.primservice.whinterface.model.inbound;

import java.io.Serializable;
import java.util.List;

/**
 * 入库单反馈
 *
 */
public class WmsInboundConfirm implements Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 3690382656230193243L;

    /** 数据唯一标识 */
    private String uuid;
    /** 上位系统平台单据号 */
    private String extPoCode;
    /** 上位系统入库单据号 */
    private String extCode;
    /** 客户编码 */
    private String customerCode;
    /** 店铺编码 */
    private String storeCode;
    /** 来源地 */
    private String fromLocation;
    /** 目的地 */
    private String toLocation;
    /** 实际到货时间格式：年（4位）月（2位）日（2位）时（2位）分（2位）秒（2位） */
    private String deliveryTimeStr;
    /** 仓库编码 */
    private String whCode;
    /** 单据状态 */
    private String poStatus;
    /** 单据类型 */
    private String poType;
    /** 上位系统单据类型 */
    private String extPoType;
    /** 是否质检 默认否 */
    private Boolean isIqc;
    /** 计划数量 */
    private Double qtyPlanned;
    /** 实际到货数量 */
    private Double qtyRcvd;
    /** 计划箱数 */
    private Integer ctnPlanned;
    /** 实际到货箱数 */
    private Integer ctnRcvd;
    /** 扩展字段信息 */
    private String extMemo;
    /** 数据来源 区分上位系统 */
    private String dataSource;
    /** 入库单反馈明细 */
    private List<WmsInboundLineConfirm> wmsInBoundConfirmLines;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getExtPoCode() {
        return extPoCode;
    }

    public void setExtPoCode(String extPoCode) {
        this.extPoCode = extPoCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
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

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }
    
    public String getDeliveryTimeStr() {
        return deliveryTimeStr;
    }

    public void setDeliveryTimeStr(String deliveryTimeStr) {
        this.deliveryTimeStr = deliveryTimeStr;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public String getPoStatus() {
        return poStatus;
    }

    public void setPoStatus(String poStatus) {
        this.poStatus = poStatus;
    }

    public String getPoType() {
        return poType;
    }

    public void setPoType(String poType) {
        this.poType = poType;
    }

    public Boolean getIsIqc() {
        return isIqc;
    }

    public void setIsIqc(Boolean isIqc) {
        this.isIqc = isIqc;
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
        return ctnPlanned;
    }

    public void setCtnPlanned(Integer ctnPlanned) {
        this.ctnPlanned = ctnPlanned;
    }

    public Integer getCtnRcvd() {
        return ctnRcvd;
    }

    public void setCtnRcvd(Integer ctnRcvd) {
        this.ctnRcvd = ctnRcvd;
    }

    public String getExtMemo() {
        return extMemo;
    }

    public void setExtMemo(String extMemo) {
        this.extMemo = extMemo;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getExtPoType() {
        return extPoType;
    }

    public void setExtPoType(String extPoType) {
        this.extPoType = extPoType;
    }

    public List<WmsInboundLineConfirm> getWmsInBoundConfirmLines() {
        return wmsInBoundConfirmLines;
    }

    public void setWmsInBoundConfirmLines(List<WmsInboundLineConfirm> wmsInBoundConfirmLines) {
        this.wmsInBoundConfirmLines = wmsInBoundConfirmLines;
    }

}
