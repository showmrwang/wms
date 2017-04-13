package com.baozun.scm.primservice.whoperation.manager.confirm;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.confirm.WhInvoiceConfirm;

public interface WhInvoiceConfirmManager extends BaseManager {

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应发票反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param ouid
     * @param dataSource
     * @return
     */
    List<WhInvoiceConfirm> findWhInvoiceConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource);

}
