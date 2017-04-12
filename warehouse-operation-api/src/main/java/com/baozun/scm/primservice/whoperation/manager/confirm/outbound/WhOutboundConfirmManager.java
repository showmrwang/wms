package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOutboundConfirmManager extends BaseManager {

    /**
     * 生成出库单反馈数据
     * 
     * @param whOdo
     */
    void saveWhOutboundConfirm(WhOdo whOdo);

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应出库单反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param ouid
     * @param dataSource
     * @return
     */
    List<WhOutboundConfirm> findWhOutboundConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource);

}
