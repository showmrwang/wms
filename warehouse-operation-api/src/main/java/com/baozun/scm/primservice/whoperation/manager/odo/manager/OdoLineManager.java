package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;

public interface OdoLineManager extends BaseManager {

    /**
     * [通用方法]根据ODOLINEID和OUID查找ODOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoLine findOdoLineById(Long id, Long ouId);

    /**
     * [通用方法]出库单明细分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]根据ODOID和OUID查找明细数量
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    long findOdoLineListCountByOdoId(Long odoId, Long ouId);

    /**
     * [通用方法]根据ODOID和OUID查找明细
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdoLine> findOdoLineListByOdoId(Long odoId, Long ouId);

}
