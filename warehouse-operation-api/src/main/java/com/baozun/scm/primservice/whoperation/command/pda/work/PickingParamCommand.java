package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author ming.tang
 *
 */
public class PickingParamCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = -4347041219228490682L;

    /**外部容器号(外部容器，小车)*/
    private String outerContainerCode;
    
    /**内部容器号(周转箱，内部容器)*/
    private String insideContainerCode;
    
    /**出库箱号*/
    private String outBoundCode;
    

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

    
    
}
