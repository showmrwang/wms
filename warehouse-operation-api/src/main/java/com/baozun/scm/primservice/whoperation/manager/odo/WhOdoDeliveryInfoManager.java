package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

public interface WhOdoDeliveryInfoManager extends BaseManager {

    /**
     * [业务方法] 通过出库单号查找没有绑定出库箱的运单信息
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdodeliveryInfo> findByOdoIdWithoutOutboundbox(Long odoId, Long ouId);

    /**
     * [通用方法] 更新运单表
     * @param whOdodeliveryInfo
     * @return
     */
    WhOdodeliveryInfo saveOrUpdate(WhOdodeliveryInfo whOdodeliveryInfo);

    /**
     * [通用方法] 查找运单表
     * @param whOdodeliveryInfo
     * @return
     */
    List<WhOdodeliveryInfo> findByParams(WhOdodeliveryInfo whOdodeliveryInfo);

}
