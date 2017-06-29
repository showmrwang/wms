package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class PickingScanResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 5696756957904723061L;

    private Boolean isLastWork =false;  //是否是最后一箱
    
    private Boolean isShortPickingEnd;   //是否短拣
    
    private Boolean isPartlyFinish = false;   //是否部分完成

    private Long workAreaId;
    /** 小批次 */
    private String batch;
    /** 工作条码 */
    private String workBarCode;

    /** 小车，出库箱，周转箱名称 */
    private String name;
    /**
     * 提示外部容器号(小车,外部容器)
     */
    private String tipOuterContainer;
    /** 外部容器号(小车,外部容器) */
    private String outerContainer;
    /** 出库箱号 */
    private String outBounxBoxCode;

    private Long outBoundBoxId;
    /** 作业id */
    private Long operationId;

    private Long functionId;
    /** 提示周转箱 */
    private String tipTurnoverBoxCode;
    /** 周转箱 */
    private String turnoverBoxCode;
    private Boolean isRemmendContainer = true; // 是否是推荐容器拣货,true是推荐容器拣货,false是整箱整托拣货 , 默认推荐容器拣货
    /** 是否扫描库位 */
    private Boolean isScanLocation = false; // 默认不扫描库位

    /** 提示托盘 */
    private String tipOuterContainerCode;
    /** 是否提示外部托盘 */
    private Boolean isTipOuterContainer;
    /** 提示内部容器号 */
    private String tipInsideContainerCode;
    /** 是否提示内部容器 */
    private Boolean isTipinsideCotnainer = false;
    /** 托盘 */
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

    private Long userId;
    /** 捡货方式 */
    private Integer pickingWay;
    /** 库存占用模型 */
    private Integer invOccupyMode;
    /** 是否扫描拣货库位托盘 */
    private java.lang.Boolean isScanOuterContainer = false;
    /** 是否已经扫描外部容器 */
//    private Boolean isNeedScanOuter = false;
    /** 是否扫描拣货库位货箱 */
    private java.lang.Boolean isScanInsideContainer = false;
    /** 是否已经扫描内部容器 */
//    private Boolean isNeedScanInsider = false;
    /** 是否扫描商品 */
    private java.lang.Boolean isScanSku = false;
    /** 扫描模式 1数量扫描 2逐件扫描 默认数量扫描 */
    private java.lang.Integer scanPattern;
    /** 是否提示商品库存属性 */
    private java.lang.Boolean isTipInvAttr = false;
    /** 是否扫描商品库存属性 */
    private java.lang.Boolean isScanInvAttr= false;
    /** 是否扫描货格 */
    private java.lang.Boolean isScanLatticeNo;
    /** 整拖拣货模式 */
    private java.lang.Integer palletPickingMode;
    /** 整箱拣货模式 */
    private java.lang.Integer containerPickingMode;
    /** 是否拣货完毕 */
    private Boolean isPicking = false;

    private Boolean isInboundLocationBarcode;
    /*** sku条码 */
    private String skuBarCode;
    /** 提示sku条码 */
    private String tipSkuBarCode;
    /** 扫描skuId */
    private Long skuId;
    /*** 货箱内待拣货sku库存属性是否唯一 */
    private Boolean isUniqueSkuAttrInside = false;
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

    /** 库位上外部容器是否扫描完毕 */
    private Boolean isNeedTipOutContainer = false;
    /** 库位上内部容器是否扫描完毕 */
    private Boolean isNeedTipInsideContainer = false;
    /** 是否提示商品 (所有的商品sku是否已经扫描完毕) */
    private Boolean isNeedTipSku = false;
    /** 库位是否扫描完毕 */
    private Boolean isNeedTipLocation = false;
    /** 出库箱编码 */
    private String tipOutBounxBoxCode;
    /** 是否需要出库箱编码 */
    private Boolean isNeedScanOutBounxBox = false; // ispicking = 3,4是为true

    private Boolean isNeedScanLatticeNo = false;// 是否要扫描货格
    /** 货格号 */
    private Integer useContainerLatticeNo;
    /** 是否短拣 */
    private Boolean isShortPicking = false;
    /** 是否满箱 */
    private Boolean isTrunkful = false;
    /** 出库箱/周转箱满箱后是否使用新的容器 */
    private Boolean isUserNewContainer = false;

    private Boolean isNeedScanSku = false; // 是否扫描sku

    private Boolean isNeedTipLoc;

    private Boolean isScanOutBoundBox = false; // 是否已经扫描出库箱
    

    private Double scanSkuQty; // 扫描数量
    /** 是否继续扫描sn明细 */
    private Boolean isContinueScanSn = false; // 默认不继续扫描sn
    
    private Integer tempReplenishWay;
    
    private String tipSkuMutilBarcode;
    
    
    private Boolean isMgmtConsumableSku;   //是否管理耗材


    /**
     * 出库箱集合(仅限于有小车,有出库箱的情况)
     */
    private Set<Long> outBoundIds = new HashSet<Long>();

    /************************************************** 整托整箱开始 **************************************************/
    /** 推荐内部容器 */
    private List<String> tipContainer = new ArrayList<String>();
    /** 推荐商品 */
    private List<Long> tipSkuLst = new ArrayList<Long>();
    /** 推荐当前商品 */
    private List<Long> tipCurrentSkuLst = new ArrayList<Long>();
    /** 推荐唯一sku */
    private List<String> onlySkuLst = new ArrayList<String>();

    /** 判断是否提示商品属性 */
    private Boolean isNeedTipSkuAttr;
    /** 判断是否扫描商品属性 */
    private Boolean isNeedScanSkuAttr;
    /** 推荐唯一sku */
    private String onlySku;
    /** 推荐唯一sku */
    private Long onlySkuQty;
    /** 提示内部容器id */
    private Long tipInsideContainerId;
    /** 判断是否是SN/残次商品 */
    private Boolean isSkuSn;
    /** 判断是否占用SN/残次条码 */
    private Boolean isSkuSnOccupation;
    
    /** 库内移动方式 */
    private Integer inWarehouseMoveWay;
    /** 取消模式 */
    private Integer cancelPattern;
    
    /************************************************** 整托整箱结束 **************************************************/

    /************************************************** pda补货开始 **************************************************/

    /** 补货方式 */
    private Integer replenishWay;

    /************************************************** pda补货结束 **************************************************/
    /** 拣货模式:2, 播种墙; 其余为非播种墙*/
    private String pickingMode;


    public String getWorkBarCode() {
        return workBarCode;
    }

    public void setWorkBarCode(String workBarCode) {
        this.workBarCode = workBarCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipOuterContainer() {
        return tipOuterContainer;
    }

    public void setTipOuterContainer(String tipOuterContainer) {
        this.tipOuterContainer = tipOuterContainer;
    }

    public String getOuterContainer() {
        return outerContainer;
    }

    public void setOuterContainer(String outerContainer) {
        this.outerContainer = outerContainer;
    }

    public String getOutBounxBoxCode() {
        return outBounxBoxCode;
    }

    public void setOutBounxBoxCode(String outBounxBoxCode) {
        this.outBounxBoxCode = outBounxBoxCode;
    }

    public Long getOutBoundBoxId() {
        return outBoundBoxId;
    }

    public void setOutBoundBoxId(Long outBoundBoxId) {
        this.outBoundBoxId = outBoundBoxId;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
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

    public Boolean getIsRemmendContainer() {
        return isRemmendContainer;
    }

    public void setIsRemmendContainer(Boolean isRemmendContainer) {
        this.isRemmendContainer = isRemmendContainer;
    }

    public Boolean getIsScanLocation() {
        return isScanLocation;
    }

    public void setIsScanLocation(Boolean isScanLocation) {
        this.isScanLocation = isScanLocation;
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

    public Long resultCommand() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

    public Integer getPickingWay() {
        return pickingWay;
    }

    public void setPickingWay(Integer pickingWay) {
        this.pickingWay = pickingWay;
    }

    public Integer getInvOccupyMode() {
        return invOccupyMode;
    }

    public void setInvOccupyMode(Integer invOccupyMode) {
        this.invOccupyMode = invOccupyMode;
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

    public Boolean getIsPicking() {
        return isPicking;
    }

    public void setIsPicking(Boolean isPicking) {
        this.isPicking = isPicking;
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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getTipOutBounxBoxCode() {
        return tipOutBounxBoxCode;
    }

    public void setTipOutBounxBoxCode(String tipOutBounxBoxCode) {
        this.tipOutBounxBoxCode = tipOutBounxBoxCode;
    }

    public Boolean getIsNeedScanOutBounxBox() {
        return isNeedScanOutBounxBox;
    }

    public void setIsNeedScanOutBounxBox(Boolean isNeedScanOutBounxBox) {
        this.isNeedScanOutBounxBox = isNeedScanOutBounxBox;
    }

    public Integer getUseContainerLatticeNo() {
        return useContainerLatticeNo;
    }

    public void setUseContainerLatticeNo(Integer useContainerLatticeNo) {
        this.useContainerLatticeNo = useContainerLatticeNo;
    }

    public Boolean getIsShortPicking() {
        return isShortPicking;
    }

    public void setIsShortPicking(Boolean isShortPicking) {
        this.isShortPicking = isShortPicking;
    }

    public Boolean getIsTrunkful() {
        return isTrunkful;
    }

    public void setIsTrunkful(Boolean isTrunkful) {
        this.isTrunkful = isTrunkful;
    }

    public Boolean getIsUserNewContainer() {
        return isUserNewContainer;
    }

    public void setIsUserNewContainer(Boolean isUserNewContainer) {
        this.isUserNewContainer = isUserNewContainer;
    }

    public Set<Long> getOutBoundIds() {
        return outBoundIds;
    }

    public void setOutBoundIds(Set<Long> outBoundIds) {
        this.outBoundIds = outBoundIds;
    }

    public List<String> getTipContainer() {
        return tipContainer;
    }

    public void setTipContainer(List<String> tipContainer) {
        this.tipContainer = tipContainer;
    }

    public List<Long> getTipSkuLst() {
        return tipSkuLst;
    }

    public void setTipSkuLst(List<Long> tipSkuLst) {
        this.tipSkuLst = tipSkuLst;
    }

    public List<Long> getTipCurrentSkuLst() {
        return tipCurrentSkuLst;
    }

    public void setTipCurrentSkuLst(List<Long> tipCurrentSkuLst) {
        this.tipCurrentSkuLst = tipCurrentSkuLst;
    }

    public Boolean getIsNeedTipSkuAttr() {
        return isNeedTipSkuAttr;
    }

    public void setIsNeedTipSkuAttr(Boolean isNeedTipSkuAttr) {
        this.isNeedTipSkuAttr = isNeedTipSkuAttr;
    }

    public Boolean getIsNeedScanSkuAttr() {
        return isNeedScanSkuAttr;
    }

    public void setIsNeedScanSkuAttr(Boolean isNeedScanSkuAttr) {
        this.isNeedScanSkuAttr = isNeedScanSkuAttr;
    }

    public String getOnlySku() {
        return onlySku;
    }

    public void setOnlySku(String onlySku) {
        this.onlySku = onlySku;
    }

    public Long getOnlySkuQty() {
        return onlySkuQty;
    }

    public void setOnlySkuQty(Long onlySkuQty) {
        this.onlySkuQty = onlySkuQty;
    }

    public Long getTipInsideContainerId() {
        return tipInsideContainerId;
    }

    public void setTipInsideContainerId(Long tipInsideContainerId) {
        this.tipInsideContainerId = tipInsideContainerId;
    }

    public List<String> getOnlySkuLst() {
        return onlySkuLst;
    }

    public void setOnlySkuLst(List<String> onlySkuLst) {
        this.onlySkuLst = onlySkuLst;
    }

    public Boolean getIsSkuSn() {
        return isSkuSn;
    }

    public void setIsSkuSn(Boolean isSkuSn) {
        this.isSkuSn = isSkuSn;
    }

    public Boolean getIsSkuSnOccupation() {
        return isSkuSnOccupation;
    }

    public void setIsSkuSnOccupation(Boolean isSkuSnOccupation) {
        this.isSkuSnOccupation = isSkuSnOccupation;
    }

    public Boolean getIsTipinsideCotnainer() {
        return isTipinsideCotnainer;
    }

    public void setIsTipinsideCotnainer(Boolean isTipinsideCotnainer) {
        this.isTipinsideCotnainer = isTipinsideCotnainer;
    }

    public Boolean getIsTipOuterContainer() {
        return isTipOuterContainer;
    }

    public void setIsTipOuterContainer(Boolean isTipOuterContainer) {
        this.isTipOuterContainer = isTipOuterContainer;
    }

//    public Boolean getIsNeedScanOuter() {
//        return isNeedScanOuter;
//    }
//
//    public void setIsNeedScanOuter(Boolean isNeedScanOuter) {
//        this.isNeedScanOuter = isNeedScanOuter;
//    }
//
//    public Boolean getIsNeedScanInsider() {
//        return isNeedScanInsider;
//    }
//
//    public void setIsNeedScanInsider(Boolean isNeedScanInsider) {
//        this.isNeedScanInsider = isNeedScanInsider;
//    }

    public Boolean getIsNeedScanSku() {
        return isNeedScanSku;
    }

    public void setIsNeedScanSku(Boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public String getTipSkuBarCode() {
        return tipSkuBarCode;
    }

    public void setTipSkuBarCode(String tipSkuBarCode) {
        this.tipSkuBarCode = tipSkuBarCode;
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


    public Integer getReplenishWay() {
        return replenishWay;
    }

    public void setReplenishWay(Integer replenishWay) {
        this.replenishWay = replenishWay;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Long getWorkAreaId() {
        return workAreaId;
    }

    public void setWorkAreaId(Long workAreaId) {
        this.workAreaId = workAreaId;
    }

    public Boolean getIsNeedTipLoc() {
        return isNeedTipLoc;
    }

    public void setIsNeedTipLoc(Boolean isNeedTipLoc) {
        this.isNeedTipLoc = isNeedTipLoc;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Boolean getIsScanOutBoundBox() {
        return isScanOutBoundBox;
    }

    public void setIsScanOutBoundBox(Boolean isScanOutBoundBox) {
        this.isScanOutBoundBox = isScanOutBoundBox;
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

    public Double getScanSkuQty() {
        return scanSkuQty;
    }

    public void setScanSkuQty(Double scanSkuQty) {
        this.scanSkuQty = scanSkuQty;
    }

    public Long getSkuInvStatus() {
        return skuInvStatus;
    }

    public void setSkuInvStatus(Long skuInvStatus) {
        this.skuInvStatus = skuInvStatus;
    }

    public Boolean getIsContinueScanSn() {
        return isContinueScanSn;
    }

    public void setIsContinueScanSn(Boolean isContinueScanSn) {
        this.isContinueScanSn = isContinueScanSn;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public Boolean getIsShortPickingEnd() {
        return isShortPickingEnd;
    }

    public void setIsShortPickingEnd(Boolean isShortPickingEnd) {
        this.isShortPickingEnd = isShortPickingEnd;
    }

    public Boolean getIsLastWork() {
        return isLastWork;
    }

    public void setIsLastWork(Boolean isLastWork) {
        this.isLastWork = isLastWork;
    }

    public String getSkuInvStatusName() {
        return skuInvStatusName;
    }

    public void setSkuInvStatusName(String skuInvStatusName) {
        this.skuInvStatusName = skuInvStatusName;
    }

    public Integer getInWarehouseMoveWay() {
        return inWarehouseMoveWay;
    }

    public void setInWarehouseMoveWay(Integer inWarehouseMoveWay) {
        this.inWarehouseMoveWay = inWarehouseMoveWay;
    }

    public Integer getCancelPattern() {
        return cancelPattern;
    }

    public void setCancelPattern(Integer cancelPattern) {
        this.cancelPattern = cancelPattern;
    }

    public Boolean getIsNeedScanLatticeNo() {
        return isNeedScanLatticeNo;
    }

    public void setIsNeedScanLatticeNo(Boolean isNeedScanLatticeNo) {
        this.isNeedScanLatticeNo = isNeedScanLatticeNo;
    }

    public Integer getTempReplenishWay() {
        return tempReplenishWay;
    }

    public void setTempReplenishWay(Integer tempReplenishWay) {
        this.tempReplenishWay = tempReplenishWay;
    }

    public String getTipSkuMutilBarcode() {
        return tipSkuMutilBarcode;
    }

    public void setTipSkuMutilBarcode(String tipSkuMutilBarcode) {
        this.tipSkuMutilBarcode = tipSkuMutilBarcode;
    }


    public Boolean getIsPartlyFinish() {
        return isPartlyFinish;
    }

    public void setIsPartlyFinish(Boolean isPartlyFinish) {
        this.isPartlyFinish = isPartlyFinish;
    }
    public Boolean getIsMgmtConsumableSku() {
        return isMgmtConsumableSku;
    }

    public void setIsMgmtConsumableSku(Boolean isMgmtConsumableSku) {
        this.isMgmtConsumableSku = isMgmtConsumableSku;
    }


}
