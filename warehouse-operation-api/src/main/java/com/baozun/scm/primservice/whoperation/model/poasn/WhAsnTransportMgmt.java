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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhAsnTransportMgmt extends BaseModel {

    private static final long serialVersionUID = -3483772991382433091L;

    // columns START
    /** asn单id */
    private Long asnId;
    /** 运输服务商 */
    private String transportServiceProvider;
    /** 快递单号 */
    private String trackingNumber;
    /** 寄件人姓名 */
    private String senderTargetName;
    /** 寄件人手机 */
    private String senderTargetMobilePhone;
    /** 寄件人固定电话 */
    private String senderTargetTelephone;
    /** 寄件人国家 */
    private String senderTargetCountry;
    /** 寄件人省 */
    private String senderTargetProvince;
    /** 寄件人市 */
    private String senderTargetCity;
    /** 寄件人区 */
    private String senderTargetDistrict;
    /** 寄件人乡镇/街道 */
    private String senderTargetVillagesTowns;
    /** 寄件人详细地址 */
    private String senderTargetAddress;
    /** 寄件人邮箱 */
    private String senderTargetEmail;
    /** 寄件人邮编 */
    private String senderTargetZip;
    /** 仓库组织ID */
    private Long ouId;
    /** 当前月份 用于归档 */
    private String sysDate;

    // columns END

    public WhAsnTransportMgmt() {}

    public WhAsnTransportMgmt(Long id) {
        this.id = id;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public Long getAsnId() {
        return this.asnId;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getTransportServiceProvider() {
        return this.transportServiceProvider;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public void setSenderTargetName(String senderTargetName) {
        this.senderTargetName = senderTargetName;
    }

    public String getSenderTargetName() {
        return this.senderTargetName;
    }

    public void setSenderTargetMobilePhone(String senderTargetMobilePhone) {
        this.senderTargetMobilePhone = senderTargetMobilePhone;
    }

    public String getSenderTargetMobilePhone() {
        return this.senderTargetMobilePhone;
    }

    public void setSenderTargetTelephone(String senderTargetTelephone) {
        this.senderTargetTelephone = senderTargetTelephone;
    }

    public String getSenderTargetTelephone() {
        return this.senderTargetTelephone;
    }

    public void setSenderTargetCountry(String senderTargetCountry) {
        this.senderTargetCountry = senderTargetCountry;
    }

    public String getSenderTargetCountry() {
        return this.senderTargetCountry;
    }

    public void setSenderTargetProvince(String senderTargetProvince) {
        this.senderTargetProvince = senderTargetProvince;
    }

    public String getSenderTargetProvince() {
        return this.senderTargetProvince;
    }

    public void setSenderTargetCity(String senderTargetCity) {
        this.senderTargetCity = senderTargetCity;
    }

    public String getSenderTargetCity() {
        return this.senderTargetCity;
    }

    public void setSenderTargetDistrict(String senderTargetDistrict) {
        this.senderTargetDistrict = senderTargetDistrict;
    }

    public String getSenderTargetDistrict() {
        return this.senderTargetDistrict;
    }

    public void setSenderTargetVillagesTowns(String senderTargetVillagesTowns) {
        this.senderTargetVillagesTowns = senderTargetVillagesTowns;
    }

    public String getSenderTargetVillagesTowns() {
        return this.senderTargetVillagesTowns;
    }

    public void setSenderTargetAddress(String senderTargetAddress) {
        this.senderTargetAddress = senderTargetAddress;
    }

    public String getSenderTargetAddress() {
        return this.senderTargetAddress;
    }

    public void setSenderTargetEmail(String senderTargetEmail) {
        this.senderTargetEmail = senderTargetEmail;
    }

    public String getSenderTargetEmail() {
        return this.senderTargetEmail;
    }

    public void setSenderTargetZip(String senderTargetZip) {
        this.senderTargetZip = senderTargetZip;
    }

    public String getSenderTargetZip() {
        return this.senderTargetZip;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }


}
