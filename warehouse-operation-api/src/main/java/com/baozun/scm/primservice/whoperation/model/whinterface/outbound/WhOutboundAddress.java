/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.whinterface.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOutboundAddress extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 5528955994682786741L;
    
    //columns START
	/** 出库单ID */
	private java.lang.Long outboundId;
	/** 购买人平台账号 */
	private java.lang.String account;
	/** 配送对象姓名 */
	private java.lang.String distributionTargetName;
	/** 配送对象手机 */
	private java.lang.String distributionTargetMobilePhone;
	/** 配送对象固定电话 */
	private java.lang.String distributionTargetTelephone;
	/** 配送对象国家 */
	private java.lang.String distributionTargetCountry;
	/** 配送对象省 */
	private java.lang.String distributionTargetProvince;
	/** 配送对象市 */
	private java.lang.String distributionTargetCity;
	/** 配送对象区 */
	private java.lang.String distributionTargetDistrict;
	/** 配送对象乡镇/街道 */
	private java.lang.String distributionTargetVillagesTowns;
	/** 配送对象详细地址 */
	private java.lang.String distributionTargetAddress;
	/** 配送对象邮箱 */
	private java.lang.String distributionTargetEmail;
	/** 配送对象邮编 */
	private java.lang.String distributionTargetZip;
	/** 收货人姓名 */
	private java.lang.String consigneeTargetName;
	/** 收货人手机 */
	private java.lang.String consigneeTargetMobilePhone;
	/** 收货人固定电话 */
	private java.lang.String consigneeTargetTelephone;
	/** 收货人国家 */
	private java.lang.String consigneeTargetCountry;
	/** 收货人省 */
	private java.lang.String consigneeTargetProvince;
	/** 收货人市 */
	private java.lang.String consigneeTargetCity;
	/** 收货人区 */
	private java.lang.String consigneeTargetDistrict;
	/** 收货人乡镇/街道 */
	private java.lang.String consigneeTargetVillagesTowns;
	/** 收货人详细地址 */
	private java.lang.String consigneeTargetAddress;
	/** 收货人邮箱 */
	private java.lang.String consigneeTargetEmail;
	/** 收货人邮编 */
	private java.lang.String consigneeTargetZip;
	//columns END
	
    public java.lang.Long getOutboundId() {
        return outboundId;
    }
    public void setOutboundId(java.lang.Long outboundId) {
        this.outboundId = outboundId;
    }
    public java.lang.String getAccount() {
        return account;
    }
    public void setAccount(java.lang.String account) {
        this.account = account;
    }
    public java.lang.String getDistributionTargetName() {
        return distributionTargetName;
    }
    public void setDistributionTargetName(java.lang.String distributionTargetName) {
        this.distributionTargetName = distributionTargetName;
    }
    public java.lang.String getDistributionTargetMobilePhone() {
        return distributionTargetMobilePhone;
    }
    public void setDistributionTargetMobilePhone(java.lang.String distributionTargetMobilePhone) {
        this.distributionTargetMobilePhone = distributionTargetMobilePhone;
    }
    public java.lang.String getDistributionTargetTelephone() {
        return distributionTargetTelephone;
    }
    public void setDistributionTargetTelephone(java.lang.String distributionTargetTelephone) {
        this.distributionTargetTelephone = distributionTargetTelephone;
    }
    public java.lang.String getDistributionTargetCountry() {
        return distributionTargetCountry;
    }
    public void setDistributionTargetCountry(java.lang.String distributionTargetCountry) {
        this.distributionTargetCountry = distributionTargetCountry;
    }
    public java.lang.String getDistributionTargetProvince() {
        return distributionTargetProvince;
    }
    public void setDistributionTargetProvince(java.lang.String distributionTargetProvince) {
        this.distributionTargetProvince = distributionTargetProvince;
    }
    public java.lang.String getDistributionTargetCity() {
        return distributionTargetCity;
    }
    public void setDistributionTargetCity(java.lang.String distributionTargetCity) {
        this.distributionTargetCity = distributionTargetCity;
    }
    public java.lang.String getDistributionTargetDistrict() {
        return distributionTargetDistrict;
    }
    public void setDistributionTargetDistrict(java.lang.String distributionTargetDistrict) {
        this.distributionTargetDistrict = distributionTargetDistrict;
    }
    public java.lang.String getDistributionTargetVillagesTowns() {
        return distributionTargetVillagesTowns;
    }
    public void setDistributionTargetVillagesTowns(java.lang.String distributionTargetVillagesTowns) {
        this.distributionTargetVillagesTowns = distributionTargetVillagesTowns;
    }
    public java.lang.String getDistributionTargetAddress() {
        return distributionTargetAddress;
    }
    public void setDistributionTargetAddress(java.lang.String distributionTargetAddress) {
        this.distributionTargetAddress = distributionTargetAddress;
    }
    public java.lang.String getDistributionTargetEmail() {
        return distributionTargetEmail;
    }
    public void setDistributionTargetEmail(java.lang.String distributionTargetEmail) {
        this.distributionTargetEmail = distributionTargetEmail;
    }
    public java.lang.String getDistributionTargetZip() {
        return distributionTargetZip;
    }
    public void setDistributionTargetZip(java.lang.String distributionTargetZip) {
        this.distributionTargetZip = distributionTargetZip;
    }
    public java.lang.String getConsigneeTargetName() {
        return consigneeTargetName;
    }
    public void setConsigneeTargetName(java.lang.String consigneeTargetName) {
        this.consigneeTargetName = consigneeTargetName;
    }
    public java.lang.String getConsigneeTargetMobilePhone() {
        return consigneeTargetMobilePhone;
    }
    public void setConsigneeTargetMobilePhone(java.lang.String consigneeTargetMobilePhone) {
        this.consigneeTargetMobilePhone = consigneeTargetMobilePhone;
    }
    public java.lang.String getConsigneeTargetTelephone() {
        return consigneeTargetTelephone;
    }
    public void setConsigneeTargetTelephone(java.lang.String consigneeTargetTelephone) {
        this.consigneeTargetTelephone = consigneeTargetTelephone;
    }
    public java.lang.String getConsigneeTargetCountry() {
        return consigneeTargetCountry;
    }
    public void setConsigneeTargetCountry(java.lang.String consigneeTargetCountry) {
        this.consigneeTargetCountry = consigneeTargetCountry;
    }
    public java.lang.String getConsigneeTargetProvince() {
        return consigneeTargetProvince;
    }
    public void setConsigneeTargetProvince(java.lang.String consigneeTargetProvince) {
        this.consigneeTargetProvince = consigneeTargetProvince;
    }
    public java.lang.String getConsigneeTargetCity() {
        return consigneeTargetCity;
    }
    public void setConsigneeTargetCity(java.lang.String consigneeTargetCity) {
        this.consigneeTargetCity = consigneeTargetCity;
    }
    public java.lang.String getConsigneeTargetDistrict() {
        return consigneeTargetDistrict;
    }
    public void setConsigneeTargetDistrict(java.lang.String consigneeTargetDistrict) {
        this.consigneeTargetDistrict = consigneeTargetDistrict;
    }
    public java.lang.String getConsigneeTargetVillagesTowns() {
        return consigneeTargetVillagesTowns;
    }
    public void setConsigneeTargetVillagesTowns(java.lang.String consigneeTargetVillagesTowns) {
        this.consigneeTargetVillagesTowns = consigneeTargetVillagesTowns;
    }
    public java.lang.String getConsigneeTargetAddress() {
        return consigneeTargetAddress;
    }
    public void setConsigneeTargetAddress(java.lang.String consigneeTargetAddress) {
        this.consigneeTargetAddress = consigneeTargetAddress;
    }
    public java.lang.String getConsigneeTargetEmail() {
        return consigneeTargetEmail;
    }
    public void setConsigneeTargetEmail(java.lang.String consigneeTargetEmail) {
        this.consigneeTargetEmail = consigneeTargetEmail;
    }
    public java.lang.String getConsigneeTargetZip() {
        return consigneeTargetZip;
    }
    public void setConsigneeTargetZip(java.lang.String consigneeTargetZip) {
        this.consigneeTargetZip = consigneeTargetZip;
    }

}

