package com.baozun.scm.primservice.whinterface.manager;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whinterface.model.inbound.WmsInboundConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsInvoiceConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundStatusConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutboundConfirm;

/**
 * WMS数据反馈接口
 *
 */
public interface WmsConfirmServiceManagerProxy {

    /**
     * 同步库存流水 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @param customerCode not null 客户编码
     * @return
     */
    List<WmsSkuInventoryFlow> wmsSkuInventoryFlow(Date beginTime, Date endTime, String whCode, String customerCode);

    /**
     * 同步出库单状态反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsOutBoundStatusConfirm> wmsOutBoundStatusConfirm(Date beginTime, Date endTime, String whCode, String dataSource);

    /**
     * 同步出库单反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsOutboundConfirm> wmsOutBoundConfirm(Date beginTime, Date endTime, String whCode, String dataSource);

    /**
     * 发票信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsInvoiceConfirm> wmsInvoiceConfirm(Date beginTime, Date endTime, String whCode, String dataSource);

    /**
     * 入库单信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsInboundConfirm> wmsInBoundConfirm(Date beginTime, Date endTime, String dataSource);
}
