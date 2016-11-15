package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class LocationCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 8716540162202662736L;
    /* 库位id */
    private Long id;
    /* 库位编码 */
    private String code;
    /* 库位条码 */
    private String barCode;
    /* 系统编制库位编码 */
    private String sysCompileCode;
    /* 手工编制库位编码 */
    private String manualCode;
    /* 所属组织架构ID */
    private Long ouId;
    /* 上架区域 */
    private Long shelfAreaId;
    /* 分配区域 */
    private Long allocateAreaId;
    /* 工作区域 */
    private Long workAreaId;
    /* 库存状态 */
    private Long statusId;
    /* 是否允许混放 */
    private Boolean isMixStacking;
    /* 最大混放SKU数 */
    private Integer mixStackingNumber;
    /* 是否需要补货条码 */
    private Boolean isReplenishmentBarcode;
    /* 是否管理效期 */
    private Boolean isValidMgt;
    /* 是否管理批次号 */
    private Boolean isBatchMgt;
    /* 是否跟踪容器号 */
    private Boolean isTrackVessel;
    /* 是否临时库位 */
    private Boolean isTemporaryLocation;
    /* 对应模板CODE */
    private String templetCode;
    /* 创建时间 */
    private Date createTime;
    /* 最后操作时间 */
    private Date lastModifyTime;
    /* 操作人ID */
    private Long operatorId;
    /* 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /* 对应模板NAME */
    private String templetName;
    /* 所属物理仓 */
    private String ouName;
    /* 上架区域 */
    private String shelfArea;
    /* 分配区域 */
    private String allocateArea;
    /* 工作区域 */
    private String workArea;
    /* 库存状态 */
    private String status;
    /* 上架顺序 */
    private String shelfSort;
    /* 拣货顺序 */

    private String pickSort;

    private String replenishmentBarcode;

    private String temporaryCode;
    private String latestSkuCode;
    private String uuid;
    /* 模板Id */
    private Long templetId;
    /* 维度 */
    List<LocationTempletRulesCommand> rules;

    private Boolean isModifiedallocateArea;
    private Boolean isModifiedWorkArea;
    private Boolean isModifiedShelfArea;

    private Long outerContainerId;
    private String occupationCode;

    /* 维度 */
    private String dimension1;
    private String dimension2;
    private String dimension3;
    private String dimension4;
    private String dimension5;
    private String dimension6;
    private String dimension7;
    private String dimension8;
    /** 模糊查询用来限制查询条数 */
    private Integer lineNum;
    /** 是否模糊查询;0:否，1：是 */
    private Integer isFuzzy;
    /** 可用的混放数量 */
    private Integer availableMixCount;
    /** 是否静态库位 */
    private Boolean isStatic;
    /** 是否占用库存 */
    private Boolean isOccupiedInventory;

    /* 上限 */
    private String upBound;
    /* 下限 */
    private String downBound;

    private Long asnId;

    /** 长度 */
    private java.lang.Double length;
    /** 宽 */
    private java.lang.Double width;
    /** 高 */
    private java.lang.Double high;
    /** 体积 */
    private java.lang.Double volume;
    /** 重量 */
    private java.lang.Double weight;
    /** 库位尺寸类型 */
    private java.lang.String sizeType;
    /** 托盘数 */
    private java.lang.Integer trayCount;
    /** 最大混放sku属性数 */
    private java.lang.Long maxChaosSku;
    /** 体积利用率 */
    private java.lang.Double volumeRate;

    /** 长度单位 */
    private String lengthUom;

    /** 体积单位 */
    private String volumeUom;

    /** 重量单位 */
    private String weightUom;

    public Boolean getIsOccupiedInventory() {
        return isOccupiedInventory;
    }

    public void setIsOccupiedInventory(Boolean isOccupiedInventory) {
        this.isOccupiedInventory = isOccupiedInventory;
    }

    public Boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Integer getIsFuzzy() {
        return isFuzzy;
    }

    public void setIsFuzzy(Integer isFuzzy) {
        this.isFuzzy = isFuzzy;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSysCompileCode() {
        return sysCompileCode;
    }

    public void setSysCompileCode(String sysCompileCode) {
        this.sysCompileCode = sysCompileCode;
    }

    public String getManualCode() {
        return manualCode;
    }

    public void setManualCode(String manualCode) {
        this.manualCode = manualCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getShelfAreaId() {
        return shelfAreaId;
    }

    public void setShelfAreaId(Long shelfAreaId) {
        this.shelfAreaId = shelfAreaId;
    }

    public Long getAllocateAreaId() {
        return allocateAreaId;
    }

    public void setAllocateAreaId(Long allocateAreaId) {
        this.allocateAreaId = allocateAreaId;
    }

    public Long getWorkAreaId() {
        return workAreaId;
    }

    public void setWorkAreaId(Long workAreaId) {
        this.workAreaId = workAreaId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Boolean getIsMixStacking() {
        return isMixStacking;
    }

    public void setIsMixStacking(Boolean isMixStacking) {
        this.isMixStacking = isMixStacking;
    }

    public Integer getMixStackingNumber() {
        return mixStackingNumber;
    }

    public void setMixStackingNumber(Integer mixStackingNumber) {
        this.mixStackingNumber = mixStackingNumber;
    }

    public Boolean getIsReplenishmentBarcode() {
        return isReplenishmentBarcode;
    }

    public void setIsReplenishmentBarcode(Boolean isReplenishmentBarcode) {
        this.isReplenishmentBarcode = isReplenishmentBarcode;
    }

    public Boolean getIsValidMgt() {
        return isValidMgt;
    }

    public void setIsValidMgt(Boolean isValidMgt) {
        this.isValidMgt = isValidMgt;
    }

    public Boolean getIsBatchMgt() {
        return isBatchMgt;
    }

    public void setIsBatchMgt(Boolean isBatchMgt) {
        this.isBatchMgt = isBatchMgt;
    }

    public Boolean getIsTrackVessel() {
        return isTrackVessel;
    }

    public void setIsTrackVessel(Boolean isTrackVessel) {
        this.isTrackVessel = isTrackVessel;
    }

    public String getTempletCode() {
        return templetCode;
    }

    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getTempletName() {
        return templetName;
    }

    public void setTempletName(String templetName) {
        this.templetName = templetName;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public String getShelfArea() {
        return shelfArea;
    }

    public void setShelfArea(String shelfArea) {
        this.shelfArea = shelfArea;
    }

    public String getAllocateArea() {
        return allocateArea;
    }

    public void setAllocateArea(String allocateArea) {
        this.allocateArea = allocateArea;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsTemporaryLocation() {
        return isTemporaryLocation;
    }

    public void setIsTemporaryLocation(Boolean isTemporaryLocation) {
        this.isTemporaryLocation = isTemporaryLocation;
    }

    public String getShelfSort() {
        return shelfSort;
    }

    public void setShelfSort(String shelfSort) {
        this.shelfSort = shelfSort;
    }

    public String getPickSort() {
        return pickSort;
    }

    public void setPickSort(String pickSort) {
        this.pickSort = pickSort;
    }

    public String getReplenishmentBarcode() {
        return replenishmentBarcode;
    }

    public void setReplenishmentBarcode(String replenishmentBarcode) {
        this.replenishmentBarcode = replenishmentBarcode;
    }

    public String getTemporaryCode() {
        return temporaryCode;
    }

    public void setTemporaryCode(String temporaryCode) {
        this.temporaryCode = temporaryCode;
    }

    public String getLatestSkuCode() {
        return latestSkuCode;
    }

    public void setLatestSkuCode(String latestSkuCode) {
        this.latestSkuCode = latestSkuCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }



    public Long getTempletId() {
        return templetId;
    }

    public void setTempletId(Long templetId) {
        this.templetId = templetId;
    }

    public List<LocationTempletRulesCommand> getRules() {
        return rules;
    }

    public void setRules(List<LocationTempletRulesCommand> rules) {
        this.rules = rules;
    }

    public Boolean getIsModifiedallocateArea() {
        return isModifiedallocateArea;
    }

    public void setIsModifiedallocateArea(Boolean isModifiedallocateArea) {
        this.isModifiedallocateArea = isModifiedallocateArea;
    }

    public Boolean getIsModifiedWorkArea() {
        return isModifiedWorkArea;
    }

    public void setIsModifiedWorkArea(Boolean isModifiedWorkArea) {
        this.isModifiedWorkArea = isModifiedWorkArea;
    }

    public Boolean getIsModifiedShelfArea() {
        return isModifiedShelfArea;
    }

    public void setIsModifiedShelfArea(Boolean isModifiedShelfArea) {
        this.isModifiedShelfArea = isModifiedShelfArea;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public String getDimension3() {
        return dimension3;
    }

    public void setDimension3(String dimension3) {
        this.dimension3 = dimension3;
    }

    public String getDimension4() {
        return dimension4;
    }

    public void setDimension4(String dimension4) {
        this.dimension4 = dimension4;
    }

    public String getDimension5() {
        return dimension5;
    }

    public void setDimension5(String dimension5) {
        this.dimension5 = dimension5;
    }

    public String getDimension6() {
        return dimension6;
    }

    public void setDimension6(String dimension6) {
        this.dimension6 = dimension6;
    }

    public String getDimension7() {
        return dimension7;
    }

    public void setDimension7(String dimension7) {
        this.dimension7 = dimension7;
    }

    public String getDimension8() {
        return dimension8;
    }

    public void setDimension8(String dimension8) {
        this.dimension8 = dimension8;
    }

    public Integer getAvailableMixCount() {
        return availableMixCount;
    }

    public void setAvailableMixCount(Integer availableMixCount) {
        this.availableMixCount = availableMixCount;
    }

    public String getUpBound() {
        return upBound;
    }

    public void setUpBound(String upBound) {
        this.upBound = upBound;
    }

    public String getDownBound() {
        return downBound;
    }

    public void setDownBound(String downBound) {
        this.downBound = downBound;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Long getAsnId() {
        return asnId;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public java.lang.Double getLength() {
        return length;
    }

    public void setLength(java.lang.Double length) {
        this.length = length;
    }

    public java.lang.Double getWidth() {
        return width;
    }

    public void setWidth(java.lang.Double width) {
        this.width = width;
    }

    public java.lang.Double getHigh() {
        return high;
    }

    public void setHigh(java.lang.Double high) {
        this.high = high;
    }

    public java.lang.Double getVolume() {
        return volume;
    }

    public void setVolume(java.lang.Double volume) {
        this.volume = volume;
    }

    public java.lang.Double getWeight() {
        return weight;
    }

    public void setWeight(java.lang.Double weight) {
        this.weight = weight;
    }

    public String getLengthUom() {
        return lengthUom;
    }

    public void setLengthUom(String lengthUom) {
        this.lengthUom = lengthUom;
    }

    public String getVolumeUom() {
        return volumeUom;
    }

    public void setVolumeUom(String volumeUom) {
        this.volumeUom = volumeUom;
    }

    public String getWeightUom() {
        return weightUom;
    }

    public void setWeightUom(String weightUom) {
        this.weightUom = weightUom;
    }

    public java.lang.Integer getTrayCount() {
        return trayCount;
    }

    public void setTrayCount(java.lang.Integer trayCount) {
        this.trayCount = trayCount;
    }

    public java.lang.Long getMaxChaosSku() {
        return maxChaosSku;
    }

    public void setMaxChaosSku(java.lang.Long maxChaosSku) {
        this.maxChaosSku = maxChaosSku;
    }

    public java.lang.String getSizeType() {
        return sizeType;
    }

    public void setSizeType(java.lang.String sizeType) {
        this.sizeType = sizeType;
    }

    public java.lang.Double getVolumeRate() {
        return volumeRate;
    }

    public void setVolumeRate(java.lang.Double volumeRate) {
        this.volumeRate = volumeRate;
    }


}
