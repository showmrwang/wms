package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoAddressCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -6732342642534846712L;
    /***/
    private Long id;
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
    // -----------------------------------------------------------------------------
    // 自定义字段
    /** 出库目标对象 */
    private String outboundTargetType;
    /** USERID */
    private Long userId;

    /** 配送人区号 */
    private String distributionTargetTelephoneCode;
    /** 配送人号码 */
    private String distributionTargetTelephoneNumber;
    /** 配送人分机号 */
    private String distributionTargetTelephoneDivision;
    /** 收货人区号 */
    private String consigneeTargetTelephoneCode;
    /** 收货人号码 */
    private String consigneeTargetTelephoneNumber;
    /** 收货人分机号 */
    private String consigneeTargetTelephoneDivision;

    private String odoExtCode;


    public String getOdoExtCode() {
        return odoExtCode;
    }

    public void setOdoExtCode(String odoExtCode) {
        this.odoExtCode = odoExtCode;
    }

    public String getConsigneeTargetTelephoneCode() {
        return consigneeTargetTelephoneCode;
    }

    public void setConsigneeTargetTelephoneCode(String consigneeTargetTelephoneCode) {
        this.consigneeTargetTelephoneCode = consigneeTargetTelephoneCode;
    }

    public String getConsigneeTargetTelephoneNumber() {
        return consigneeTargetTelephoneNumber;
    }

    public void setConsigneeTargetTelephoneNumber(String consigneeTargetTelephoneNumber) {
        this.consigneeTargetTelephoneNumber = consigneeTargetTelephoneNumber;
    }

    public String getConsigneeTargetTelephoneDivision() {
        return consigneeTargetTelephoneDivision;
    }

    public void setConsigneeTargetTelephoneDivision(String consigneeTargetTelephoneDivision) {
        this.consigneeTargetTelephoneDivision = consigneeTargetTelephoneDivision;
    }

    public String getDistributionTargetTelephoneCode() {
        return distributionTargetTelephoneCode;
    }

    public void setDistributionTargetTelephoneCode(String distributionTargetTelephoneCode) {
        this.distributionTargetTelephoneCode = distributionTargetTelephoneCode;
    }

    public String getDistributionTargetTelephoneNumber() {
        return distributionTargetTelephoneNumber;
    }

    public void setDistributionTargetTelephoneNumber(String distributionTargetTelephoneNumber) {
        this.distributionTargetTelephoneNumber = distributionTargetTelephoneNumber;
    }

    public String getDistributionTargetTelephoneDivision() {
        return distributionTargetTelephoneDivision;
    }

    public void setDistributionTargetTelephoneDivision(String distributionTargetTelephoneDivision) {
        this.distributionTargetTelephoneDivision = distributionTargetTelephoneDivision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOutboundTargetType() {
        return outboundTargetType;
    }

    public void setOutboundTargetType(String outboundTargetType) {
        this.outboundTargetType = outboundTargetType;
    }

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
