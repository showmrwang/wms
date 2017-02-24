package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WhWaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.Container2ndCategoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;

public class OdoCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 3423612599719415302L;

    /** 出库单号 */
    private String odoCode;
    /** 外部对接编码 */
    private String extCode;
    /** 电商平台订单号 */
    private String ecOrderCode;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 出库单类型 */
    private String odoType;
    /** 原始出库单号 */
    private String originalOdoCode;
    /** 优先级 */
    private Integer priorityLevel;
    /** 是否整单出库0：否 1：是 */
    private Boolean isWholeOrderOutbound;
    /** 越库标志 */
    private String crossDockingSymbol;
    /** 订单平台类型 */
    private String orderType;
    /** 下单时间 */
    private Date orderTime;
    /** 出库单状态 */
    private String odoStatus;
    /** 计划数量 */
    private Double qty;
    /** 本次出库数量 */
    private Double currentQty;
    /** 实际出库数量 */
    private Double actualQty;
    /** 取消数量 */
    private Double cancelQty;
    /** SKU总件数 */
    private Integer skuNumberOfPackages;
    /** 订单总金额 */
    private Double amt;
    /** 配货模式 */
    private String distributeMode;
    /** 上位系统单据类型 */
    private String epistaticSystemsOrderType;
    /** 出库箱类型 */
    private Long outboundCartonType;
    /** 含危险品1:是0:否 */
    private Boolean includeHazardousCargo;
    /** 含易碎品1:是0:否 */
    private Boolean includeFragileCargo;
    /** 是否锁定 */
    private Boolean isLocked;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** id */
    private Long id;
    /** 波次号 */
    private String waveCode;
    /** 计划发货时间 */
    private Date planDeliverGoodsTime;
    /** 计数器编码 */
    private String counterCode;
    // ------------------------------------------------------------------------------------
    // 自定义字段
    private Long userId;
    private String createTimeStr;
    private String lastModifyTimeStr;
    private String orderTimeStr;
    private String storeName;
    private String customerName;
    private Long waveId;

    // 分组字段
    private String groupCode;

    //出库箱推荐
    private WhWaveCommand whWaveCommand;
    /** 出库单下的波次明细列表 */
    private List<WhWaveLineCommand> whWaveLineCommandList;
    /** 订单下的出库箱列表 */
    private List<OutInvBoxTypeCommand> outboundBoxList;
    /** 订单下的整托列表 */
    private List<ContainerCommand> wholeTrayList;
    /** 订单下的整托列表 */
    private List<ContainerCommand> wholeCaseList;
    /** 订单下的占用库存列表 */
    private List<WhSkuInventoryCommand> skuInventoryCommandList;
    /** 订单涉及分配区域列表 */
    private List<Long> allocateAreaIdList;
    /** 推荐的拣货小车 */
    private Container2ndCategoryCommand trolley;
    /** 周转箱列表 */
    private List<Container2ndCategoryCommand> turnoverBoxList;
    /** 波次明细<odoLineId, waveLine> */
    private Map<Long, WhWaveLineCommand> odoLineIdwaveLineMap;


    public Date getPlanDeliverGoodsTime() {
        return planDeliverGoodsTime;
    }

    public void setPlanDeliverGoodsTime(Date planDeliverGoodsTime) {
        this.planDeliverGoodsTime = planDeliverGoodsTime;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderTimeStr() {
        return orderTimeStr;
    }

    public void setOrderTimeStr(String orderTimeStr) {
        this.orderTimeStr = orderTimeStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getLastModifyTimeStr() {
        return lastModifyTimeStr;
    }

    public void setLastModifyTimeStr(String lastModifyTimeStr) {
        this.lastModifyTimeStr = lastModifyTimeStr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getEcOrderCode() {
        return ecOrderCode;
    }

    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
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

    public String getOdoType() {
        return odoType;
    }

    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }

    public String getOriginalOdoCode() {
        return originalOdoCode;
    }

    public void setOriginalOdoCode(String originalOdoCode) {
        this.originalOdoCode = originalOdoCode;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Boolean getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(Boolean isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }

    public String getCrossDockingSymbol() {
        return crossDockingSymbol;
    }

    public void setCrossDockingSymbol(String crossDockingSymbol) {
        this.crossDockingSymbol = crossDockingSymbol;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }


    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(Double currentQty) {
        this.currentQty = currentQty;
    }

    public Double getActualQty() {
        return actualQty;
    }

    public void setActualQty(Double actualQty) {
        this.actualQty = actualQty;
    }

    public Double getCancelQty() {
        return cancelQty;
    }

    public void setCancelQty(Double cancelQty) {
        this.cancelQty = cancelQty;
    }

    public Integer getSkuNumberOfPackages() {
        return skuNumberOfPackages;
    }

    public void setSkuNumberOfPackages(Integer skuNumberOfPackages) {
        this.skuNumberOfPackages = skuNumberOfPackages;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getDistributeMode() {
        return distributeMode;
    }

    public void setDistributeMode(String distributeMode) {
        this.distributeMode = distributeMode;
    }

    public String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public Long getOutboundCartonType() {
        return outboundCartonType;
    }

    public void setOutboundCartonType(Long outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }

    public Boolean getIncludeHazardousCargo() {
        return includeHazardousCargo;
    }

    public void setIncludeHazardousCargo(Boolean includeHazardousCargo) {
        this.includeHazardousCargo = includeHazardousCargo;
    }

    public Boolean getIncludeFragileCargo() {
        return includeFragileCargo;
    }

    public void setIncludeFragileCargo(Boolean includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }

    public String getCounterCode() {
        return counterCode;
    }

    public void setCounterCode(String counterCode) {
        this.counterCode = counterCode;
    }

    public WhWaveCommand getWhWaveCommand() {
        return whWaveCommand;
    }

    public void setWhWaveCommand(WhWaveCommand whWaveCommand) {
        this.whWaveCommand = whWaveCommand;
    }

    public List<WhWaveLineCommand> getWhWaveLineCommandList() {
        return whWaveLineCommandList;
    }

    public void setWhWaveLineCommandList(List<WhWaveLineCommand> whWaveLineCommandList) {
        this.whWaveLineCommandList = whWaveLineCommandList;
    }

    public List<OutInvBoxTypeCommand> getOutboundBoxList() {
        return outboundBoxList;
    }

    public void setOutboundBoxList(List<OutInvBoxTypeCommand> outboundBoxList) {
        this.outboundBoxList = outboundBoxList;
    }

    public List<ContainerCommand> getWholeTrayList() {
        return wholeTrayList;
    }

    public void setWholeTrayList(List<ContainerCommand> wholeTrayList) {
        this.wholeTrayList = wholeTrayList;
    }

    public List<ContainerCommand> getWholeCaseList() {
        return wholeCaseList;
    }

    public void setWholeCaseList(List<ContainerCommand> wholeCaseList) {
        this.wholeCaseList = wholeCaseList;
    }

    public List<WhSkuInventoryCommand> getSkuInventoryCommandList() {
        return skuInventoryCommandList;
    }

    public void setSkuInventoryCommandList(List<WhSkuInventoryCommand> skuInventoryCommandList) {
        this.skuInventoryCommandList = skuInventoryCommandList;
    }

    public List<Long> getAllocateAreaIdList() {
        return allocateAreaIdList;
    }

    public void setAllocateAreaIdList(List<Long> allocateAreaIdList) {
        this.allocateAreaIdList = allocateAreaIdList;
    }

    public Container2ndCategoryCommand getTrolley() {
        return trolley;
    }

    public void setTrolley(Container2ndCategoryCommand trolley) {
        this.trolley = trolley;
    }

    public List<Container2ndCategoryCommand> getTurnoverBoxList() {
        return turnoverBoxList;
    }

    public void setTurnoverBoxList(List<Container2ndCategoryCommand> turnoverBoxList) {
        this.turnoverBoxList = turnoverBoxList;
    }

    public Map<Long, WhWaveLineCommand> getOdoLineIdwaveLineMap() {
        return odoLineIdwaveLineMap;
    }

    public void setOdoLineIdwaveLineMap(Map<Long, WhWaveLineCommand> odoLineIdwaveLineMap) {
        this.odoLineIdwaveLineMap = odoLineIdwaveLineMap;
    }
}
