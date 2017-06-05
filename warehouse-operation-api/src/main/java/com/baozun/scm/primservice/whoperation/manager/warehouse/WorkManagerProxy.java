package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;

public interface WorkManagerProxy extends BaseManager {

    /**
     * 签出
     * 
     * @param workIds
     * @param userId
     * @param ouId
     * @return
     */
    ResponseMsg assignOutOperation(List<Long> workIds, Long userId, Long ouId);

    /**
     * 取消签出
     * 
     * @param workIds
     * @param userId
     * @param ouId
     */
    void cancelAssignOutOperation(List<Long> workIds, Long userId, Long ouId);

    /**
     * 签入
     * 
     * @param assignBatch
     * @param userId
     * @param ouId
     * @return
     */
    ResponseMsg assignInOperation(String assignBatch, Long userId, Long ouId);
}
