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
package com.baozun.scm.primservice.whoperation.model.odo;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoAddress extends BaseModel {

    private static final long serialVersionUID = -3643247043464662187L;

    /** 出库单ID */
    private Long odoId;
    /** 购买人平台账号 */
    private String account;
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
    /** 收货人姓名 */
    private String consigneeTargetName;
    /** 收货人手机 */
    private String consigneeTargetMobilePhone;
    /** 收货人固定电话 */
    private String consigneeTargetTelephone;
    /** 收货人国家 */
    private String consigneeTargetCountry;
    /** 收货人省 */
    private String consigneeTargetProvince;
    /** 收货人市 */
    private String consigneeTargetCity;
    /** 收货人区 */
    private String consigneeTargetDistrict;
    /** 收货人乡镇/街道 */
    private String consigneeTargetVillagesTowns;
    /** 收货人详细地址 */
    private String consigneeTargetAddress;
    /** 收货人邮箱 */
    private String consigneeTargetEmail;
    /** 收货人邮编 */
    private String consigneeTargetZip;
    /** 仓库组织ID */
    private Long ouId;

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDistributionTargetName() {
        return distributionTargetName;
    }

    public void setDistributionTargetName(String distributionTargetName) {
        this.distributionTargetName = distributionTargetName;
    }

    public String getDistributionTargetMobilePhone() {
        return distributionTargetMobilePhone;
    }

    public void setDistributionTargetMobilePhone(String distributionTargetMobilePhone) {
        this.distributionTargetMobilePhone = distributionTargetMobilePhone;
    }

    public String getDistributionTargetTelephone() {
        return distributionTargetTelephone;
    }

    public void setDistributionTargetTelephone(String distributionTargetTelephone) {
        this.distributionTargetTelephone = distributionTargetTelephone;
    }

    public String getDistributionTargetCountry() {
        return distributionTargetCountry;
    }

    public void setDistributionTargetCountry(String distributionTargetCountry) {
        this.distributionTargetCountry = distributionTargetCountry;
    }

    public String getDistributionTargetProvince() {
        return distributionTargetProvince;
    }

    public void setDistributionTargetProvince(String distributionTargetProvince) {
        this.distributionTargetProvince = distributionTargetProvince;
    }

    public String getDistributionTargetCity() {
        return distributionTargetCity;
    }

    public void setDistributionTargetCity(String distributionTargetCity) {
        this.distributionTargetCity = distributionTargetCity;
    }

    public String getDistributionTargetDistrict() {
        return distributionTargetDistrict;
    }

    public void setDistributionTargetDistrict(String distributionTargetDistrict) {
        this.distributionTargetDistrict = distributionTargetDistrict;
    }

    public String getDistributionTargetVillagesTowns() {
        return distributionTargetVillagesTowns;
    }

    public void setDistributionTargetVillagesTowns(String distributionTargetVillagesTowns) {
        this.distributionTargetVillagesTowns = distributionTargetVillagesTowns;
    }

    public String getDistributionTargetAddress() {
        return distributionTargetAddress;
    }

    public void setDistributionTargetAddress(String distributionTargetAddress) {
        this.distributionTargetAddress = distributionTargetAddress;
    }

    public String getDistributionTargetEmail() {
        return distributionTargetEmail;
    }

    public void setDistributionTargetEmail(String distributionTargetEmail) {
        this.distributionTargetEmail = distributionTargetEmail;
    }

    public String getDistributionTargetZip() {
        return distributionTargetZip;
    }

    public void setDistributionTargetZip(String distributionTargetZip) {
        this.distributionTargetZip = distributionTargetZip;
    }

    public String getConsigneeTargetName() {
        return consigneeTargetName;
    }

    public void setConsigneeTargetName(String consigneeTargetName) {
        this.consigneeTargetName = consigneeTargetName;
    }

    public String getConsigneeTargetMobilePhone() {
        return consigneeTargetMobilePhone;
    }

    public void setConsigneeTargetMobilePhone(String consigneeTargetMobilePhone) {
        this.consigneeTargetMobilePhone = consigneeTargetMobilePhone;
    }

    public String getConsigneeTargetTelephone() {
        return consigneeTargetTelephone;
    }

    public void setConsigneeTargetTelephone(String consigneeTargetTelephone) {
        this.consigneeTargetTelephone = consigneeTargetTelephone;
    }

    public String getConsigneeTargetCountry() {
        return consigneeTargetCountry;
    }

    public void setConsigneeTargetCountry(String consigneeTargetCountry) {
        this.consigneeTargetCountry = consigneeTargetCountry;
    }

    public String getConsigneeTargetProvince() {
        return consigneeTargetProvince;
    }

    public void setConsigneeTargetProvince(String consigneeTargetProvince) {
        this.consigneeTargetProvince = consigneeTargetProvince;
    }

    public String getConsigneeTargetCity() {
        return consigneeTargetCity;
    }

    public void setConsigneeTargetCity(String consigneeTargetCity) {
        this.consigneeTargetCity = consigneeTargetCity;
    }

    public String getConsigneeTargetDistrict() {
        return consigneeTargetDistrict;
    }

    public void setConsigneeTargetDistrict(String consigneeTargetDistrict) {
        this.consigneeTargetDistrict = consigneeTargetDistrict;
    }

    public String getConsigneeTargetVillagesTowns() {
        return consigneeTargetVillagesTowns;
    }

    public void setConsigneeTargetVillagesTowns(String consigneeTargetVillagesTowns) {
        this.consigneeTargetVillagesTowns = consigneeTargetVillagesTowns;
    }

    public String getConsigneeTargetAddress() {
        return consigneeTargetAddress;
    }

    public void setConsigneeTargetAddress(String consigneeTargetAddress) {
        this.consigneeTargetAddress = consigneeTargetAddress;
    }

    public String getConsigneeTargetEmail() {
        return consigneeTargetEmail;
    }

    public void setConsigneeTargetEmail(String consigneeTargetEmail) {
        this.consigneeTargetEmail = consigneeTargetEmail;
    }

    public String getConsigneeTargetZip() {
        return consigneeTargetZip;
    }

    public void setConsigneeTargetZip(String consigneeTargetZip) {
        this.consigneeTargetZip = consigneeTargetZip;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
