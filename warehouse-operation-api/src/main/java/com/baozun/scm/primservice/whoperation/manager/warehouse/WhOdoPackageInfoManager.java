package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;

public interface WhOdoPackageInfoManager extends BaseManager{
    
    /**
     * [通用方法] 创建作业明细信息
     * 
     * @param WhOperationLineCommand
     * @return
     */
    void saveOrUpdate(WhOdoPackageInfoCommand whOdoPackageInfoCommand);

    
    /**
     * [通用方法] 通过出出库箱编码, 组织id查找出库单打包信息
     * 
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
   public WhOdoPackageInfo findByOutboundBoxCode(String outboundboxCode,Long ouId);

}
