package com.baozun.scm.primservice.whoperation.command.pda.work;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -8974897137006173615L;
    
    
    /**************************************************基础数据开始**************************************************/ 
    
    /** 补货方式 */
    private Integer replenishWay;

    
    /**************************************************基础数据结束**************************************************/
    
    /**************************************************非整托整箱开始************************************************/   
    
    /**************************************************非整托整箱结束************************************************/
    
    /**************************************************整托整箱开始**************************************************/   
    
    /**************************************************整托整箱结束**************************************************/
    
    public Integer getReplenishWay() {
        return replenishWay;
    }
    public void setReplenishWay(Integer replenishWay) {
        this.replenishWay = replenishWay;
    }
}
