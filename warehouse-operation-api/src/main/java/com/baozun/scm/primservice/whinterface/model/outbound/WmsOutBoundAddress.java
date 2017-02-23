package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

public class WmsOutBoundAddress implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6474224643603081227L;

    private String  account ;// 购买人平台账号
    private String  distributionTargetName  ;// 配送对象姓名
    private String  distributionTargetMobilePhone   ;// 配送对象手机
    private String  distributionTargetTelephone ;// 配送对象固定电话;手机&固定电话必填一个
    private String  distributionTargetCountry   ;// 配送对象国家
    private String  distributionTargetProvince  ;// 配送对象省
    private String  distributionTargetCity  ;// 配送对象市
    private String  distributionTargetDistrict  ;// 配送对象区
    private String  distributionTargetVillagesTowns ;// 配送对象乡镇/街道
    private String  distributionTargetAddress   ;// 配送对象详细地址
    private String  distributionTargetEmail ;// 配送对象邮箱
    private String  distributionTargetZip   ;// 配送对象邮编
    private String  consigneeTargetName ;// 收货人姓名
    private String  consigneeTargetMobilePhone  ;// 收货人手机
    private String  consigneeTargetTelephone    ;// 收货人固定电话;手机&固定电话必填一个
    private String  consigneeTargetCountry  ;// 收货人国家
    private String  consigneeTargetProvince ;// 收货人省
    private String  consigneeTargetCity ;// 收货人市
    private String  consigneeTargetDistrict ;// 收货人区
    private String  consigneeTargetVillagesTowns    ;// 收货人乡镇/街道
    private String  consigneeTargetAddress  ;// 收货人详细地址
    private String  consigneeTargetEmail    ;// 收货人邮箱
    private String  consigneeTargetZip  ;// 收货人邮编
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
    

}
