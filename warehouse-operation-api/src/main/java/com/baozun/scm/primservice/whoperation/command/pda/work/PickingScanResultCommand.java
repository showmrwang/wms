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

}
