package com.baozun.scm.primservice.whinterface.manager;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;

/**
 * WMS数据反馈接口
 *
 */
public interface WmsConfirmServiceManagerProxy {

    /**
     * 同步库存流失 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @return
     */
    List<WmsSkuInventoryFlow> wmsSkuInventoryFlow(Date beginTime, Date endTime, String whCode);
}
