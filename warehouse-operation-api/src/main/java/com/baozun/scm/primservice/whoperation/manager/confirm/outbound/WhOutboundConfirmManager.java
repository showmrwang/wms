package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOutboundConfirmManager extends BaseManager {

    /**
     * 生成出库单反馈数据
     * 
     * @param whOdo
     */
    void saveWhOutboundConfirm(WhOdo whOdo);

}
