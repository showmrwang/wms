package com.baozun.scm.primservice.whoperation.manager.confirm.inbound;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundConfirm;

public interface WhInboundConfirmManager extends BaseManager {

    /**
     * 通过创建时间段+数据来源获取对应入库单反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param dataSource
     * @return
     */
    List<WhInboundConfirm> findWhInboundConfirmByCreateTimeAndDataSource(String beginTime, String endTime, String dataSource);

}
