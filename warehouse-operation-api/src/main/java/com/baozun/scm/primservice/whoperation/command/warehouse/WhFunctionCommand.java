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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 功能主表
 * 
 * @author larkark
 * 
 */
public class WhFunctionCommand extends BaseCommand {


    private static final long serialVersionUID = -7715496251004628693L;

    /** 主键ID */
    private Long id;
    /** 功能编码 */
    private String functionCode;
    /** 功能名称 */
    private String functionName;
    /** 功能模板 收货 上架 入库分拣 */
    private String functionTemplet;
    /** 平台类型 PDA 操作台opstation */
    private String platformType;
    /** 对应仓库ID */
    private Long ouId;
    /** 是否系统创建人 */
    private Boolean isSys;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createId;
    /** 操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;
    /** 是否鎖定1:是 0:否 */
    private Integer isLock;
    /** 功能模板中文名称 */
    private String functionTempletName;

    private Long userId;

    /** SKU属性管理 */
    private String skuAttrMgmt;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** CASELEVEL是否需要扫描SKU */
    private Boolean isCaselevelScanSku;

    /** ==========入库分拣对应参数============ */
    /** 入库分拣规则ID */
    private Long inboundRuleId;
    /** 是否需要扫描目标容器号 */
    private Boolean isScanContainer;
    /** ====================== */

    /** ==========上架对应参数============ */
    /** 上架模式 系统指导上架 人为指定上架 系统建议上架 */
    private Integer putawayPattern;
    /** 是否整托上架 */
    private Boolean isEntireTrayPutaway;
    /** 是否整箱上架 */
    private Boolean isEntireBinPutaway;
    /** 非CASELEVEL是否需要扫描SKU */
    private Boolean isNotcaselevelScanSku;
    /** ====================== */

    /** ==========收货对应参数============ */
    /** 收货模式 货箱收货 托盘收货 */
    private Integer rcvdPattern;
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
    /** 是否限定唯一批次号 */
    private Boolean isLimitUniqueBatch;
    /** 是否限定唯一生产日期 */
    private Boolean isLimitUniqueDateOfManufacture;
    /** 是否限定唯一失效日期 */
    private Boolean isLimitUniqueExpiryDate;
    /** 是否支持混放SKU */
    private Boolean isMixingSku;
    /** 已知库存属性的ASN是否需要提示用户 */
    private Boolean isInvattrAsnPointoutUser;
    /** 库存属性差异是否允许收货 */
    private Boolean isInvattrDiscrepancyAllowrcvd;

    /** ====================== */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionTemplet() {
        return functionTemplet;
    }

    public void setFunctionTemplet(String functionTemplet) {
        this.functionTemplet = functionTemplet;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
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

    public Boolean getIsSys() {
        return isSys;
    }

    public void setIsSys(Boolean isSys) {
        this.isSys = isSys;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getFunctionTempletName() {
        return functionTempletName;
    }

    public void setFunctionTempletName(String functionTempletName) {
        this.functionTempletName = functionTempletName;
    }

    public Long getInboundRuleId() {
        return inboundRuleId;
    }

    public void setInboundRuleId(Long inboundRuleId) {
        this.inboundRuleId = inboundRuleId;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Boolean getIsScanContainer() {
        return isScanContainer;
    }

    public void setIsScanContainer(Boolean isScanContainer) {
        this.isScanContainer = isScanContainer;
    }

    public String getSkuAttrMgmt() {
        return skuAttrMgmt;
    }

    public void setSkuAttrMgmt(String skuAttrMgmt) {
        this.skuAttrMgmt = skuAttrMgmt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPutawayPattern() {
        return putawayPattern;
    }

    public void setPutawayPattern(Integer putawayPattern) {
        this.putawayPattern = putawayPattern;
    }

    public Boolean getIsEntireTrayPutaway() {
        return isEntireTrayPutaway;
    }

    public void setIsEntireTrayPutaway(Boolean isEntireTrayPutaway) {
        this.isEntireTrayPutaway = isEntireTrayPutaway;
    }

    public Boolean getIsEntireBinPutaway() {
        return isEntireBinPutaway;
    }

    public void setIsEntireBinPutaway(Boolean isEntireBinPutaway) {
        this.isEntireBinPutaway = isEntireBinPutaway;
    }

    public Boolean getIsCaselevelScanSku() {
        return isCaselevelScanSku;
    }

    public void setIsCaselevelScanSku(Boolean isCaselevelScanSku) {
        this.isCaselevelScanSku = isCaselevelScanSku;
    }

    public Boolean getIsNotcaselevelScanSku() {
        return isNotcaselevelScanSku;
    }

    public void setIsNotcaselevelScanSku(Boolean isNotcaselevelScanSku) {
        this.isNotcaselevelScanSku = isNotcaselevelScanSku;
    }

    public Integer getRcvdPattern() {
        return rcvdPattern;
    }

    public void setRcvdPattern(Integer rcvdPattern) {
        this.rcvdPattern = rcvdPattern;
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

    public Boolean getIsLimitUniqueBatch() {
        return isLimitUniqueBatch;
    }

    public void setIsLimitUniqueBatch(Boolean isLimitUniqueBatch) {
        this.isLimitUniqueBatch = isLimitUniqueBatch;
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

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }



}
