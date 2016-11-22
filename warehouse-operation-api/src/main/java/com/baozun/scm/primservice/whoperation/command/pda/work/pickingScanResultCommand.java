package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
/**
 * 
 * @author tangming
 *
 */
public class pickingScanResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**提示小车*/
    private String tipOuterContainerCode;
    
    /**提示出库箱*/
    private String tipOutBoundCode;
    
    /**提示周转箱*/
    private String tipContainerCode;

    public String getTipOuterContainerCode() {
        return tipOuterContainerCode;
    }

    public void setTipOuterContainerCode(String tipOuterContainerCode) {
        this.tipOuterContainerCode = tipOuterContainerCode;
    }

    public String getTipOutBoundCode() {
        return tipOutBoundCode;
    }

    public void setTipOutBoundCode(String tipOutBoundCode) {
        this.tipOutBoundCode = tipOutBoundCode;
    }

    public String getTipContainerCode() {
        return tipContainerCode;
    }

    public void setTipContainerCode(String tipContainerCode) {
        this.tipContainerCode = tipContainerCode;
    }
    
  
}
