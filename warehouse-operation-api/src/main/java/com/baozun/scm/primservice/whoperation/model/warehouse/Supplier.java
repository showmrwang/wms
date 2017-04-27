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
public class Supplier extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7316684834078643250L;

    /** 供应商编码 */
    private String code;
    /** 供应商名称 */
    private String name;
    /** 类型 */
    private String type;
    /** 等级 */
    private String level;
    /** 联系人 */
    private String user;
    /** 固定电话 */
    private String contact;
    /** 所属客户 */
    private Long customerId;
    /** 1.可用;0.禁用*/
    private Integer lifecycle;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    
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
    
    /*
     * 用于全局表最后修改时间统一
     */
    private Date globalLastModifyTime ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
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

    public Date getGlobalLastModifyTime() {
        return globalLastModifyTime;
    }

    public void setGlobalLastModifyTime(Date globalLastModifyTime) {
        this.globalLastModifyTime = globalLastModifyTime;
    }

}
