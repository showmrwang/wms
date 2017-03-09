package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.pda.work.InventoryMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaInventoryMoveWorkEntranceManager extends BaseManager {

    /**
     * [通用方法] 库内移动工作查询
     * @param command
     * @return
     */
    public InventoryMoveWorkCommand retrieveInventoryMoveWorkList(InventoryMoveWorkCommand command, Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * [业务方法] 库内移动工作-查询获取工作方法
     * @param funcId
     * @param ouId
     * @return
     */
    public InventoryMoveWorkCommand getObtainWorkWay(InventoryMoveWorkCommand command, Long funcId, Long ouId);

}
