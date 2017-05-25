package com.baozun.scm.primservice.whinterface.manager;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsInvoiceConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundStatusConfirm;

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
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param customerCode not null 客户编码
     * @return
     */
    List<WmsSkuInventoryFlow> wmsSkuInventoryFlow(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String customerCode);

    /**
     * 同步出库单状态反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsOutBoundStatusConfirm> wmsOutBoundStatusConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource);

    /**
     * 同步出库单反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource DEFAULT null 数据来源 区分上位系统
     * @return
     */
    List<WmsOutBoundConfirm> wmsOutBoundConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource);

    /**
     * 发票信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    List<WmsInvoiceConfirm> wmsInvoiceConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource);

    /**
     * 入库单信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param dataSource DEFAULT null 数据来源 区分上位系统
     * @return
     */
    List<WmsInBoundConfirm> wmsInBoundConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String dataSource);
}
