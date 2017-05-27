package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentScanResultComamnd extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**库位id*/
    private Long locationId;
    /**周转箱id*/
    private Long turnoverBoxId;
    /**周转箱id*/
    private Long palletId;
    /**周转箱id*/
    private Long containerId;
    /**是否需要扫描库位*/
    private Boolean isNeedScanLocation;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanTurnoverBox;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanPallet;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanContainer;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getTurnoverBoxId() {
        return turnoverBoxId;
    }

    public void setTurnoverBoxId(Long turnoverBoxId) {
        this.turnoverBoxId = turnoverBoxId;
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

    public Long getPalletId() {
        return palletId;
    }

    public void setPalletId(Long palletId) {
        this.palletId = palletId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public Boolean getIsNeedScanPallet() {
        return isNeedScanPallet;
    }

    public void setIsNeedScanPallet(Boolean isNeedScanPallet) {
        this.isNeedScanPallet = isNeedScanPallet;
    }

    public Boolean getIsNeedScanContainer() {
        return isNeedScanContainer;
    }

    public void setIsNeedScanContainer(Boolean isNeedScanContainer) {
        this.isNeedScanContainer = isNeedScanContainer;
    }
}
