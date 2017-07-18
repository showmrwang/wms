package com.baozun.scm.primservice.whoperation.command.wave;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhWaveLineCommand extends BaseCommand {

    private static final long serialVersionUID = -6122809461237056322L;

    /** 波次ID */
    private Long waveId;
    /** 出库单明细行ID */
    private Long odoLineId;
    /** 出库单ID */
    private Long odoId;
    /** 出库单号 */
    private String odoCode;
    /** 出库单优先级 */
    private Integer odoPriorityLevel;
    /** 出库单计划发货时间 */
    private Date odoPlanDeliverGoodsTime;
    /** 出库单下单时间 */
    private Date odoOrderTime;
    /** 分配规则ID */
    private Long allocateRuleId;
    /** 行号 */
    private Integer linenum;
    /** 店铺ID */
    private Long storeId;
    /** 外部单据行号 */
    private Integer extLinenum;
    /** 商品ID */
    private Long skuId;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 上位系统商品名称 */
    private String extSkuName;
    /** 数量 */
    private Double qty;
    /** 分配数量 */
    private Double allocateQty;
    /** 是否整单出库 */
    private Boolean isWholeOrderOutbound;
    /** 整行出库标志 */
    private Boolean fullLineOutbound;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 最小失效日期 */
    private Date minExpDate;
    /** 最大失效日期 */
    private Date maxExpDate;
    /** 批次号 */
    private String batchNumber;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private Long invStatus;
    /** 库存类型 */
    private String invType;
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
    /** 出库箱类型 */
    private Long outboundCartonType;
    /** 颜色 */
    private String color;
    /** 款式 */
    private String style;
    /** 尺码 */
    private String size;
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
    /** 是否占用整托/整箱 */
    private Boolean isPalletContainer;
    /** 整托整箱占用数量 */
    private Boolean palletContainerQty;
    /** 是否静态库位可超分配 */
    private Boolean isStaticLocationAllocate;
    /** 被占用的静态库位ID */
    private String staticLocationIds;
    /** 整托占用IDS */
    private String trayIds;
    /** 整箱占用IDS */
    private String packingCaseIds;
    
    // 用户自定义
    /** id */
    private Long id;
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
    /** 排序 */
    private Long sortNo;
    
	public Long getWaveId() {
		return waveId;
	}
	public void setWaveId(Long waveId) {
		this.waveId = waveId;
	}
	public Long getOdoLineId() {
		return odoLineId;
	}
	public void setOdoLineId(Long odoLineId) {
		this.odoLineId = odoLineId;
	}
	public Long getOdoId() {
		return odoId;
	}
	public void setOdoId(Long odoId) {
		this.odoId = odoId;
	}
	public String getOdoCode() {
		return odoCode;
	}
	public void setOdoCode(String odoCode) {
		this.odoCode = odoCode;
	}
	public Integer getOdoPriorityLevel() {
		return odoPriorityLevel;
	}
	public void setOdoPriorityLevel(Integer odoPriorityLevel) {
		this.odoPriorityLevel = odoPriorityLevel;
	}
	public Date getOdoPlanDeliverGoodsTime() {
		return odoPlanDeliverGoodsTime;
	}
	public void setOdoPlanDeliverGoodsTime(Date odoPlanDeliverGoodsTime) {
		this.odoPlanDeliverGoodsTime = odoPlanDeliverGoodsTime;
	}
	public Date getOdoOrderTime() {
		return odoOrderTime;
	}
	public void setOdoOrderTime(Date odoOrderTime) {
		this.odoOrderTime = odoOrderTime;
	}
	public Long getAllocateRuleId() {
		return allocateRuleId;
	}
	public void setAllocateRuleId(Long allocateRuleId) {
		this.allocateRuleId = allocateRuleId;
	}
	public Integer getLinenum() {
		return linenum;
	}
	public void setLinenum(Integer linenum) {
		this.linenum = linenum;
	}
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	public Integer getExtLinenum() {
		return extLinenum;
	}
	public void setExtLinenum(Integer extLinenum) {
		this.extLinenum = extLinenum;
	}
	public Long getSkuId() {
		return skuId;
	}
	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
	public String getSkuBarCode() {
		return skuBarCode;
	}
	public void setSkuBarCode(String skuBarCode) {
		this.skuBarCode = skuBarCode;
	}
	public String getSkuName() {
		return skuName;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public String getExtSkuName() {
		return extSkuName;
	}
	public void setExtSkuName(String extSkuName) {
		this.extSkuName = extSkuName;
	}
	public Double getQty() {
		return qty;
	}
	public void setQty(Double qty) {
		this.qty = qty;
	}
	public Double getAllocateQty() {
		return allocateQty;
	}
	public void setAllocateQty(Double allocateQty) {
		this.allocateQty = allocateQty;
	}
	public Boolean getIsWholeOrderOutbound() {
		return isWholeOrderOutbound;
	}
	public void setIsWholeOrderOutbound(Boolean isWholeOrderOutbound) {
		this.isWholeOrderOutbound = isWholeOrderOutbound;
	}
	public Boolean getFullLineOutbound() {
		return fullLineOutbound;
	}
	public void setFullLineOutbound(Boolean fullLineOutbound) {
		this.fullLineOutbound = fullLineOutbound;
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
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
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
	public Long getOutboundCartonType() {
		return outboundCartonType;
	}
	public void setOutboundCartonType(Long outboundCartonType) {
		this.outboundCartonType = outboundCartonType;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
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
	public Boolean getIsPalletContainer() {
		return isPalletContainer;
	}
	public void setIsPalletContainer(Boolean isPalletContainer) {
		this.isPalletContainer = isPalletContainer;
	}
	public Boolean getPalletContainerQty() {
		return palletContainerQty;
	}
	public void setPalletContainerQty(Boolean palletContainerQty) {
		this.palletContainerQty = palletContainerQty;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Boolean getIsStaticLocationAllocate() {
		return isStaticLocationAllocate;
	}
	public void setIsStaticLocationAllocate(Boolean isStaticLocationAllocate) {
		this.isStaticLocationAllocate = isStaticLocationAllocate;
	}
	public String getStaticLocationIds() {
		return staticLocationIds;
	}
	public void setStaticLocationIds(String staticLocationIds) {
		this.staticLocationIds = staticLocationIds;
	}
	public String getTrayIds() {
		return trayIds;
	}
	public void setTrayIds(String trayIds) {
		this.trayIds = trayIds;
	}
	public String getPackingCaseIds() {
		return packingCaseIds;
	}
	public void setPackingCaseIds(String packingCaseIds) {
		this.packingCaseIds = packingCaseIds;
	}
    public Long getSortNo() {
        return sortNo;
    }
    public void setSortNo(Long sortNo) {
        this.sortNo = sortNo;
    }
	

}
