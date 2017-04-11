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
    private String tipLcoationBarCode;
    /** 工作条码 */
    private String workBarCode;
    /** 仓库id */
    private Long ouId;

    private Long userId;
    /** 是否扫描库位编码 */
    private Boolean isInboundLocationBarcode;
    /** 库位id */
    private Long locationId;
    /***/
    private String tipTurnoverBoxCode;
    
    private String turnoverBoxCode;
    
    /**是否需要扫描库位*/
    private Boolean isNeedScanLocation = false;
    /**是否需要扫描周转箱*/
    private Boolean isNeedScanTurnoverBox = false;
    /**是否扫描结束*/
    private Boolean isScanFinsh = false;
    /**是否整托盘整箱补货*/
    private Boolean isWholeCase = false;
    /**是否只有唯一目标库位*/
    private Boolean isOnlyLocation = false;
    

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

    public Boolean getIsInboundLocationBarcode() {
        return isInboundLocationBarcode;
    }

    public void setIsInboundLocationBarcode(Boolean isInboundLocationBarcode) {
        this.isInboundLocationBarcode = isInboundLocationBarcode;
    }

    public String getTipLocationCode() {
        return tipLocationCode;
    }

    public void setTipLocationCode(String tipLocationCode) {
        this.tipLocationCode = tipLocationCode;
    }

    public String getTipLcoationBarCode() {
        return tipLcoationBarCode;
    }

    public void setTipLcoationBarCode(String tipLcoationBarCode) {
        this.tipLcoationBarCode = tipLcoationBarCode;
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
    
    
}
