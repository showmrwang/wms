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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 *
 * @author larkark
 *
 */
public class Customer extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -1496164410394233363L;

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
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long operatorId;
    /** 1.可用;0.禁用 */
    private Integer lifecycle;
    /** 联系手机 */
    private String picMobileTelephone;
    /** 国家ID */
    private Long countryId;
    /** 省ID */
    private Long provinceId;
    /** 市ID */
    private Long cityId;
    /** 详细地址 */
    private String address;
    /** 邮政编码 */
    private String zipCode;
    /** 邮箱 */
    private String email;
    /** 乡镇/街道 */
    private Long villagesTownsId;
    /** 区ID */
    private Long districtId;
    /** 用于全局表最后修改时间统一 */
    private Date globalLastModifyTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Date getGlobalLastModifyTime() {
        return globalLastModifyTime;
    }

    public void setGlobalLastModifyTime(Date globalLastModifyTime) {
        this.globalLastModifyTime = globalLastModifyTime;
    }

    public String getPicMobileTelephone() {
        return picMobileTelephone;
    }

    public void setPicMobileTelephone(String picMobileTelephone) {
        this.picMobileTelephone = picMobileTelephone;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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

    public Long getVillagesTownsId() {
        return villagesTownsId;
    }

    public void setVillagesTownsId(Long villagesTownsId) {
        this.villagesTownsId = villagesTownsId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

}
