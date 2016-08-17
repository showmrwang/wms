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
package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;

/**
 * 库存明细表
 * 
 * @author larkark
 * 
 */
public class WhSkuInventorySnCommand extends BaseCommand {


    /**
     * 
     */
    private static final long serialVersionUID = -6914815409688486216L;

    /** 主键ID */
    private Long id;
    /** sn号 */
    private String sn;
    /** 占用单据号 */
    private String occupationCode;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次类型来源 STORE店铺 WH仓库 */
    private String defectSource;
    /** 残次原因类型ID */
    private Long defectTypeId;
    /** 残次原因ID */
    private Long defectReasonsId;
    /** 状态 1:在库2:已分配3:冻结 */
    private Integer status;
    /** 库存对应属性拼接 开发内部使用 */
    private String invAttr;
    /** 内部对接码 */
    private String uuid;
    /** 对应仓库ID */
    private Long ouId;
    /** */
    private String sysUuid;

    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 库位号 */
    private String locationCode;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 内部容器号 */
    private String insideContainerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺名称 */
    private String storeName;
    /** 残次类型名 */
    private String defectTypeName;
    /** 残次类型原因名 */
    private String defectReasonsName;
    /** 状态名 */
    private String statusStr;
    /** 残次类型(店铺) */
    private String storeDefectTypeName;
    /** 残次原因(店铺) */
    private String storeDefectReasonsName;
    /** 残次类型(仓库) */
    private String whDefectTypeName;
    /** 残次原因(仓库) */
    private String whDefectReasonsName;
    /** 序列号管理类型 */
    private String serialNumberType;
    /** 上架规则 */
    private List<ShelveRecommendRuleCommand> shelveRecommendRuleCommandList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public String getDefectWareBarcode() {
        return defectWareBarcode;
    }

    public void setDefectWareBarcode(String defectWareBarcode) {
        this.defectWareBarcode = defectWareBarcode;
    }

    public String getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public Long getDefectTypeId() {
        return defectTypeId;
    }

    public void setDefectTypeId(Long defectTypeId) {
        this.defectTypeId = defectTypeId;
    }

    public Long getDefectReasonsId() {
        return defectReasonsId;
    }

    public void setDefectReasonsId(Long defectReasonsId) {
        this.defectReasonsId = defectReasonsId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInvAttr() {
        return invAttr;
    }

    public void setInvAttr(String invAttr) {
        this.invAttr = invAttr;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getSysUuid() {
        return sysUuid;
    }

    public void setSysUuid(String sysUuid) {
        this.sysUuid = sysUuid;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDefectTypeName() {
        return defectTypeName;
    }

    public void setDefectTypeName(String defectTypeName) {
        this.defectTypeName = defectTypeName;
    }

    public String getDefectReasonsName() {
        return defectReasonsName;
    }

    public void setDefectReasonsName(String defectReasonsName) {
        this.defectReasonsName = defectReasonsName;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getStoreDefectTypeName() {
        return storeDefectTypeName;
    }

    public void setStoreDefectTypeName(String storeDefectTypeName) {
        this.storeDefectTypeName = storeDefectTypeName;
    }

    public String getStoreDefectReasonsName() {
        return storeDefectReasonsName;
    }

    public void setStoreDefectReasonsName(String storeDefectReasonsName) {
        this.storeDefectReasonsName = storeDefectReasonsName;
    }

    public String getWhDefectTypeName() {
        return whDefectTypeName;
    }

    public void setWhDefectTypeName(String whDefectTypeName) {
        this.whDefectTypeName = whDefectTypeName;
    }

    public String getWhDefectReasonsName() {
        return whDefectReasonsName;
    }

    public void setWhDefectReasonsName(String whDefectReasonsName) {
        this.whDefectReasonsName = whDefectReasonsName;
    }

    public List<ShelveRecommendRuleCommand> getShelveRecommendRuleCommandList() {
        return shelveRecommendRuleCommandList;
    }

    public void setShelveRecommendRuleCommandList(List<ShelveRecommendRuleCommand> shelveRecommendRuleCommandList) {
        this.shelveRecommendRuleCommandList = shelveRecommendRuleCommandList;
    }
}
