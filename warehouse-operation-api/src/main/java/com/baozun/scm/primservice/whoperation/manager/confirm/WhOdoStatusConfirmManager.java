package com.baozun.scm.primservice.whoperation.manager.confirm;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOdoStatusConfirmManager extends BaseManager {

    /***
     * 生成出库单状态反馈数据
     * 
     * @param whOdo
     * @return
     */
    int saveWhOdoStatusConfirm(WhOdo whOdo);

}
