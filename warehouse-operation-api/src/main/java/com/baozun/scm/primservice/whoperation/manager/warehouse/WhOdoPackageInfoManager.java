package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WhOdoPackageInfoManager extends BaseManager{
    
    /**
     * [通用方法] 创建作业明细信息
     * 
     * @param WhOperationLineCommand
     * @return
     */
    void saveOrUpdate(WhOdoPackageInfoCommand whOdoPackageInfoCommand);

}
