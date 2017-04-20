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
package com.baozun.scm.primservice.whoperation.model.warehouse.ma;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 * 
 */
public class DistributionTarget extends BaseModel {

    private static final long serialVersionUID = 1008408724540833669L;


    /** 出库目标类型 */
    private String type;
    /** 出库目标编码 */
    private String code;
    /** 配送对象姓名 */
    private String name;
    /** 配送对象手机 */
    private String mobilePhone;
    /** 配送对象固定电话 */
    private String telephone;
    /** 配送对象国家 */
    private Long country;
    /** 配送对象省 */
    private Long province;
    /** 配送对象市 */
    private Long city;
    /** 配送对象区 */
    private Long district;
    /** 配送对象乡镇/街道 */
    private Long villagesTowns;
    /** 配送对象详细地址 */
    private String address;
    /** 配送对象邮箱 */
    private String email;
    /** 配送对象邮编 */
    private String zip;
    /** 线路信息 */
    private String lineInfo;
    /** 顺序号 */
    private Long sequence;
    /** 1.可用;0.禁用 */
    private Integer lifecycle;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;
    /** 用于全局表最后修改时间统一 */
    private Date globalLastModifyTime;
    /** 配送对象分组 */
    private String distributionTargetGroup;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getCountry() {
        return country;
    }

    public void setCountry(Long country) {
        this.country = country;
    }

    public Long getProvince() {
        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

    public Long getCity() {
        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    public Long getDistrict() {
        return district;
    }

    public void setDistrict(Long district) {
        this.district = district;
    }

    public Long getVillagesTowns() {
        return villagesTowns;
    }

    public void setVillagesTowns(Long villagesTowns) {
        this.villagesTowns = villagesTowns;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLineInfo() {
        return lineInfo;
    }

    public void setLineInfo(String lineInfo) {
        this.lineInfo = lineInfo;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
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

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public Date getGlobalLastModifyTime() {
        return globalLastModifyTime;
    }

    public void setGlobalLastModifyTime(Date globalLastModifyTime) {
        this.globalLastModifyTime = globalLastModifyTime;
    }

    public String getDistributionTargetGroup() {
        return distributionTargetGroup;
    }

    public void setDistributionTargetGroup(String distributionTargetGroup) {
        this.distributionTargetGroup = distributionTargetGroup;
    }

}
