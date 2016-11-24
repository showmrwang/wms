package com.baozun.scm.primservice.whoperation.command.pda.work;

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
    private String outerContainerCode;

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
    /**是否扫描库位*/
    private Boolean isScanLocation;
   
    /** 内部容器号 */
    private String insideContainerCode;

  
    /** 仓库id */
    private Long ouId;

    private Integer pickingWay;

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

    


}
