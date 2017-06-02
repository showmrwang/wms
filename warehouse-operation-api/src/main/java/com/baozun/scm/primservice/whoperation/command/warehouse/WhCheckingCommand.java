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


public class WhCheckingCommand extends BaseCommand {


    private static final long serialVersionUID = -2504204141651276196L;
    /** 主键ID */
    private Long id;
    /** 播种墙ID */
    private Long facilityId;
    /** 周转箱ID */
    private Long containerId;
    /** 小批次 */
    private String batch;
    /** 波次号 */
    private String waveCode;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 运输服务商CODE */
    private String transportCode;
    /** 运输服务商名称 */
    private String transportName;
    /** 产品类型CODE */
    private String productCode;
    /** 产品类型名称 */
    private String productName;
    /** 时效类型CODE */
    private String timeEffectCode;
    /** 时效类型名称 */
    private String timeEffectName;
    /** 复核状态 */
    private Integer status;
    /** 对应组织ID */
    private Long ouId;
    /** 外部容器，小车 */
    private Long outerContainerId;
    /** 货格编码数 */
    private Integer containerLatticeNo;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 配货模式 */
    private String distributionMode;
    /** 拣货模式 */
    private String pickingMode;
    /** 复核模式 */
    private String checkingMode;
    /** 创建人 */
    private java.lang.Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private java.lang.Long modifiedId;
    // /** 复核头集合 */
    // private List<WhCheckingLineCommand> checkingLineCommandLst;


    // ========== 按箱复核 ==========
    /** 外部容器，小车编码*/
    private String outerContainerCode;
    /** 播种墙编码*/
    private String seedingWallCode;
    /** 输入 */
    private String input;
    /** 提示 */
    private String tip;
    /** 出库单id*/
    private Long odoId;
    /** 按单复核方式*/
    private Integer oDCheckWay;
    // /** 页面缓存复核明细*/
    // private List<WhCheckingLine> checkingLineList;
    /** 出库单号*/
    private String odoCode;
    /** 外部对接编码*/
    private String extCode;
    /** 小批次下总箱数*/
    private Integer batchBoxCount;
    /** 待复核总箱数*/
    private Integer toBeCheckedBoxCount;
    /** 小批次下总单数*/
    private Integer batchOdoCount;
    /** 待复核总单数*/
    private Integer toBeCheckedOdoCount;
    /** 主品条码*/
    private String mainSkuCode;
    /** 主品名称*/
    private String mainSkuName;
    /** 耗材编码*/
    private String consumableSkuBarcode;
    /** 耗材id*/
    private Long consumableSkuId;
    /** 周转箱编码*/
    private String containerCode;
    /** 面单号*/
    // private String waybillCode;
    /** 面单类型*/
    private String waybillType;



    /****************自定义*********************/
    /** 耗材编码 */
    private String consumableCode;
    /** 复核头ID */
    private Long checkingId;
    /** 播种墙/复核台编码 */
    private String facilityCode;
    /** 小车编码 */
    private String outContainerCode;
    /** 复核类型 复核流程 */
    private String checkingType;
    /** 复核数据源编码 */
    private String checkingSourceCode;
    /** 复核箱编码 */
    private String checkingBoxCode;
    /** 复核数据源类型 播种集货/复核集货 */
    private String checkingSourceType;
    /** 复核台id*/
    private Long checkingFacilityId;

    /** 复核的出库箱 */
    private WhOutboundboxCommand outboundbox;
    /** 小批次下总箱数*/
    private Long batchBoxCnt;
    /** 待复核总箱数*/
    private Long batchBoxCntCheck;
    /** 小批次下总单数*/
    private Long batchOdoCnt;
    /** 待复核总单数*/
    private Long batchOdoCntCheck;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

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

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTimeEffectCode() {
        return timeEffectCode;
    }

    public void setTimeEffectCode(String timeEffectCode) {
        this.timeEffectCode = timeEffectCode;
    }

    public String getTimeEffectName() {
        return timeEffectName;
    }

    public void setTimeEffectName(String timeEffectName) {
        this.timeEffectName = timeEffectName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }

    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(String distributionMode) {
        this.distributionMode = distributionMode;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public String getCheckingMode() {
        return checkingMode;
    }

    public void setCheckingMode(String checkingMode) {
        this.checkingMode = checkingMode;
    }

    // public List<WhCheckingLineCommand> getCheckingLineCommandLst() {
    // return checkingLineCommandLst;
    // }
    //
    // public void setCheckingLineCommandLst(List<WhCheckingLineCommand> checkingLineCommandLst) {
    // this.checkingLineCommandLst = checkingLineCommandLst;
    // }


    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public String getSeedingWallCode() {
        return seedingWallCode;
    }

    public void setSeedingWallCode(String seedingWallCode) {
        this.seedingWallCode = seedingWallCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    // public List<WhCheckingLine> getCheckingLineList() {
    // return checkingLineList;
    // }
    //
    // public void setCheckingLineList(List<WhCheckingLine> checkingLineList) {
    // this.checkingLineList = checkingLineList;
    // }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Integer getoDCheckWay() {
        return oDCheckWay;
    }

    public void setoDCheckWay(Integer oDCheckWay) {
        this.oDCheckWay = oDCheckWay;
    }

    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public Integer getBatchBoxCount() {
        return batchBoxCount;
    }

    public void setBatchBoxCount(Integer batchBoxCount) {
        this.batchBoxCount = batchBoxCount;
    }

    public Integer getToBeCheckedBoxCount() {
        return toBeCheckedBoxCount;
    }

    public void setToBeCheckedBoxCount(Integer toBeCheckedBoxCount) {
        this.toBeCheckedBoxCount = toBeCheckedBoxCount;
    }

    public Integer getBatchOdoCount() {
        return batchOdoCount;
    }

    public void setBatchOdoCount(Integer batchOdoCount) {
        this.batchOdoCount = batchOdoCount;
    }

    public Integer getToBeCheckedOdoCount() {
        return toBeCheckedOdoCount;
    }

    public void setToBeCheckedOdoCount(Integer toBeCheckedOdoCount) {
        this.toBeCheckedOdoCount = toBeCheckedOdoCount;
    }

    public String getMainSkuCode() {
        return mainSkuCode;
    }

    public void setMainSkuCode(String mainSkuCode) {
        this.mainSkuCode = mainSkuCode;
    }

    public String getMainSkuName() {
        return mainSkuName;
    }

    public void setMainSkuName(String mainSkuName) {
        this.mainSkuName = mainSkuName;
    }

    public String getConsumableSkuBarcode() {
        return consumableSkuBarcode;
    }

    public void setConsumableSkuBarcode(String consumableSkuBarcode) {
        this.consumableSkuBarcode = consumableSkuBarcode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getConsumableCode() {
        return consumableCode;
    }

    public void setConsumableCode(String consumableCode) {
        this.consumableCode = consumableCode;
    }

    public Long getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(Long checkingId) {
        this.checkingId = checkingId;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getOutContainerCode() {
        return outContainerCode;
    }

    public void setOutContainerCode(String outContainerCode) {
        this.outContainerCode = outContainerCode;
    }

    public String getCheckingType() {
        return checkingType;
    }

    public void setCheckingType(String checkingType) {
        this.checkingType = checkingType;
    }

    public String getCheckingSourceCode() {
        return checkingSourceCode;
    }

    public void setCheckingSourceCode(String checkingSourceCode) {
        this.checkingSourceCode = checkingSourceCode;
    }

    public String getCheckingBoxCode() {
        return checkingBoxCode;
    }

    public void setCheckingBoxCode(String checkingBoxCode) {
        this.checkingBoxCode = checkingBoxCode;
    }

    public String getCheckingSourceType() {
        return checkingSourceType;
    }

    public void setCheckingSourceType(String checkingSourceType) {
        this.checkingSourceType = checkingSourceType;
    }

    public WhOutboundboxCommand getOutboundbox() {
        return outboundbox;
    }

    public void setOutboundbox(WhOutboundboxCommand outboundbox) {
        this.outboundbox = outboundbox;
    }

    public java.lang.Long getCreateId() {
        return createId;
    }

    public void setCreateId(java.lang.Long createId) {
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

    public java.lang.Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public Long getConsumableSkuId() {
        return consumableSkuId;
    }

    public void setConsumableSkuId(Long consumableSkuId) {
        this.consumableSkuId = consumableSkuId;
    }

    public Long getCheckingFacilityId() {
        return checkingFacilityId;
    }

    public void setCheckingFacilityId(Long checkingFacilityId) {
        this.checkingFacilityId = checkingFacilityId;
    }

    // public String getWaybillCode() {
    // return waybillCode;
    // }
    //
    // public void setWaybillCode(String waybillCode) {
    // this.waybillCode = waybillCode;
    // }

    public String getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(String waybillType) {
        this.waybillType = waybillType;
    }

    public Long getBatchBoxCnt() {
        return batchBoxCnt;
    }

    public void setBatchBoxCnt(Long batchBoxCnt) {
        this.batchBoxCnt = batchBoxCnt;
    }

    public Long getBatchBoxCntCheck() {
        return batchBoxCntCheck;
    }

    public void setBatchBoxCntCheck(Long batchBoxCntCheck) {
        this.batchBoxCntCheck = batchBoxCntCheck;
    }

    public Long getBatchOdoCnt() {
        return batchOdoCnt;
    }

    public void setBatchOdoCnt(Long batchOdoCnt) {
        this.batchOdoCnt = batchOdoCnt;
    }

    public Long getBatchOdoCntCheck() {
        return batchOdoCntCheck;
    }

    public void setBatchOdoCntCheck(Long batchOdoCntCheck) {
        this.batchOdoCntCheck = batchOdoCntCheck;
    }

}
