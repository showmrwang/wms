package com.baozun.scm.primservice.whoperation.manager.pda.putaway;

import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaManMadePutawayManager extends BaseManager{

   /**
    * 验证容器号
    * 
    * @author lijun.shen
    * @param pdaManMadePutawayCommand
    * @return
    */
    PdaManMadePutawayCommand pdaScanContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand);

    
    /**
     * 验证货箱容器号
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     * @return
     */
    PdaManMadePutawayCommand pdaScanBinContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand);

    

    /**
     * 验证库位号
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     * @return
     */
    PdaManMadePutawayCommand pdaScanLocation(PdaManMadePutawayCommand pdaManMadePutawayCommand);


    /**
     * 库位不允许混放逻辑
     * 
     * @author lijun.shen
     * @param command
     * @return
     */
    PdaManMadePutawayCommand pdaLocationNotMix(PdaManMadePutawayCommand command);

    /**
     * 库位允许混放逻辑
     * 
     * @author lijun.shen
     * @param command
     * @return
     */
    PdaManMadePutawayCommand pdaLocationIsMix(PdaManMadePutawayCommand command);




}
