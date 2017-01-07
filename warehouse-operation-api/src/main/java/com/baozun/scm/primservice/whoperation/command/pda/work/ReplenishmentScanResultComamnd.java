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
    /**是否需要扫描库位*/
    private Boolean isNeedScanLocation;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanTurnoverBox;

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
    
    
    
}
