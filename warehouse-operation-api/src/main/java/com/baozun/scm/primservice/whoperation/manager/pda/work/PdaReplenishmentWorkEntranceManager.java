package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaReplenishmentWorkEntranceManager extends BaseManager {

    /**
     * [通用方法] 补货工作查询
     * @param command
     * @return
     */
    public ReplenishmentWorkCommand retrieveReplenishmentWorkList(ReplenishmentWorkCommand command, Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * [业务方法] 补货工作-查询获取工作方法
     * @param funcId
     * @param ouId
     * @return
     */
    public ReplenishmentWorkCommand getObtainWorkWay(ReplenishmentWorkCommand command, Long funcId, Long ouId);

}
