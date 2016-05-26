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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 收货功能
 * 
 * @author larkark
 * 
 */
public class WhFunctionRcvdCommand extends BaseCommand {


    private static final long serialVersionUID = 767647537129203332L;

    /** 主键ID */
    private Long id;
    /** 对应功能ID */
    private Long functionId;
    /** 收货模式 货箱收货 托盘收货 */
    private Integer rcvdPattern;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 跳转功能链接 */
    private Long skipUrl;
    /** 标签锁 */
    private String tagLock;
    /** 库存类型 */
    private String invType;
    /** 库存状态 */
    private Long invStatus;
    /** 按照标准装箱提示收货 */
    private Boolean normIncPointoutRcvd;
    /** 是否限定唯一批次号 */
    private Boolean isLimitUniqueBatch;
    /** 是否限定唯一库存类型 */
    private Boolean isLimitUniqueInvType;
    /** 是否限定唯一库存状态 */
    private Boolean isLimitUniqueInvStatus;
    /** 是否限定唯一库存属性1 */
    private Boolean isLimitUniqueInvAttr1;
    /** 是否限定唯一库存属性2 */
    private Boolean isLimitUniqueInvAttr2;
    /** 是否限定唯一库存属性3 */
    private Boolean isLimitUniqueInvAttr3;
    /** 是否限定唯一库存属性4 */
    private Boolean isLimitUniqueInvAttr4;
    /** 是否限定唯一库存属性5 */
    private Boolean isLimitUniqueInvAttr5;
    /** 是否限定唯一原产地 */
    private Boolean isLimitUniquePlaceoforigin;
    /** 是否限定唯一生产日期 */
    private Boolean isLimitUniqueDateOfManufacture;
    /** 是否限定唯一失效日期 */
    private Boolean isLimitUniqueExpiryDate;
    /** 是否支持混放SKU */
    private Boolean isMixingSku;
    /** 对应组织ID */
    private Long ouId;
    /** 已知库存属性的ASN是否需要提示用户 */
    private Boolean isInvattrAsnPointoutUser;
    /** 库存属性差异是否允许收货 */
    private Boolean isInvattrDiscrepancyAllowrcvd;
    /** CASELEVEL是否需要扫描SKU */
    private Boolean isCaselevelScanSku;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Integer getRcvdPattern() {
        return rcvdPattern;
    }

    public void setRcvdPattern(Integer rcvdPattern) {
        this.rcvdPattern = rcvdPattern;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Long getSkipUrl() {
        return skipUrl;
    }

    public void setSkipUrl(Long skipUrl) {
        this.skipUrl = skipUrl;
    }

    public String getTagLock() {
        return tagLock;
    }

    public void setTagLock(String tagLock) {
        this.tagLock = tagLock;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public Boolean getNormIncPointoutRcvd() {
        return normIncPointoutRcvd;
    }

    public void setNormIncPointoutRcvd(Boolean normIncPointoutRcvd) {
        this.normIncPointoutRcvd = normIncPointoutRcvd;
    }

    public Boolean getIsLimitUniqueBatch() {
        return isLimitUniqueBatch;
    }

    public void setIsLimitUniqueBatch(Boolean isLimitUniqueBatch) {
        this.isLimitUniqueBatch = isLimitUniqueBatch;
    }

    public Boolean getIsLimitUniqueInvType() {
        return isLimitUniqueInvType;
    }

    public void setIsLimitUniqueInvType(Boolean isLimitUniqueInvType) {
        this.isLimitUniqueInvType = isLimitUniqueInvType;
    }

    public Boolean getIsLimitUniqueInvStatus() {
        return isLimitUniqueInvStatus;
    }

    public void setIsLimitUniqueInvStatus(Boolean isLimitUniqueInvStatus) {
        this.isLimitUniqueInvStatus = isLimitUniqueInvStatus;
    }

    public Boolean getIsLimitUniqueInvAttr1() {
        return isLimitUniqueInvAttr1;
    }

    public void setIsLimitUniqueInvAttr1(Boolean isLimitUniqueInvAttr1) {
        this.isLimitUniqueInvAttr1 = isLimitUniqueInvAttr1;
    }

    public Boolean getIsLimitUniqueInvAttr2() {
        return isLimitUniqueInvAttr2;
    }

    public void setIsLimitUniqueInvAttr2(Boolean isLimitUniqueInvAttr2) {
        this.isLimitUniqueInvAttr2 = isLimitUniqueInvAttr2;
    }

    public Boolean getIsLimitUniqueInvAttr3() {
        return isLimitUniqueInvAttr3;
    }

    public void setIsLimitUniqueInvAttr3(Boolean isLimitUniqueInvAttr3) {
        this.isLimitUniqueInvAttr3 = isLimitUniqueInvAttr3;
    }

    public Boolean getIsLimitUniqueInvAttr4() {
        return isLimitUniqueInvAttr4;
    }

    public void setIsLimitUniqueInvAttr4(Boolean isLimitUniqueInvAttr4) {
        this.isLimitUniqueInvAttr4 = isLimitUniqueInvAttr4;
    }

    public Boolean getIsLimitUniqueInvAttr5() {
        return isLimitUniqueInvAttr5;
    }

    public void setIsLimitUniqueInvAttr5(Boolean isLimitUniqueInvAttr5) {
        this.isLimitUniqueInvAttr5 = isLimitUniqueInvAttr5;
    }

    public Boolean getIsLimitUniquePlaceoforigin() {
        return isLimitUniquePlaceoforigin;
    }

    public void setIsLimitUniquePlaceoforigin(Boolean isLimitUniquePlaceoforigin) {
        this.isLimitUniquePlaceoforigin = isLimitUniquePlaceoforigin;
    }

    public Boolean getIsLimitUniqueDateOfManufacture() {
        return isLimitUniqueDateOfManufacture;
    }

    public void setIsLimitUniqueDateOfManufacture(Boolean isLimitUniqueDateOfManufacture) {
        this.isLimitUniqueDateOfManufacture = isLimitUniqueDateOfManufacture;
    }

    public Boolean getIsLimitUniqueExpiryDate() {
        return isLimitUniqueExpiryDate;
    }

    public void setIsLimitUniqueExpiryDate(Boolean isLimitUniqueExpiryDate) {
        this.isLimitUniqueExpiryDate = isLimitUniqueExpiryDate;
    }

    public Boolean getIsMixingSku() {
        return isMixingSku;
    }

    public void setIsMixingSku(Boolean isMixingSku) {
        this.isMixingSku = isMixingSku;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Boolean getIsInvattrAsnPointoutUser() {
        return isInvattrAsnPointoutUser;
    }

    public void setIsInvattrAsnPointoutUser(Boolean isInvattrAsnPointoutUser) {
        this.isInvattrAsnPointoutUser = isInvattrAsnPointoutUser;
    }

    public Boolean getIsInvattrDiscrepancyAllowrcvd() {
        return isInvattrDiscrepancyAllowrcvd;
    }

    public void setIsInvattrDiscrepancyAllowrcvd(Boolean isInvattrDiscrepancyAllowrcvd) {
        this.isInvattrDiscrepancyAllowrcvd = isInvattrDiscrepancyAllowrcvd;
    }

    public Boolean getIsCaselevelScanSku() {
        return isCaselevelScanSku;
    }

    public void setIsCaselevelScanSku(Boolean isCaselevelScanSku) {
        this.isCaselevelScanSku = isCaselevelScanSku;
    }



}
