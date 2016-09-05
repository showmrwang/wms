package com.baozun.scm.primservice.whoperation.manager.odo.merge;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface OdoMergeManager extends BaseManager {
    /**
     * 分页
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 合并出库订单
     * @param odoIds
     * @param options
     * @param ouId
     * @param userId
     * @return
     */
    Map<String, String> doOdoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId);

    /**
     * 返回合并成功列表
     * @param successIds
     * @param ouId
     * @return
     */
    List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus);
}
