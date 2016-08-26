package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;

public interface OdoManagerProxy extends BaseManager {
    /**
     * [业务方法]出库单一览
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法，接口方法]创建出库单
     * 
     * @param odoGroup
     * @return
     */
    ResponseMsg createOdoFromWms(OdoGroupCommand odoGroup);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findOdOById(Long id, Long ouId);

    /**
     * [通用方法]根据ODO_ID,OUID查找ODOTRANSPORTMGMT
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId);

    /**
     * [通用方法]根据ODOLINEID和OUID查找ODOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoLine findOdoLineById(Long id, Long ouId);

    /**
     * [业务方法]保存ODO明细
     * 
     * @param lineCommand
     */
    void saveOdoUnit(OdoLineCommand lineCommand);
}
