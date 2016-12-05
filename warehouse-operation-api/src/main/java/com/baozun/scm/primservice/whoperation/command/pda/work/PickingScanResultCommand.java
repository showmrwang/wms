package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.HashSet;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class PickingScanResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 5696756957904723061L;

    /**
     * 提示外部容器号(小车,外部容器)
     */
    private String tipOuterContainer;
    /** 外部容器号(小车,外部容器) */
    private String outerContainer;

    /** 提示出库箱号 */
    private String tipOutBoundCode;
    /** 出库箱号 */
    private String outBoundCode;
    /** 作业id */
    private Long operatorId;

    private Long functionId;
    /** 提示周转箱 */
    private String tipTurnoverBoxCode;
    /** 周转箱 */
    private String turnoverBoxCode;
    private Boolean isRemmendContainer = true; // 是否是推荐容器拣货,true是推荐容器拣货,false是整箱整托拣货 , 默认推荐容器拣货
    /** 是否扫描库位 */
    private Boolean isScanLocation = false;   //默认不扫描库位

    /**提示托盘*/
    private String tipOuterContainerCode;
    /** 提示内部容器号 */
    private String tipInsideContainerCode;
    /**托盘*/
    private String outerContainerCode;
    /** 内部容器号 */
    private String insideContainerCode;
    /** 库位id */
    private Long locationId;
    private String tipLocationCode; // 提示库位编码

    private String tipLocationBarCode; // 提示库位条码
    private String locationCode; // 库位编码

    private String locationBarCode; // 库位条码
    /** 仓库id */
    private Long ouId;
    /** 捡货方式 */
    private Integer pickingWay;
    /** 库存占用模型 */
    private Integer invOccupyMode;
    /** 是否扫描拣货库位托盘 */
    private java.lang.Boolean isScanOuterContainer;
    /** 是否扫描拣货库位货箱 */
    private java.lang.Boolean isScanInsideContainer;
    /** 是否扫描商品 */
    private java.lang.Boolean isScanSku;
    /** 扫描模式 1数量扫描 2逐件扫描 默认数量扫描 */
    private java.lang.Integer scanPattern;
    /** 是否提示商品库存属性 */
    private java.lang.Boolean isTipInvAttr;
    /** 是否扫描商品库存属性 */
    private java.lang.Boolean isScanInvAttr;
    /** 是否扫描货格 */
    private java.lang.Boolean isScanLatticeNo;
    /** 整拖拣货模式 */
    private java.lang.Integer palletPickingMode;
    /** 整箱拣货模式 */
    private java.lang.Integer containerPickingMode;
    /**是否拣货完毕*/
    private Boolean isPicking;
    
    private Boolean isInboundLocationBarcode;
    /***sku条码*/
    private String skuBarCode;
    /**扫描skuId*/
    private Long skuId;
    /***货箱内待拣货sku库存属性是否唯一*/
    private Boolean isUniqueSkuAttrInside;
    /** 是否需要扫商品的库存类型 */
    private Boolean isNeedScanSkuInvType = false;
    /** 提示商品库存类型 */
    private String skuInvType = "";
    /** 是否需要扫商品的库存状态 */
    private Boolean isNeedScanSkuInvStatus = false;
    /** 提示商品库存状态 */
    private String skuInvStatus = "";
    /**是否需要扫描批次号*/
    private Boolean isNeedScanBatchNumber = false;
    /**批次号*/
    private String batchNumber;
    /**是否需要扫描原产地*/
    private Boolean isNeedScanOrigin = false;   //是否需要扫描原产地
   /**sku原产地*/
    private String skuOrigin;
    /** 是否需要扫商品的生产日期 */
    private Boolean isNeedScanSkuMfgDate = false;
    /** 提示商品生产日期 */
    private String skuMfgDate;
    /** 是否需要扫商品过期日期 */
    private Boolean isNeedScanSkuExpDate = false;
    /** 提示商品过期日期 */
    private String skuExpDate;
    /** 是否需要扫商品库存属性1 */
    private Boolean isNeedScanSkuInvAttr1 = false;
    /** 提示商品库存属性1 */
    private String skuInvAttr1 = "";
    /** 是否需要扫商品库存属性2 */
    private Boolean isNeedScanSkuInvAttr2 = false;
    /** 提示商品库存属性2 */
    private String skuInvAttr2 = "";
    /** 是否需要扫商品库存属性3 */
    private Boolean isNeedScanSkuInvAttr3 = false;
    /** 提示商品库存属性3 */
    private String skuInvAttr3;
    /** 是否需要扫商品库存属性4 */
    private Boolean isNeedScanSkuInvAttr4 = false;
    /** 提示商品库存属性4 */
    private String skuInvAttr4 = "";
    /** 是否需要扫商品库存属性5 */
    private Boolean isNeedScanSkuInvAttr5 = false;
    /** 提示商品库存属性5 */
    private String skuInvAttr5 = "";
    /** 提示商品数量 */
    private Long tipSkuQty;
    /** 提示商品sn */
    private Boolean IsNeedScanSkuSn;  
    /** 提示商品defect*/
    private Boolean IsNeedScanSkuDefect;  
    
    private String skuSn;
    
    private String skuDefect;
    /**是否扫描sku库存属性*/
    private Boolean isNeedSkuDetail;
    
    /** 库位上外部容器是否扫描完毕*/
    private Boolean isNeedTipOutContainer;
    /**库位上内部容器是否扫描完毕*/
    private Boolean isNeedTipInsideContainer;
    /** 是否提示商品 (所有的商品sku是否已经扫描完毕)*/
    private Boolean isNeedTipSku;
    /**库位是否扫描完毕*/
    private Boolean isNeedTipLocation;

    /**
     * 出库箱集合(仅限于有小车,有出库箱的情况)
     */
    private Set<Long> outBoundIds = new HashSet<Long>();

    public String getTipOutBoundCode() {
        return tipOutBoundCode;
    }


    public void setTipOutBoundCode(String tipOutBoundCode) {
        this.tipOutBoundCode = tipOutBoundCode;
    }


    public Long getOperatorId() {
        return operatorId;
    }


    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }


    public Boolean getIsRemmendContainer() {
        return isRemmendContainer;
    }


    public void setIsRemmendContainer(Boolean isRemmendContainer) {
        this.isRemmendContainer = isRemmendContainer;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }


    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }


    public String getOutBoundCode() {
        return outBoundCode;
    }


    public void setOutBoundCode(String outBoundCode) {
        this.outBoundCode = outBoundCode;
    }


    public Long getOuId() {
        return ouId;
    }


    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }


    public Integer getPickingWay() {
        return pickingWay;
    }


    public void setPickingWay(Integer pickingWay) {
        this.pickingWay = pickingWay;
    }


    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }


    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }


    public String getTipTurnoverBoxCode() {
        return tipTurnoverBoxCode;
    }


    public void setTipTurnoverBoxCode(String tipTurnoverBoxCode) {
        this.tipTurnoverBoxCode = tipTurnoverBoxCode;
    }


    public Long getFunctionId() {
        return functionId;
    }


    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }


    public String getTipOuterContainer() {
        return tipOuterContainer;
    }


    public void setTipOuterContainer(String tipOuterContainer) {
        this.tipOuterContainer = tipOuterContainer;
    }


    public Boolean getIsScanLocation() {
        return isScanLocation;
    }


    public void setIsScanLocation(Boolean isScanLocation) {
        this.isScanLocation = isScanLocation;
    }

    public String getLocationCode() {
        return locationCode;
    }


    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }


    public String getLocationBarCode() {
        return locationBarCode;
    }


    public void setLocationBarCode(String locationBarCode) {
        this.locationBarCode = locationBarCode;
    }


    public Integer getInvOccupyMode() {
        return invOccupyMode;
    }


    public void setInvOccupyMode(Integer invOccupyMode) {
        this.invOccupyMode = invOccupyMode;
    }


    public Set<Long> getOutBoundIds() {
        return outBoundIds;
    }


    public void setOutBoundIds(Set<Long> outBoundIds) {
        this.outBoundIds = outBoundIds;
    }


    public String getOuterContainer() {
        return outerContainer;
    }


    public void setOuterContainer(String outerContainer) {
        this.outerContainer = outerContainer;
    }


    public java.lang.Boolean getIsScanOuterContainer() {
        return isScanOuterContainer;
    }


    public void setIsScanOuterContainer(java.lang.Boolean isScanOuterContainer) {
        this.isScanOuterContainer = isScanOuterContainer;
    }


    public java.lang.Boolean getIsScanInsideContainer() {
        return isScanInsideContainer;
    }


    public void setIsScanInsideContainer(java.lang.Boolean isScanInsideContainer) {
        this.isScanInsideContainer = isScanInsideContainer;
    }


    public java.lang.Boolean getIsScanSku() {
        return isScanSku;
    }


    public void setIsScanSku(java.lang.Boolean isScanSku) {
        this.isScanSku = isScanSku;
    }


    public java.lang.Integer getScanPattern() {
        return scanPattern;
    }


    public void setScanPattern(java.lang.Integer scanPattern) {
        this.scanPattern = scanPattern;
    }


    public java.lang.Boolean getIsTipInvAttr() {
        return isTipInvAttr;
    }


    public void setIsTipInvAttr(java.lang.Boolean isTipInvAttr) {
        this.isTipInvAttr = isTipInvAttr;
    }


    public java.lang.Boolean getIsScanInvAttr() {
        return isScanInvAttr;
    }


    public void setIsScanInvAttr(java.lang.Boolean isScanInvAttr) {
        this.isScanInvAttr = isScanInvAttr;
    }


    public java.lang.Boolean getIsScanLatticeNo() {
        return isScanLatticeNo;
    }


    public void setIsScanLatticeNo(java.lang.Boolean isScanLatticeNo) {
        this.isScanLatticeNo = isScanLatticeNo;
    }


    public java.lang.Integer getPalletPickingMode() {
        return palletPickingMode;
    }


    public void setPalletPickingMode(java.lang.Integer palletPickingMode) {
        this.palletPickingMode = palletPickingMode;
    }


    public java.lang.Integer getContainerPickingMode() {
        return containerPickingMode;
    }


    public void setContainerPickingMode(java.lang.Integer containerPickingMode) {
        this.containerPickingMode = containerPickingMode;
    }


    public String getTipLocationCode() {
        return tipLocationCode;
    }


    public void setTipLocationCode(String tipLocationCode) {
        this.tipLocationCode = tipLocationCode;
    }


    public String getTipLocationBarCode() {
        return tipLocationBarCode;
    }


    public void setTipLocationBarCode(String tipLocationBarCode) {
        this.tipLocationBarCode = tipLocationBarCode;
    }


    public Long getLocationId() {
        return locationId;
    }


    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }


    public String getOuterContainerCode() {
        return outerContainerCode;
    }


    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }


    public Boolean getIsPicking() {
        return isPicking;
    }


    public void setIsPicking(Boolean isPicking) {
        this.isPicking = isPicking;
    }


    public String getTipOuterContainerCode() {
        return tipOuterContainerCode;
    }


    public void setTipOuterContainerCode(String tipOuterContainerCode) {
        this.tipOuterContainerCode = tipOuterContainerCode;
    }


    public String getTipInsideContainerCode() {
        return tipInsideContainerCode;
    }


    public void setTipInsideContainerCode(String tipInsideContainerCode) {
        this.tipInsideContainerCode = tipInsideContainerCode;
    }


    public Boolean getIsInboundLocationBarcode() {
        return isInboundLocationBarcode;
    }


    public void setIsInboundLocationBarcode(Boolean isInboundLocationBarcode) {
        this.isInboundLocationBarcode = isInboundLocationBarcode;
    }


    public String getSkuBarCode() {
        return skuBarCode;
    }


    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }


    public Boolean getIsUniqueSkuAttrInside() {
        return isUniqueSkuAttrInside;
    }


    public void setIsUniqueSkuAttrInside(Boolean isUniqueSkuAttrInside) {
        this.isUniqueSkuAttrInside = isUniqueSkuAttrInside;
    }


    public Boolean getIsNeedScanSkuInvType() {
        return isNeedScanSkuInvType;
    }


    public void setIsNeedScanSkuInvType(Boolean isNeedScanSkuInvType) {
        this.isNeedScanSkuInvType = isNeedScanSkuInvType;
    }


    public String getSkuInvType() {
        return skuInvType;
    }


    public void setSkuInvType(String skuInvType) {
        this.skuInvType = skuInvType;
    }


    public Boolean getIsNeedScanSkuInvStatus() {
        return isNeedScanSkuInvStatus;
    }


    public void setIsNeedScanSkuInvStatus(Boolean isNeedScanSkuInvStatus) {
        this.isNeedScanSkuInvStatus = isNeedScanSkuInvStatus;
    }


    public String getSkuInvStatus() {
        return skuInvStatus;
    }


    public void setSkuInvStatus(String skuInvStatus) {
        this.skuInvStatus = skuInvStatus;
    }


    public Boolean getIsNeedScanBatchNumber() {
        return isNeedScanBatchNumber;
    }


    public void setIsNeedScanBatchNumber(Boolean isNeedScanBatchNumber) {
        this.isNeedScanBatchNumber = isNeedScanBatchNumber;
    }


    public String getBatchNumber() {
        return batchNumber;
    }


    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }


    public Boolean getIsNeedScanOrigin() {
        return isNeedScanOrigin;
    }


    public void setIsNeedScanOrigin(Boolean isNeedScanOrigin) {
        this.isNeedScanOrigin = isNeedScanOrigin;
    }


    public String getSkuOrigin() {
        return skuOrigin;
    }


    public void setSkuOrigin(String skuOrigin) {
        this.skuOrigin = skuOrigin;
    }


    public Boolean getIsNeedScanSkuMfgDate() {
        return isNeedScanSkuMfgDate;
    }


    public void setIsNeedScanSkuMfgDate(Boolean isNeedScanSkuMfgDate) {
        this.isNeedScanSkuMfgDate = isNeedScanSkuMfgDate;
    }

    public Boolean getIsNeedScanSkuExpDate() {
        return isNeedScanSkuExpDate;
    }


    public void setIsNeedScanSkuExpDate(Boolean isNeedScanSkuExpDate) {
        this.isNeedScanSkuExpDate = isNeedScanSkuExpDate;
    }

    public Boolean getIsNeedScanSkuInvAttr1() {
        return isNeedScanSkuInvAttr1;
    }


    public void setIsNeedScanSkuInvAttr1(Boolean isNeedScanSkuInvAttr1) {
        this.isNeedScanSkuInvAttr1 = isNeedScanSkuInvAttr1;
    }


    public String getSkuInvAttr1() {
        return skuInvAttr1;
    }


    public void setSkuInvAttr1(String skuInvAttr1) {
        this.skuInvAttr1 = skuInvAttr1;
    }


    public Boolean getIsNeedScanSkuInvAttr2() {
        return isNeedScanSkuInvAttr2;
    }


    public void setIsNeedScanSkuInvAttr2(Boolean isNeedScanSkuInvAttr2) {
        this.isNeedScanSkuInvAttr2 = isNeedScanSkuInvAttr2;
    }


    public String getSkuInvAttr2() {
        return skuInvAttr2;
    }


    public void setSkuInvAttr2(String skuInvAttr2) {
        this.skuInvAttr2 = skuInvAttr2;
    }


    public Boolean getIsNeedScanSkuInvAttr3() {
        return isNeedScanSkuInvAttr3;
    }


    public void setIsNeedScanSkuInvAttr3(Boolean isNeedScanSkuInvAttr3) {
        this.isNeedScanSkuInvAttr3 = isNeedScanSkuInvAttr3;
    }


    public String getSkuInvAttr3() {
        return skuInvAttr3;
    }


    public void setSkuInvAttr3(String skuInvAttr3) {
        this.skuInvAttr3 = skuInvAttr3;
    }


    public Boolean getIsNeedScanSkuInvAttr4() {
        return isNeedScanSkuInvAttr4;
    }


    public void setIsNeedScanSkuInvAttr4(Boolean isNeedScanSkuInvAttr4) {
        this.isNeedScanSkuInvAttr4 = isNeedScanSkuInvAttr4;
    }


    public String getSkuInvAttr4() {
        return skuInvAttr4;
    }


    public void setSkuInvAttr4(String skuInvAttr4) {
        this.skuInvAttr4 = skuInvAttr4;
    }


    public Boolean getIsNeedScanSkuInvAttr5() {
        return isNeedScanSkuInvAttr5;
    }


    public void setIsNeedScanSkuInvAttr5(Boolean isNeedScanSkuInvAttr5) {
        this.isNeedScanSkuInvAttr5 = isNeedScanSkuInvAttr5;
    }


    public String getSkuInvAttr5() {
        return skuInvAttr5;
    }


    public void setSkuInvAttr5(String skuInvAttr5) {
        this.skuInvAttr5 = skuInvAttr5;
    }


    public Long getSkuId() {
        return skuId;
    }


    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }


    public Long getTipSkuQty() {
        return tipSkuQty;
    }


    public void setTipSkuQty(Long tipSkuQty) {
        this.tipSkuQty = tipSkuQty;
    }


    public String getSkuMfgDate() {
        return skuMfgDate;
    }


    public void setSkuMfgDate(String skuMfgDate) {
        this.skuMfgDate = skuMfgDate;
    }


    public String getSkuExpDate() {
        return skuExpDate;
    }


    public void setSkuExpDate(String skuExpDate) {
        this.skuExpDate = skuExpDate;
    }


    public Boolean getIsNeedScanSkuSn() {
        return IsNeedScanSkuSn;
    }


    public void setIsNeedScanSkuSn(Boolean isNeedScanSkuSn) {
        IsNeedScanSkuSn = isNeedScanSkuSn;
    }


    public Boolean getIsNeedScanSkuDefect() {
        return IsNeedScanSkuDefect;
    }


    public void setIsNeedScanSkuDefect(Boolean isNeedScanSkuDefect) {
        IsNeedScanSkuDefect = isNeedScanSkuDefect;
    }


    public String getSkuSn() {
        return skuSn;
    }


    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }


    public String getSkuDefect() {
        return skuDefect;
    }


    public void setSkuDefect(String skuDefect) {
        this.skuDefect = skuDefect;
    }


    public Boolean getIsNeedSkuDetail() {
        return isNeedSkuDetail;
    }


    public void setIsNeedSkuDetail(Boolean isNeedSkuDetail) {
        this.isNeedSkuDetail = isNeedSkuDetail;
    }


    public Boolean getIsNeedTipOutContainer() {
        return isNeedTipOutContainer;
    }


    public void setIsNeedTipOutContainer(Boolean isNeedTipOutContainer) {
        this.isNeedTipOutContainer = isNeedTipOutContainer;
    }


    public Boolean getIsNeedTipInsideContainer() {
        return isNeedTipInsideContainer;
    }


    public void setIsNeedTipInsideContainer(Boolean isNeedTipInsideContainer) {
        this.isNeedTipInsideContainer = isNeedTipInsideContainer;
    }


    public Boolean getIsNeedTipSku() {
        return isNeedTipSku;
    }


    public void setIsNeedTipSku(Boolean isNeedTipSku) {
        this.isNeedTipSku = isNeedTipSku;
    }


    public Boolean getIsNeedTipLocation() {
        return isNeedTipLocation;
    }


    public void setIsNeedTipLocation(Boolean isNeedTipLocation) {
        this.isNeedTipLocation = isNeedTipLocation;
    }

    

    

}
