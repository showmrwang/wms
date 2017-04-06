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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * t_wh_invoice_address
 * @author larkark
 *
 */
public class WhInvoiceAddress extends BaseModel {

    private static final long serialVersionUID = -7166816863394686243L;
    
    // columns START
    /** 发票头ID */
    private Long whInvoiceId;
    /** 配送对象姓名 */
    private String distributionTargetName;
    /** 配送对象手机 */
    private String distributionTargetMobilePhone;
    /** 配送对象固定电话 */
    private String distributionTargetTelephone;
    /** 配送对象国家 */
    private String distributionTargetCountry;
    /** 配送对象省 */
    private String distributionTargetProvince;
    /** 配送对象市 */
    private String distributionTargetCity;
    /** 配送对象区 */
    private String distributionTargetDistrict;
    /** 配送对象乡镇/街道 */
    private String distributionTargetVillagesTowns;
    /** 配送对象详细地址 */
    private String distributionTargetAddress;
    /** 配送对象邮箱 */
    private String distributionTargetEmail;
    /** 配送对象邮编 */
    private String distributionTargetZip;
    /** 物流商编码 */
    private String transportCode;
    /** 运单号 */
    private String waybillCode;
    /** 仓库组织ID */
    private Long ouId;
    // columns END

    public WhInvoiceAddress() {}

    public WhInvoiceAddress(Long id) {
        this.id = id;
    }

    public void setWhInvoiceId(Long whInvoiceId) {
        this.whInvoiceId = whInvoiceId;
    }

    public Long getWhInvoiceId() {
        return this.whInvoiceId;
    }

    public void setDistributionTargetName(String distributionTargetName) {
        this.distributionTargetName = distributionTargetName;
    }

    public String getDistributionTargetName() {
        return this.distributionTargetName;
    }

    public void setDistributionTargetMobilePhone(String distributionTargetMobilePhone) {
        this.distributionTargetMobilePhone = distributionTargetMobilePhone;
    }

    public String getDistributionTargetMobilePhone() {
        return this.distributionTargetMobilePhone;
    }

    public void setDistributionTargetTelephone(String distributionTargetTelephone) {
        this.distributionTargetTelephone = distributionTargetTelephone;
    }

    public String getDistributionTargetTelephone() {
        return this.distributionTargetTelephone;
    }

    public void setDistributionTargetCountry(String distributionTargetCountry) {
        this.distributionTargetCountry = distributionTargetCountry;
    }

    public String getDistributionTargetCountry() {
        return this.distributionTargetCountry;
    }

    public void setDistributionTargetProvince(String distributionTargetProvince) {
        this.distributionTargetProvince = distributionTargetProvince;
    }

    public String getDistributionTargetProvince() {
        return this.distributionTargetProvince;
    }

    public void setDistributionTargetCity(String distributionTargetCity) {
        this.distributionTargetCity = distributionTargetCity;
    }

    public String getDistributionTargetCity() {
        return this.distributionTargetCity;
    }

    public void setDistributionTargetDistrict(String distributionTargetDistrict) {
        this.distributionTargetDistrict = distributionTargetDistrict;
    }

    public String getDistributionTargetDistrict() {
        return this.distributionTargetDistrict;
    }

    public void setDistributionTargetVillagesTowns(String distributionTargetVillagesTowns) {
        this.distributionTargetVillagesTowns = distributionTargetVillagesTowns;
    }

    public String getDistributionTargetVillagesTowns() {
        return this.distributionTargetVillagesTowns;
    }

    public void setDistributionTargetAddress(String distributionTargetAddress) {
        this.distributionTargetAddress = distributionTargetAddress;
    }

    public String getDistributionTargetAddress() {
        return this.distributionTargetAddress;
    }

    public void setDistributionTargetEmail(String distributionTargetEmail) {
        this.distributionTargetEmail = distributionTargetEmail;
    }

    public String getDistributionTargetEmail() {
        return this.distributionTargetEmail;
    }

    public void setDistributionTargetZip(String distributionTargetZip) {
        this.distributionTargetZip = distributionTargetZip;
    }

    public String getDistributionTargetZip() {
        return this.distributionTargetZip;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransportCode() {
        return this.transportCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }
}

