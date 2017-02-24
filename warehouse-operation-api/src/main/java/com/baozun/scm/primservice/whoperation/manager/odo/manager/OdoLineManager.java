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
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineById(List<Long> idList, Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idStrList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByIdStr(List<String> idStrList, Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByOdoId(List<Long> idList, Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByOdoIdOrderByPickingSort(List<Long> idList, Long ouId);



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

    /**
     * [通用方法]根据ODOID和OUID查找明细
     *
     * @param odoId
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineCommandListByOdoId(Long odoId, Long ouId);

    /**
     * [删除出库单明细]可批量
     * 
     * @param lineList
     * @param ouId
     * @param userId
     * @param logId
     */
    void deleteLines(List<WhOdoLine> lineList, Long ouId, Long userId, String logId);

    /**
     * [通用方法] 修改出库单明细状态,方法统一放在OdoManager中进行事务处理
     * @param odoLineId
     * @param ouId
     * @param status
     * @return
     */
    @Deprecated
    Boolean updateOdoLineStatus(Long odoLineId, Long ouId, String status);

    /**
     * [通用方法]根据波次查询出库单明细
     * 
     * @param code
     * @param ouId
     * @return
     */
    List<WhOdoLine> findOdoLineListByWaveCode(String code, Long ouId);

    /**
     * [通用方法]根据ODOID查找某种状态的明细
     * 
     * @param odoId
     * @param ouId
     * @param strings
     * @return
     */
    List<WhOdoLine> findOdoLineListByOdoIdStatus(Long odoId, Long ouId, String[] strings);

}
