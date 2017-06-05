package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.model.ResponseMsg;


@Service("workManagerProxy")
public class WorkManagerProxyImpl implements WorkManagerProxy {
    @Autowired
    private WhWorkManager whWorkManager;

    @Override
    public ResponseMsg assignOutOperation(List<Long> workIds, Long userId, Long ouId) {
        return this.whWorkManager.assignOutOperation(workIds, userId, ouId);
    }

    @Override
    public void cancelAssignOutOperation(List<Long> workIds, Long userId, Long ouId) {
        this.whWorkManager.cancelAssignOutOperation(workIds, userId, ouId);

    }

    @Override
    public ResponseMsg assignInOperation(String assignBatch, Long userId, Long ouId) {
        return this.whWorkManager.assignInOperation(assignBatch, userId, ouId);
    }

}
