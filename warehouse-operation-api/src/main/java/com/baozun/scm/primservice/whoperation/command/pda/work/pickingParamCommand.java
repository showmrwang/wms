package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
/**
 * 
 * @author tangming
 *
 */
public class pickingParamCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    /**外部容器号*/
    private String outerContainerCode;
    
    /**内部容器号*/
    private String insideContainerCode;

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
    
    
    
    
}
