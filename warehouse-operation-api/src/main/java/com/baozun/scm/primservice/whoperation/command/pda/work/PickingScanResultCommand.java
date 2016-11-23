package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class PickingScanResultCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = 5696756957904723061L;
    
    /**
     * 提示外部容器号(小车,外部容器)
     */
    private String tipOuterContainerCode;
    
    /** 提示内部容器号(周转箱)*/
    private String tipInsideContainerCode;
    /**提示出库箱号*/
    private String tipOutBoundCode;
    
    private Long operatorId;
    
    private Boolean isRemmendContainer = true;   //是否是推荐容器拣货,true是推荐容器拣货,false是整箱整托拣货   , 默认推荐容器拣货
    

    /**外部容器号(外部容器，小车)*/
    private String outerContainerCode;
    
    /**内部容器号(周转箱，内部容器)*/
    private String insideContainerCode;
    
    /**出库箱号*/
    private String outBoundCode;
    /**仓库id*/
    private Long ouId;
    
    private Integer pickingWay;

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

    
    
    
}
