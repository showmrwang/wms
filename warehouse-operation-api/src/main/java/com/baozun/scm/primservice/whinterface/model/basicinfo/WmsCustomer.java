package com.baozun.scm.primservice.whinterface.model.basicinfo;

import java.io.Serializable;

public class WmsCustomer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7886777371721614630L;
    /** 客户编号 */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 描述 */
    private String description;
    /** 联系人 */
    private String pic;
    /** 联系人电话 */
    private String picContact;
    /** 客户类型 */
    private String customerType;
    /** 发票类型 */
    private String invoiceType;
    /** 结算方式 */
    private String paymentTerm;
    /** 联系手机 */
    private String picMobileTelephone;
    /*
     * 国家
     */
    private String countryName;
    private String countryCode;
    /*
     * 省
     */
    private String provinceName;
    private String provinceCode;
    /*
     * 市
     */
    private String cityName;
    private String cityCode;
    /*
     * 详细地址
     */
    private String address;
    /*
     * 邮政编码
     */
    private String zipCode;
    /*
     * 邮箱
     */
    private String email;
    /*
     * 乡镇/街道
     */
    private String villagesTownsName;
    private String villagesTownsCode;
    /*
     * 区
     */
    private String districtName;
    private String districtCode;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPicContact() {
        return picContact;
    }

    public void setPicContact(String picContact) {
        this.picContact = picContact;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getPicMobileTelephone() {
        return picMobileTelephone;
    }

    public void setPicMobileTelephone(String picMobileTelephone) {
        this.picMobileTelephone = picMobileTelephone;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVillagesTownsName() {
        return villagesTownsName;
    }

    public void setVillagesTownsName(String villagesTownsName) {
        this.villagesTownsName = villagesTownsName;
    }

    public String getVillagesTownsCode() {
        return villagesTownsCode;
    }

    public void setVillagesTownsCode(String villagesTownsCode) {
        this.villagesTownsCode = villagesTownsCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

}
