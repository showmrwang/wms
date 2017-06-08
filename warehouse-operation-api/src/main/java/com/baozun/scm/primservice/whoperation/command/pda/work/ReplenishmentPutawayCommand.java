package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentPutawayCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** 功能id */
    private Long functionId;
    /** 作业id */
    private Long operationId;
    /** 补货方式 */
    private Integer replenishWay;
    /** 提示库位编码 */
    private String tipLocationCode;
    /** 库位编码 */
    private String locationCode;
    /** 库位条码 */
    private String locationBarCode;
    /** 提示库位条码 */
    private String tipLocationBarCode;
    /** 工作条码 */
    private String workBarCode;
    /** 仓库id */
    private Long ouId;

    private Long userId;
    /** 库位id */
    private Long locationId;
    /***/
    private String tipTurnoverBoxCode;
    

    private String turnoverBoxCode;

    /**是否需要扫描库位*/
    private Boolean isNeedScanLocation = false;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanTurnoverBox = false;
    /**是否需要扫描托盘*/
    private Boolean isNeedScanPallet = false;
    /**是否扫描结束*/
    private Boolean isScanFinsh = false;
    /**是否整托盘整箱补货*/
    private Boolean isWholeCase = false;
    /**是否只有唯一目标库位*/
    private Boolean isOnlyLocation = false;  //默认单个库位

    private Boolean isPutawayToContainer = false; // 是否引入新的容器

    /** 是否需要扫商品的库存类型 */
    private Boolean isNeedScanSkuInvType = false;
    /** 提示商品库存类型 */
    private String skuInvType = "";
    /** 是否需要扫商品的库存状态 */
    private Boolean isNeedScanSkuInvStatus = false;
    /** 提示商品库存状态 */
    private Long skuInvStatus;
    private String skuInvStatusName;
    /** 是否需要扫描批次号 */
    private Boolean isNeedScanBatchNumber = false;
    /** 批次号 */
    private String batchNumber;
    /** 是否需要扫描原产地 */
    private Boolean isNeedScanOrigin = false; // 是否需要扫描原产地
    /** sku原产地 */
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

    private String skuSn;

    private String skuDefect;
    /** 提示商品sn */
    private String tipSkuSn;
    /** 提示商品残次信息 */
    private String tipSkuDefect;
    /** 是否提示商品sn */
    private Boolean isTipSkuSn = false;
    /** 是否提示商品残次 */
    private Boolean isTipSkuDefect = false;
    /** 是否需要扫描商品sn */
    private Boolean isNeedScanSkuSn = false;
    /** 是否需要扫描商品残次 */
    private Boolean isNeedScanSkuDefect = false;
    /** 是否扫描sku库存属性 */
    private Boolean isNeedSkuDetail;
    
    private String tipOuterContainerCode;
    
    private String outerContainerCode;
    private String tipInsideContainerCode;
    
    
    private Boolean isNeedScanSku = false;  //是否需要扫描sku
    
    /*** sku条码 */
    private String skuBarCode;
    /** 提示sku条码 */
    private String tipSkuBarCode;
    
    private Long skuId;
    
    private Integer scanPattern;
    
    private Double scanSkuQty;
    
    private Boolean isContinueScanSn = false;
    
    
    private String newTurnoverBoxCode;// 引入新容器
    
    private Boolean isPutawayScanInvAttr;  //是否扫描sku库存属性
    
    
    private Boolean isReplenishmentLocationBarcode;
    
    private Boolean isUniqueSkuAttrInside;
    
    /** 库内移动方式 */
    private Integer inWarehouseMoveWay;
    
    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
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

    public String getWorkBarCode() {
        return workBarCode;
    }

    public void setWorkBarCode(String workBarCode) {
        this.workBarCode = workBarCode;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getTipLocationCode() {
        return tipLocationCode;
    }

    public void setTipLocationCode(String tipLocationCode) {
        this.tipLocationCode = tipLocationCode;
    }

    public String getTipTurnoverBoxCode() {
        return tipTurnoverBoxCode;
    }

    public void setTipTurnoverBoxCode(String tipTurnoverBoxCode) {
        this.tipTurnoverBoxCode = tipTurnoverBoxCode;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public Boolean getIsNeedScanLocation() {
        return isNeedScanLocation;
    }

    public void setIsNeedScanLocation(Boolean isNeedScanLocation) {
        this.isNeedScanLocation = isNeedScanLocation;
    }

    public Boolean getIsNeedScanTurnoverBox() {
        return isNeedScanTurnoverBox;
    }

    public void setIsNeedScanTurnoverBox(Boolean isNeedScanTurnoverBox) {
        this.isNeedScanTurnoverBox = isNeedScanTurnoverBox;
    }

    public Boolean getIsScanFinsh() {
        return isScanFinsh;
    }

    public void setIsScanFinsh(Boolean isScanFinsh) {
        this.isScanFinsh = isScanFinsh;
    }

    public Boolean getIsWholeCase() {
        return isWholeCase;
    }

    public void setIsWholeCase(Boolean isWholeCase) {
        this.isWholeCase = isWholeCase;
    }

    public Boolean getIsOnlyLocation() {
        return isOnlyLocation;
    }

    public void setIsOnlyLocation(Boolean isOnlyLocation) {
        this.isOnlyLocation = isOnlyLocation;
    }

    public Integer getReplenishWay() {
        return replenishWay;
    }

    public void setReplenishWay(Integer replenishWay) {
        this.replenishWay = replenishWay;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Boolean getIsPutawayToContainer() {
        return isPutawayToContainer;
    }

    public void setIsPutawayToContainer(Boolean isPutawayToContainer) {
        this.isPutawayToContainer = isPutawayToContainer;
    }

    public String getTipLocationBarCode() {
        return tipLocationBarCode;
    }

    public void setTipLocationBarCode(String tipLocationBarCode) {
        this.tipLocationBarCode = tipLocationBarCode;
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

    public Long getSkuInvStatus() {
        return skuInvStatus;
    }

    public void setSkuInvStatus(Long skuInvStatus) {
        this.skuInvStatus = skuInvStatus;
    }

    public String getSkuInvStatusName() {
        return skuInvStatusName;
    }

    public void setSkuInvStatusName(String skuInvStatusName) {
        this.skuInvStatusName = skuInvStatusName;
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

    public String getSkuMfgDate() {
        return skuMfgDate;
    }

    public void setSkuMfgDate(String skuMfgDate) {
        this.skuMfgDate = skuMfgDate;
    }

    public Boolean getIsNeedScanSkuExpDate() {
        return isNeedScanSkuExpDate;
    }

    public void setIsNeedScanSkuExpDate(Boolean isNeedScanSkuExpDate) {
        this.isNeedScanSkuExpDate = isNeedScanSkuExpDate;
    }

    public String getSkuExpDate() {
        return skuExpDate;
    }

    public void setSkuExpDate(String skuExpDate) {
        this.skuExpDate = skuExpDate;
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

    public Long getTipSkuQty() {
        return tipSkuQty;
    }

    public void setTipSkuQty(Long tipSkuQty) {
        this.tipSkuQty = tipSkuQty;
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

    public String getTipSkuSn() {
        return tipSkuSn;
    }

    public void setTipSkuSn(String tipSkuSn) {
        this.tipSkuSn = tipSkuSn;
    }

    public String getTipSkuDefect() {
        return tipSkuDefect;
    }

    public void setTipSkuDefect(String tipSkuDefect) {
        this.tipSkuDefect = tipSkuDefect;
    }

    public Boolean getIsTipSkuSn() {
        return isTipSkuSn;
    }

    public void setIsTipSkuSn(Boolean isTipSkuSn) {
        this.isTipSkuSn = isTipSkuSn;
    }

    public Boolean getIsTipSkuDefect() {
        return isTipSkuDefect;
    }

    public void setIsTipSkuDefect(Boolean isTipSkuDefect) {
        this.isTipSkuDefect = isTipSkuDefect;
    }

    public Boolean getIsNeedScanSkuSn() {
        return isNeedScanSkuSn;
    }

    public void setIsNeedScanSkuSn(Boolean isNeedScanSkuSn) {
        this.isNeedScanSkuSn = isNeedScanSkuSn;
    }

    public Boolean getIsNeedScanSkuDefect() {
        return isNeedScanSkuDefect;
    }

    public void setIsNeedScanSkuDefect(Boolean isNeedScanSkuDefect) {
        this.isNeedScanSkuDefect = isNeedScanSkuDefect;
    }

    public Boolean getIsNeedSkuDetail() {
        return isNeedSkuDetail;
    }

    public void setIsNeedSkuDetail(Boolean isNeedSkuDetail) {
        this.isNeedSkuDetail = isNeedSkuDetail;
    }

    public Boolean getIsNeedScanSku() {
        return isNeedScanSku;
    }

    public void setIsNeedScanSku(Boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getTipSkuBarCode() {
        return tipSkuBarCode;
    }

    public void setTipSkuBarCode(String tipSkuBarCode) {
        this.tipSkuBarCode = tipSkuBarCode;
    }

    public String getTipInsideContainerCode() {
        return tipInsideContainerCode;
    }

    public void setTipInsideContainerCode(String tipInsideContainerCode) {
        this.tipInsideContainerCode = tipInsideContainerCode;
    }

    public String getTipOuterContainerCode() {
        return tipOuterContainerCode;
    }

    public void setTipOuterContainerCode(String tipOuterContainerCode) {
        this.tipOuterContainerCode = tipOuterContainerCode;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Double getScanSkuQty() {
        return scanSkuQty;
    }

    public void setScanSkuQty(Double scanSkuQty) {
        this.scanSkuQty = scanSkuQty;
    }

    public Boolean getIsContinueScanSn() {
        return isContinueScanSn;
    }

    public void setIsContinueScanSn(Boolean isContinueScanSn) {
        this.isContinueScanSn = isContinueScanSn;
    }

    public String getNewTurnoverBoxCode() {
        return newTurnoverBoxCode;
    }

    public void setNewTurnoverBoxCode(String newTurnoverBoxCode) {
        this.newTurnoverBoxCode = newTurnoverBoxCode;
    }

    public Boolean getIsPutawayScanInvAttr() {
        return isPutawayScanInvAttr;
    }

    public void setIsPutawayScanInvAttr(Boolean isPutawayScanInvAttr) {
        this.isPutawayScanInvAttr = isPutawayScanInvAttr;
    }

    public Boolean getIsReplenishmentLocationBarcode() {
        return isReplenishmentLocationBarcode;
    }

    public void setIsReplenishmentLocationBarcode(Boolean isReplenishmentLocationBarcode) {
        this.isReplenishmentLocationBarcode = isReplenishmentLocationBarcode;
    }

    public Boolean getIsUniqueSkuAttrInside() {
        return isUniqueSkuAttrInside;
    }

    public void setIsUniqueSkuAttrInside(Boolean isUniqueSkuAttrInside) {
        this.isUniqueSkuAttrInside = isUniqueSkuAttrInside;
    }

    public Integer getInWarehouseMoveWay() {
        return inWarehouseMoveWay;
    }

    public void setInWarehouseMoveWay(Integer inWarehouseMoveWay) {
        this.inWarehouseMoveWay = inWarehouseMoveWay;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public Boolean getIsNeedScanPallet() {
        return isNeedScanPallet;
    }

    public void setIsNeedScanPallet(Boolean isNeedScanPallet) {
        this.isNeedScanPallet = isNeedScanPallet;
    }
    

}
