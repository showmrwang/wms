package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaPickingWorkEntranceManager extends BaseManager {

    /**
     * [通用方法] 拣货工作查询
     * @param command
     * @return
     */
    public PickingWorkCommand retrievePickingWorkList(PickingWorkCommand command, Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * [业务方法] 拣货工作-查询获取工作方法
     * @param funcId
     * @param ouId
     * @return
     */
    public PickingWorkCommand getObtainWorkWay(PickingWorkCommand command, Long funcId, Long ouId);

}
