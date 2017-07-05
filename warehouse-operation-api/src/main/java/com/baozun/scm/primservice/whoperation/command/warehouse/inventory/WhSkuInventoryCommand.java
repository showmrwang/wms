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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

/**
* 商品库存表
*
* @author larkark
*
*/
public class WhSkuInventoryCommand extends BaseCommand {

    private static final long serialVersionUID = -635262476522169659L;

    /** 主键ID */
    private Long id;
    /** 商品ID */
    private Long skuId;
    /** 库位ID 库位号 */
    private Long locationId;
    /** 分配区域id*/
    private Long allocateAreaId;
    /** 外部容器ID 托盘 货箱 */
    private Long outerContainerId;
    /** 内部容器ID 托盘 货箱 */
    private Long insideContainerId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 占用单据号 */
    private String occupationCode;
    /** 占用单据明细行ID */
    private Long occupationLineId;
    /** 调整数量 */
    private Double modifyQty;
    /** 播种墙编码 */
    private String seedingWallCode;
    /**货格号*/
    private Integer containerLatticeNo;
    /**出库箱号*/
    private String outboundboxCode;
    /** 在库可用库存 */
    private Double onHandQty;
    /** 已分配库存 */
    private Double allocatedQty;
    /** 待移入库存 */
    private Double toBeFilledQty;
    /** 冻结库存 */
    private Double frozenQty;
    /** 库存状态 */
    private Long invStatus;
    /** 库存类型 */
    private String invType;
    /** 批次号 */
    private String batchNumber;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存属性1 */
    private String invAttr1;
    /** 库存属性2 */
    private String invAttr2;
    /** 库存属性3 */
    private String invAttr3;
    /** 库存属性4 */
    private String invAttr4;
    /** 库存属性5 */
    private String invAttr5;
    /** 内部对接码 */
    private String uuid;
    /** 是否可用 */
    private Boolean isLocked;
    /** 对应仓库ID */
    private Long ouId;
    /** 占用单据号来源 */
    private String occupationCodeSource;
    /** 生产日期 String */
    private String mfgDateStr;
    /** 失效日期 String */
    private String expDateStr;

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
    /** 库存类型 */
    private String invTypeName;
    /** 在库数量 */
    private Double qty;
    /** 库存状态 */
    private String invstatusName;
    /** 库存属性1 lable */
    private String invAttr1Str;
    /** 库存属性2 lable */
    private String invAttr2Str;
    /** 库存属性3 lable */
    private String invAttr3Str;
    /** 库存属性4 lable */
    private String invAttr4Str;
    /** 库存属性5 lable */
    private String invAttr5Str;
    /** 占用Key */
    private String occupyKey;
    private Double expectQty;
    /** 效期 */
    private Integer validDate;
    /** 混放属性 */
    private String mixAttr;

    // pda-general-receiving
    private Long occupationId;
    /** 入库时间 */
    private Date inboundTime;
    /** 最后操作时间 */
    private Date lastModifyTime;

    /** 暂存库位ID*/
    private Long temporaryLocationId;

    // 用于通用收货的流程-------------------------------------------------------------------------------------
    /** 通用收货所选择的功能 */
    private WhFunctionRcvd rcvd;
    /** 通用收货功能菜单Index */
    private Integer menuIndex;
    /** 通用收货URL流程参数字符串 */
    private String functionUrl;
    /** 功能菜单Id */
    private Long functionId;
    /** 通用收货功能商品扫描流程参数字符串 */
    private String skuUrl;
    /** 通用收货功能商品扫描流程指针 */
    private Integer skuUrlOperator;
    /** 通用收货功能匹配明细行 */
    private String lineIdListString;
    /** 通用收货功能匹配明细行 */
    private RcvdSnCacheCommand sn;
    /** 通用收货累计收货数量 */
    private Integer skuRcvdCount;
    /** 通用收货本次累计数量 */
    private Integer skuAddUpCount;
    /** 通用收货userId */
    private Long userId;
    /** 通用收货批量收货数量 */
    private Integer skuBatchCount;
    /** 容器收货每个容器计划收货数量 */
    private Integer containerPlan;
    /** 通用收货 商品多条码数量 */
    private Integer quantity;
    /** 残次数量 */
    private Integer snCount;
    /** 残次累计数量 */
    private Integer snAddUpCount;
    /** 通用收货：容器-商品缓存对象 @mender yimin.lu 2016/11/23 */
    private RcvdContainerCacheCommand rcvdUserContainerCache;
    /** 通用收货：容器-用户缓存对象 @mender yimin.lu 2016/11/23 */
    private RcvdContainerCacheCommand rcvdSkuContainerCache;
    /** 库存状态map */
    private Map<Long, String> invStatusMap;
    /** 效期天数 */
    @Deprecated
    private Integer dayOfValidDate;
    /** 残次来源 */
    private String snSource;
    /** 残次类型 */
    private String defectType;
    /** 残次原因 */
    private String defectReasons;
    /** 商品序列号 */
    private String skuSn;
    /** 残次类型 */
    private Long defectTypeId;
    /** 残次原因 */
    private Long defectReasonsId;
    // --------------------------------------------------------------------------------------------
    
    
    
    //-------------------------------------------------
    /** 补货单号 */
    private java.lang.String replenishmentCode;
    
    /** 补货阶段使用的规则id */
    private java.lang.Long replenishmentRuleId;
    //--------------------------------------------------

    /** 是否允许货箱收货完成 */
    private Boolean isContainerRcvdFinished;

    /** 是否允许托盘收货完成 */
    private Boolean isPalletRcvdFinished;

    /** 是否允许ASN收货完成 */
    private Boolean isAsnRcvdFinished;

    /** 残次库存信息 */
    private List<WhSkuInventorySnCommand> whSkuInventorySnCommandList;

    /** 匹配的规则 */
    private List<ShelveRecommendRuleCommand> shelveRecommendRuleCommandList;
    /** 是否允许差异收货 */
    private Boolean isInvattrDiscrepancyAllowrcvd;



    public Map<Long, String> getInvStatusMap() {
        return invStatusMap;
    }

    public void setInvStatusMap(Map<Long, String> invStatusMap) {
        this.invStatusMap = invStatusMap;
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

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public String getMixAttr() {
        return mixAttr;
    }

    public void setMixAttr(String mixAttr) {
        this.mixAttr = mixAttr;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public String getDefectReasons() {
        return defectReasons;
    }

    public void setDefectReasons(String defectReasons) {
        this.defectReasons = defectReasons;
    }

    /**容器类型*/
    private Long containerType;

    /** 当前容器序号 */
    private String curOrder;

    /**asn 商品数量*/
    private Long asnSkuCount;
    /** 序列号类型*/
    private String serialNumberType;
    /** 序列号*/
    private String serialNumber;
    /** 当前url标记 */
    private Integer currUrl;

    // private Boolean isBatchScan;
    /** 扫描模式 */
    private Integer scanPattern;
    /** 是否向用户提示属性*/
    private String isInvattrAsnPointoutUser;
    /** 当前商品sn待扫数量*/
    private Integer qtyRest;
    /** 容器待扫数量*/
    private Integer qtyRestContainer;

    private Double sumOnHandQty;

    /** 分配区域id */
    private Long areaId;
    /** 分配单位 托盘 货箱 */
    private String allocateUnitCodes;
    /** 占用顺序  true:先入先出 false:先入后出 */
    private Boolean priority;
    /** 是否静态库位占用 */
    private Boolean isStatic;
    /** 是否混合库位占用 */
    private Boolean isMixStacking;
    /** 最小失效日期 */
    private Date minExpDate;
    /** 最大失效日期 */
    private Date maxExpDate;

    /** 商品条码*/
    private String skuBarcode;
    /** 容器序号*/
    private String containerQty;

    /** 库位拣货顺序 */
    private String pickSort;

    private String customerCode;

    private String storeCode;


    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public Boolean getIsPalletRcvdFinished() {
        return isPalletRcvdFinished;
    }

    public void setIsPalletRcvdFinished(Boolean isPalletRcvdFinished) {
        this.isPalletRcvdFinished = isPalletRcvdFinished;
    }

    public Boolean getIsAsnRcvdFinished() {
        return isAsnRcvdFinished;
    }

    public void setIsAsnRcvdFinished(Boolean isAsnRcvdFinished) {
        this.isAsnRcvdFinished = isAsnRcvdFinished;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Long getOccupationLineId() {
        return occupationLineId;
    }

    public void setOccupationLineId(Long occupationLineId) {
        this.occupationLineId = occupationLineId;
    }

    public Double getModifyQty() {
        return modifyQty;
    }

    public void setModifyQty(Double modifyQty) {
        this.modifyQty = modifyQty;
    }

    public Double getOnHandQty() {
        return onHandQty;
    }

    public void setOnHandQty(Double onHandQty) {
        this.onHandQty = onHandQty;
    }

    public Double getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Double allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public Double getToBeFilledQty() {
        return toBeFilledQty;
    }

    public void setToBeFilledQty(Double toBeFilledQty) {
        this.toBeFilledQty = toBeFilledQty;
    }

    public Double getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(Double frozenQty) {
        this.frozenQty = frozenQty;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getInvAttr1() {
        return invAttr1;
    }

    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }

    public String getInvAttr2() {
        return invAttr2;
    }

    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }

    public String getInvAttr3() {
        return invAttr3;
    }

    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }

    public String getInvAttr4() {
        return invAttr4;
    }

    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }

    public String getInvAttr5() {
        return invAttr5;
    }

    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getOccupationCodeSource() {
        return occupationCodeSource;
    }

    public void setOccupationCodeSource(String occupationCodeSource) {
        this.occupationCodeSource = occupationCodeSource;
    }

    public String getMfgDateStr() {
        return mfgDateStr;
    }

    public void setMfgDateStr(String mfgDateStr) {
        this.mfgDateStr = mfgDateStr;
    }

    public String getExpDateStr() {
        return expDateStr;
    }

    public void setExpDateStr(String expDateStr) {
        this.expDateStr = expDateStr;
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

    public String getInvTypeName() {
        return invTypeName;
    }

    public void setInvTypeName(String invTypeName) {
        this.invTypeName = invTypeName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getInvstatusName() {
        return invstatusName;
    }

    public void setInvstatusName(String invstatusName) {
        this.invstatusName = invstatusName;
    }

    public String getInvAttr1Str() {
        return invAttr1Str;
    }

    public void setInvAttr1Str(String invAttr1Str) {
        this.invAttr1Str = invAttr1Str;
    }

    public String getInvAttr2Str() {
        return invAttr2Str;
    }

    public void setInvAttr2Str(String invAttr2Str) {
        this.invAttr2Str = invAttr2Str;
    }

    public String getInvAttr3Str() {
        return invAttr3Str;
    }

    public void setInvAttr3Str(String invAttr3Str) {
        this.invAttr3Str = invAttr3Str;
    }

    public String getInvAttr4Str() {
        return invAttr4Str;
    }

    public void setInvAttr4Str(String invAttr4Str) {
        this.invAttr4Str = invAttr4Str;
    }

    public String getInvAttr5Str() {
        return invAttr5Str;
    }

    public void setInvAttr5Str(String invAttr5Str) {
        this.invAttr5Str = invAttr5Str;
    }

    public String getOccupyKey() {
        return occupyKey;
    }

    public void setOccupyKey(String occupyKey) {
        this.occupyKey = occupyKey;
    }

    public Double getExpectQty() {
        return expectQty;
    }

    public void setExpectQty(Double expectQty) {
        this.expectQty = expectQty;
    }

    public Long getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Long occupationId) {
        this.occupationId = occupationId;
    }

    public Date getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(Date inboundTime) {
        this.inboundTime = inboundTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public WhFunctionRcvd getRcvd() {
        return rcvd;
    }

    public void setRcvd(WhFunctionRcvd rcvd) {
        this.rcvd = rcvd;
    }

    public Integer getMenuIndex() {
        return menuIndex;
    }

    public void setMenuIndex(Integer menuIndex) {
        this.menuIndex = menuIndex;
    }

    public String getFunctionUrl() {
        return functionUrl;
    }

    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getSkuUrl() {
        return skuUrl;
    }

    public void setSkuUrl(String skuUrl) {
        this.skuUrl = skuUrl;
    }

    public Integer getSkuUrlOperator() {
        return skuUrlOperator;
    }

    public void setSkuUrlOperator(Integer skuUrlOperator) {
        this.skuUrlOperator = skuUrlOperator;
    }

    public String getLineIdListString() {
        return lineIdListString;
    }

    public void setLineIdListString(String lineIdListString) {
        this.lineIdListString = lineIdListString;
    }

    public RcvdSnCacheCommand getSn() {
        return sn;
    }

    public void setSn(RcvdSnCacheCommand sn) {
        this.sn = sn;
    }

    public Integer getSkuAddUpCount() {
        return skuAddUpCount;
    }

    public void setSkuAddUpCount(Integer skuAddUpCount) {
        this.skuAddUpCount = skuAddUpCount;
    }

    public Integer getContainerPlan() {
        return containerPlan;
    }

    public void setContainerPlan(Integer containerPlan) {
        this.containerPlan = containerPlan;
    }

    public Long getContainerType() {
        return containerType;
    }

    public void setContainerType(Long containerType) {
        this.containerType = containerType;
    }

    public String getCurOrder() {
        return curOrder;
    }

    public void setCurOrder(String curOrder) {
        this.curOrder = curOrder;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSkuBatchCount() {
        return skuBatchCount;
    }

    public void setSkuBatchCount(Integer skuBatchCount) {
        this.skuBatchCount = skuBatchCount;
    }

    public Integer getSnCount() {
        return snCount;
    }

    public void setSnCount(Integer snCount) {
        this.snCount = snCount;
    }

    public Integer getSnAddUpCount() {
        return snAddUpCount;
    }

    public void setSnAddUpCount(Integer snAddUpCount) {
        this.snAddUpCount = snAddUpCount;
    }

    public Integer getDayOfValidDate() {
        return dayOfValidDate;
    }

    public void setDayOfValidDate(Integer dayOfValidDate) {
        this.dayOfValidDate = dayOfValidDate;
    }

    public String getSnSource() {
        return snSource;
    }

    public void setSnSource(String snSource) {
        this.snSource = snSource;
    }

    public Boolean getIsContainerRcvdFinished() {
        return isContainerRcvdFinished;
    }

    public void setIsContainerRcvdFinished(Boolean isContainerRcvdFinished) {
        this.isContainerRcvdFinished = isContainerRcvdFinished;
    }

    public List<ShelveRecommendRuleCommand> getShelveRecommendRuleCommandList() {
        return shelveRecommendRuleCommandList;
    }

    public void setShelveRecommendRuleCommandList(List<ShelveRecommendRuleCommand> shelveRecommendRuleCommandList) {
        this.shelveRecommendRuleCommandList = shelveRecommendRuleCommandList;
    }

    public List<WhSkuInventorySnCommand> getWhSkuInventorySnCommandList() {
        return whSkuInventorySnCommandList;
    }

    public void setWhSkuInventorySnCommandList(List<WhSkuInventorySnCommand> whSkuInventorySnCommandList) {
        this.whSkuInventorySnCommandList = whSkuInventorySnCommandList;
    }

    public Long getAsnSkuCount() {
        return asnSkuCount;
    }

    public void setAsnSkuCount(Long asnSkuCount) {
        this.asnSkuCount = asnSkuCount;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIsInvattrAsnPointoutUser() {
        return isInvattrAsnPointoutUser;
    }

    public void setIsInvattrAsnPointoutUser(String isInvattrAsnPointoutUser) {
        this.isInvattrAsnPointoutUser = isInvattrAsnPointoutUser;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
    }

    public Integer getCurrUrl() {
        return currUrl;
    }

    public void setCurrUrl(Integer currUrl) {
        this.currUrl = currUrl;
    }

    public Integer getQtyRest() {
        return qtyRest;
    }

    public void setQtyRest(Integer qtyRest) {
        this.qtyRest = qtyRest;
    }

    public Integer getQtyRestContainer() {
        return qtyRestContainer;
    }

    public void setQtyRestContainer(Integer qtyRestContainer) {
        this.qtyRestContainer = qtyRestContainer;
    }

    public Double getSumOnHandQty() {
        return sumOnHandQty;
    }

    public void setSumOnHandQty(Double sumOnHandQty) {
        this.sumOnHandQty = sumOnHandQty;
    }

    public Boolean getIsInvattrDiscrepancyAllowrcvd() {
        return isInvattrDiscrepancyAllowrcvd;
    }

    public void setIsInvattrDiscrepancyAllowrcvd(Boolean isInvattrDiscrepancyAllowrcvd) {
        this.isInvattrDiscrepancyAllowrcvd = isInvattrDiscrepancyAllowrcvd;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAllocateUnitCodes() {
        return allocateUnitCodes;
    }

    public void setAllocateUnitCodes(String allocateUnitCodes) {
        this.allocateUnitCodes = allocateUnitCodes;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Boolean getIsMixStacking() {
        return isMixStacking;
    }

    public void setIsMixStacking(Boolean isMixStacking) {
        this.isMixStacking = isMixStacking;
    }

    public Date getMinExpDate() {
        return minExpDate;
    }

    public void setMinExpDate(Date minExpDate) {
        this.minExpDate = minExpDate;
    }

    public Date getMaxExpDate() {
        return maxExpDate;
    }

    public void setMaxExpDate(Date maxExpDate) {
        this.maxExpDate = maxExpDate;
    }

    public String getSkuBarcode() {
        return skuBarcode;
    }

    public void setSkuBarcode(String skuBarcode) {
        this.skuBarcode = skuBarcode;
    }

    public String getContainerQty() {
        return containerQty;
    }

    public void setContainerQty(String containerQty) {
        this.containerQty = containerQty;
    }

    public RcvdContainerCacheCommand getRcvdUserContainerCache() {
        return rcvdUserContainerCache;
    }

    public void setRcvdUserContainerCache(RcvdContainerCacheCommand rcvdUserContainerCache) {
        this.rcvdUserContainerCache = rcvdUserContainerCache;
    }

    public RcvdContainerCacheCommand getRcvdSkuContainerCache() {
        return rcvdSkuContainerCache;
    }

    public void setRcvdSkuContainerCache(RcvdContainerCacheCommand rcvdSkuContainerCache) {
        this.rcvdSkuContainerCache = rcvdSkuContainerCache;
    }

    public Integer getSkuRcvdCount() {
        return skuRcvdCount;
    }

    public void setSkuRcvdCount(Integer skuRcvdCount) {
        this.skuRcvdCount = skuRcvdCount;
    }

    public Long getTemporaryLocationId() {
        return temporaryLocationId;
    }

    public void setTemporaryLocationId(Long temporaryLocationId) {
        this.temporaryLocationId = temporaryLocationId;
    }

    public String getSeedingWallCode() {
        return seedingWallCode;
    }

    public void setSeedingWallCode(String seedingWallCode) {
        this.seedingWallCode = seedingWallCode;
    }

    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }

    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public java.lang.String getReplenishmentCode() {
        return replenishmentCode;
    }

    public void setReplenishmentCode(java.lang.String replenishmentCode) {
        this.replenishmentCode = replenishmentCode;
    }

    public java.lang.Long getReplenishmentRuleId() {
        return replenishmentRuleId;
    }

    public void setReplenishmentRuleId(java.lang.Long replenishmentRuleId) {
        this.replenishmentRuleId = replenishmentRuleId;
    }


    public String getPickSort() {
        return pickSort;
    }

    public void setPickSort(String pickSort) {
        this.pickSort = pickSort;
    }

    public Long getAllocateAreaId() {
        return allocateAreaId;
    }

    public void setAllocateAreaId(Long allocateAreaId) {
        this.allocateAreaId = allocateAreaId;
    }
}
