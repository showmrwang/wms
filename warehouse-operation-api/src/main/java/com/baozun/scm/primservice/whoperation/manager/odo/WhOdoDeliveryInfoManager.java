package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdodeliveryInfoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

public interface WhOdoDeliveryInfoManager extends BaseManager {

    /**
     * [业务方法] 通过出库单号查找没有绑定出库箱的运单信息
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdodeliveryInfo> findByOdoIdWithoutOutboundbox(Long odoId, Long ouId);

    /**
     * [通用方法] 更新运单表
     * 
     * @param whOdodeliveryInfo
     * @return
     */
    WhOdodeliveryInfo saveOrUpdate(WhOdodeliveryInfo whOdodeliveryInfo);

    /**
     * [通用方法] 查找运单表
     * 
     * @param whOdodeliveryInfo
     * @return
     */
    List<WhOdodeliveryInfo> findByParams(WhOdodeliveryInfo whOdodeliveryInfo);


    /**
     * 查询出库单下所有的交接信息
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    public List<WhOdodeliveryInfo> findListByOdoId(Long odoId, Long ouId);

    /**
     * 查询出库单下可用的运单
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    public WhOdodeliveryInfo findUseableWaybillInfoByOdoId(Long odoId, Long ouId);

    /**
     * 运单号是否已被使用
     * 
     * @param waybillCode
     * @param ouId
     * @return
     */
    public Boolean checkUniqueWaybillCode(String waybillCode, Long ouId);

    WhOdodeliveryInfo findByWaybillCode(String outboundboxCode, Long ouId);

    Pagination<WhOdodeliveryInfoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

}
