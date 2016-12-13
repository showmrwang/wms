package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface OdoMergeManagerProxy extends BaseManager {
    /**
     * 分页查询
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    Map<String, String> odoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId);

    List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus);

    /**
     * [业务方法] 合并出库单-取消合并
     * @param odoId
     * @param ouId
     * @param userId
     */
    void odoMergeCancel(Long odoId, Long ouId, Long userId);
}
