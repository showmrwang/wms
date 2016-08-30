package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;

public interface OdoManager extends BaseManager {
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]创建出库单TODO
     * 
     * @param odo
     * @param transportMgmt
     */
    void createOdo(WhOdo odo, WhOdoTransportMgmt transportMgmt);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findOdoByIdOuId(Long id, Long ouId);

    /**
     * [通用商品]查找出库单是否包含某种商品
     * 
     * @param odoId
     * @param skuId
     * @param ouId
     * @return
     */
    int existsSkuInOdo(Long odoId, Long skuId, Long ouId);

    /**
     * [业务方法]创建明细
     * 
     * @param line
     * @param odo
     */
    void saveUnit(WhOdoLine line, WhOdo odo);

    /**
     * [业务方法]配置配货对象
     * 
     * @param odoAddress
     * @param transportMgmt
     * @param odo
     */
    void saveDistributionUnit(WhOdoAddress odoAddress, WhOdo odo);
}
