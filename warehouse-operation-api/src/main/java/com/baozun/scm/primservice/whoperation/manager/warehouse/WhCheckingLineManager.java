package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


/***
 * 复核明细
 * @author Administrator
 *
 */
public interface WhCheckingLineManager extends BaseManager{
    
    
    /**
     * 根据复合ID和仓库组织ID查询复核明细
     * @param checkingId
     * @param ouId
     * @return
     */
     List<WhCheckingLineCommand> getCheckingLineByCheckingId(Long checkingId, Long ouId);
     
     /**
      * 保存更新复核明细
      * 
      * @param whCheckingCommand
      */
     void saveOrUpdate(WhCheckingLineCommand whCheckingLineCommand);

}
