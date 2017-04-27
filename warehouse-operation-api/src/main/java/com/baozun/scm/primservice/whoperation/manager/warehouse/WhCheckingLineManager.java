package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;


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
     
     /**
      * 修改、复合明细
      * @param line
      */
     public void saveOrUpdateByVersion(WhCheckingLine line);
     
     /**
      * 根据Id查询复合明细
      * @param id
      * @param ouId
      * @return
      */
     public WhCheckingLineCommand getCheckingLineById(Long id,Long ouId);
     
     /***判断当前是否是最后一箱
      * 
      * @param ouId
      * @param odoId
      * @return
      */
     public Boolean judeIsLastBox(Long ouId,Long odoId);

}
