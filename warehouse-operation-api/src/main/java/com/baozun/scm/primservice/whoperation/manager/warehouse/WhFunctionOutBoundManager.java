package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;

/***
 * pda补货
 * @author Administrator
 *
 */
public interface WhFunctionOutBoundManager extends BaseManager {

    
    /**
    * 查询出库功能
    * @param id
    * @param ou_id
    * @return
    */
    public WhFunctionOutBound findByFunctionIdExt(Long functionId, Long ouId);

}
